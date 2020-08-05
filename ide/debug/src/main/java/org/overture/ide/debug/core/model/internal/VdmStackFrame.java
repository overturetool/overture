/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.model.internal;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.osgi.util.NLS;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugManager;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.IDbgpStackLevel;
import org.overture.ide.debug.core.dbgp.commands.IDbgpContextCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpDebuggingEngineException;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.IRefreshableVdmVariable;
import org.overture.ide.debug.core.model.ISourceOffsetLookup;
import org.overture.ide.debug.core.model.IVdmStack;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmVariable;
import org.overture.ide.debug.logging.LogItem;

public class VdmStackFrame extends VdmDebugElement implements IVdmStackFrame
{

	private final IVdmThread thread;
	private IDbgpStackLevel level;
	private final IVdmStack stack;

	private VdmVariableContainer variables = null;
	private boolean needRefreshVariables = false;

	protected static IVdmVariable[] readVariables(VdmStackFrame parentFrame,
			int contextId, IDbgpContextCommands commands) throws DbgpException
	{

		try
		{

			IDbgpProperty[] properties = commands.getContextProperties(parentFrame.getLevel(), contextId);

			IVdmVariable[] variables = new IVdmVariable[properties.length];

			// Workaround for bug 215215
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=215215
			// Remove this code when Tcl active state debugger fixed
			Set<String> duplicates = findDuplicateNames(properties);

			for (int i = 0; i < properties.length; ++i)
			{
				IDbgpProperty property = properties[i];
				String name = property.getName();
				if (duplicates.contains(name))
				{
					name = property.getEvalName();
				}
				variables[i] = new VdmVariable(parentFrame, name, property);
			}

			return variables;
		} catch (DbgpDebuggingEngineException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				e.printStackTrace();
			}
			return new IVdmVariable[0];
		}
	}

	private static Set<String> findDuplicateNames(IDbgpProperty[] properties)
	{
		final Set<String> duplicates = new HashSet<String>();
		final Set<String> alreadyExsisting = new HashSet<String>();
		for (int i = 0; i < properties.length; ++i)
		{
			final IDbgpProperty property = properties[i];
			final String name = property.getName();
			if (!alreadyExsisting.add(name))
			{
				duplicates.add(name);
			}
		}
		return duplicates;
	}

	protected VdmVariableContainer readAllVariables() throws DbgpException
	{
		final IDbgpContextCommands commands = thread.getDbgpSession().getCoreCommands();

		((VdmThread) this.thread).getVdmDebugTarget().printLog(new LogItem(((VdmThread) this.thread).getDbgpSession().getInfo(), "REQUEST", true, "getContextNames"));
		final Map<Integer, String> names = commands.getContextNames(getLevel());
		((VdmThread) this.thread).getVdmDebugTarget().printLog(new LogItem(((VdmThread) this.thread).getDbgpSession().getInfo(), "RESPONSE", false, "getContextNames"));
		final VdmVariableContainer result = new VdmVariableContainer();
		if (thread.retrieveLocalVariables()
				&& names.containsKey(new Integer(IDbgpContextCommands.LOCAL_CONTEXT_ID)))
		{
			result.locals = readVariables(this, IDbgpContextCommands.LOCAL_CONTEXT_ID, commands);
			((VdmThread) this.thread).getVdmDebugTarget().printLog(new LogItem(((VdmThread) this.thread).getDbgpSession().getInfo(), "RESPONSE", false, "Read local variables"));
		}
		if (thread.retrieveGlobalVariables()
				&& names.containsKey(new Integer(IDbgpContextCommands.GLOBAL_CONTEXT_ID)))
		{
			result.globals = readVariables(this, IDbgpContextCommands.GLOBAL_CONTEXT_ID, commands);
			((VdmThread) this.thread).getVdmDebugTarget().printLog(new LogItem(((VdmThread) this.thread).getDbgpSession().getInfo(), "RESPONSE", false, "Read Global variables"));
		}
		if (thread.retrieveClassVariables()
				&& names.containsKey(new Integer(IDbgpContextCommands.CLASS_CONTEXT_ID)))
		{
			result.classes = readVariables(this, IDbgpContextCommands.CLASS_CONTEXT_ID, commands);
			((VdmThread) this.thread).getVdmDebugTarget().printLog(new LogItem(((VdmThread) this.thread).getDbgpSession().getInfo(), "RESPONSE", false, "Read Class variables"));
		}
		return result;
	}

	private static class VdmVariableContainer
	{
		IVariable[] locals = null;
		IVariable[] globals = null;
		IVariable[] classes = null;
		VdmVariableWrapper globalsWrapper = null;
		VdmVariableWrapper classesWrapper = null;

		VdmVariableContainer sort(IDebugTarget target)
		{
			final Comparator<Object> variableComparator = VdmDebugManager.getInstance().getVariableNameComparator();
			if (locals != null)
			{
				Arrays.sort(locals, variableComparator);
			}
			if (globals != null)
			{
				Arrays.sort(globals, variableComparator);
			}
			if (classes != null)
			{
				Arrays.sort(classes, variableComparator);
			}
			return this;
		}

		private int size()
		{
			int size = 0;
			if (locals != null)
			{
				size += locals.length;
			}
			if (globals != null && globals.length > 0)
			{
				++size;
			}
			if (classes != null && classes.length > 0)
			{
				++size;
			}
			return size;
		}

		IVdmVariable[] toArray(IDebugTarget target)
		{
			final int size = size();
			final IVdmVariable[] result = new IVdmVariable[size];
			if (size != 0)
			{
				int index = 0;
				if (globals != null && globals.length > 0)
				{
					if (globalsWrapper == null)
					{
						globalsWrapper = new VdmVariableWrapper(target, "Global Variables", globals);
					} else
					{
						globalsWrapper.refreshValue(globals);
					}
					result[index++] = globalsWrapper;
				}
				if (classes != null && classes.length > 0)
				{
					if (classesWrapper == null)
					{
						classesWrapper = new VdmVariableWrapper(target, "Instance Variables", classes);
					} else
					{
						classesWrapper.refreshValue(classes);
					}
					result[index++] = classesWrapper;
				}
				if (locals != null)
				{
					System.arraycopy(locals, 0, result, index, locals.length);
					index += locals.length;
				}
			}
			return result;
		}

		/**
		 * @return
		 */
		public boolean hasVariables()
		{
			return locals != null && locals.length != 0 || classes != null
					|| globals != null;
		}

		/**
		 * @param varName
		 * @return
		 * @throws DebugException
		 */
		public IVariable findVariable(String varName) throws DebugException
		{
			if (locals != null)
			{
				final IVariable variable = findVariable(varName, locals);
				if (variable != null)
				{
					return variable;
				}
			}
			if (globals != null)
			{
				final IVariable variable = findVariable(varName, globals);
				if (variable != null)
				{
					return variable;
				}
			}
			return null;
		}

		private static IVariable findVariable(String varName, IVariable[] vars)
				throws DebugException
		{
			for (int i = 0; i < vars.length; i++)
			{
				final IVariable var = vars[i];
				if (var.getName().equals(varName))
				{
					return var;
				}
			}
			return null;
		}
	}

	public VdmStackFrame(IVdmStack stack, IDbgpStackLevel stackLevel)
	{

		this.stack = stack;
		this.thread = stack.getThread();
		this.level = stackLevel;
	}

	public synchronized void updateVariables()
	{
		this.variables = null;
		try
		{
			checkVariablesAvailable();
		} catch (DebugException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IVdmStack getStack()
	{
		return stack;
	}

	/**
	 * @return
	 * @deprecated use #getSourceURI()
	 */
	public URI getFileName()
	{
		return level.getFileURI();
	}

	private static final int MULTI_LINE_COUNT = 2;

	public int getCharStart() throws DebugException
	{
		final int beginLine = level.getBeginLine();
		if (beginLine > 0)
		{
			final int endLine = level.getEndLine();
			if (endLine > 0 && endLine >= beginLine)
			{
				final ISourceOffsetLookup offsetLookup = VdmDebugPlugin.getSourceOffsetLookup();
				if (offsetLookup != null)
				{
					return offsetLookup.calculateOffset(this, beginLine, level.getBeginColumn(), false);
				}
			}
		}
		return -1;
	}

	public int getCharEnd() throws DebugException
	{
		final int endLine = level.getEndLine();
		if (endLine > 0)
		{
			final int beginLine = level.getBeginLine();
			if (beginLine > 0 && endLine >= beginLine)
			{
				final ISourceOffsetLookup offsetLookup = VdmDebugPlugin.getSourceOffsetLookup();
				if (offsetLookup != null)
				{
					if (endLine < beginLine + MULTI_LINE_COUNT)
					{
						final int offset = offsetLookup.calculateOffset(this, endLine, level.getEndColumn(), true);
						if (offset >= 0)
						{
							return offset + 1;
						}
					} else
					{
						final int offset = offsetLookup.calculateOffset(this, beginLine, -1, true);
						if (offset >= 0)
						{
							return offset + 1;
						}
					}
				}
			}
		}
		return -1;
	}

	public int getLineNumber() throws DebugException
	{
		return level.getLineNumber();
	}

	public int getBeginLine()
	{
		return level.getBeginLine();
	}

	public int getBeginColumn()
	{
		return level.getBeginColumn();
	}

	public int getEndLine()
	{
		return level.getEndLine();
	}

	public int getEndColumn()
	{
		return level.getEndColumn();
	}

	public String getWhere()
	{
		return level.getWhere().trim();
	}

	public String getName() throws DebugException
	{
		// String name = level.getWhere().trim();
		//
		// if (name == null || name.length() == 0) {
		// name = toString();
		// }
		//
		//		name += " (" + level.getFileURI().getPath() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		try {
			return getOnlyFileName() + " line: " + getLineNumber();
		} catch (CoreException e) {
			e.printStackTrace();
			throw new DebugException(e.getStatus());
		}
	}

	public String getOnlyFileName() throws CoreException
	{
		ILaunchConfiguration configuration = this.getDebugTarget().getLaunch().getLaunchConfiguration();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, ""));
		URI prp = project.getLocationURI();
		URI fp = level.getFileURI();
		URI rep = prp.relativize(fp);
		return rep.getPath();
	}

	public boolean hasRegisterGroups() throws DebugException
	{
		return false;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		return new IRegisterGroup[0];
	}

	public IThread getThread()
	{
		return thread;
	}

	public synchronized boolean hasVariables() throws DebugException
	{
		checkVariablesAvailable();
		return variables.hasVariables();
	}

	private synchronized void checkVariablesAvailable() throws DebugException
	{
		try
		{
			if (variables == null)
			{
				variables = readAllVariables();

				variables.sort(getDebugTarget());
			} else if (needRefreshVariables)
			{
				try
				{
					refreshVariables();
				} finally
				{
					needRefreshVariables = false;
				}
			}
		} catch (DbgpException e)
		{
			variables = new VdmVariableContainer();
			final Status status = new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, "Unable To Load Variables", e);
			VdmDebugPlugin.log(status);
			throw new DebugException(status);
		}
	}

	/**
	 * @throws DebugException
	 * @throws DbgpException
	 */
	private void refreshVariables() throws DebugException, DbgpException
	{
		final VdmVariableContainer newVars = readAllVariables();
		newVars.sort(getDebugTarget());
		variables.locals = refreshVariables(newVars.locals, variables.locals);
		variables.globals = refreshVariables(newVars.globals, variables.globals);
		variables.classes = refreshVariables(newVars.classes, variables.classes);
	}

	/**
	 * @param newVars
	 * @param oldVars
	 * @return
	 * @throws DebugException
	 */
	static IVariable[] refreshVariables(IVariable[] newVars, IVariable[] oldVars)
			throws DebugException
	{
		if (oldVars != null)
		{
			final Map<String, IVariable> map = new HashMap<String, IVariable>();
			for (int i = 0; i < oldVars.length; ++i)
			{
				final IVariable variable = oldVars[i];
				if (variable instanceof IRefreshableVdmVariable)
				{
					map.put(variable.getName(), variable);
				}
			}
			for (int i = 0; i < newVars.length; ++i)
			{
				final IVariable variable = newVars[i];
				final IRefreshableVdmVariable old;
				old = (IRefreshableVdmVariable) map.get(variable.getName());
				if (old != null)
				{
					newVars[i] = old.refreshVariable(variable);
				}
			}
		}
		return newVars;
	}

	public synchronized IVariable[] getVariables() throws DebugException
	{
		checkVariablesAvailable();
		return variables.toArray(getDebugTarget());
	}

	// IStep
	public boolean canStepInto()
	{
		return thread.canStepInto();
	}

	public boolean canStepOver()
	{
		return thread.canStepOver();
	}

	public boolean canStepReturn()
	{
		return thread.canStepReturn();
	}

	public boolean isStepping()
	{
		return thread.isStepping();
	}

	public void stepInto() throws DebugException
	{
		thread.stepInto();
	}

	public void stepOver() throws DebugException
	{
		thread.stepOver();
	}

	public void stepReturn() throws DebugException
	{
		thread.stepReturn();
	}

	// ISuspenResume
	public boolean canResume()
	{
		return thread.canResume();
	}

	public boolean canSuspend()
	{
		return thread.canSuspend();
	}

	public boolean isSuspended()
	{
		return thread.isSuspended();
	}

	public void resume() throws DebugException
	{
		thread.resume();
	}

	public void suspend() throws DebugException
	{
		thread.suspend();
	}

	// ITerminate
	public boolean canTerminate()
	{
		return thread.canTerminate();
	}

	public boolean isTerminated()
	{
		return thread.isTerminated();
	}

	public void terminate() throws DebugException
	{
		thread.terminate();
	}

	// IDebugElement
	public IDebugTarget getDebugTarget()
	{
		return thread.getDebugTarget();
	}

	public synchronized IVdmVariable findVariable(String varName)
			throws DebugException
	{
		checkVariablesAvailable();
		return (IVdmVariable) variables.findVariable(varName);
	}

	public int getLevel()
	{
		return level.getLevel();
	}

	public String toString()
	{
		return NLS.bind("stackFrame", new Integer(level.getLevel()));
	}

	public String getSourceLine()
	{
		return level.getWhere();
	}

	public URI getSourceURI()
	{
		return level.getFileURI();
	}

	public IVdmThread getVdmThread()
	{
		return (IVdmThread) getThread();
	}

	/**
	 * @param frame
	 * @param depth
	 * @return
	 */
	public VdmStackFrame bind(IDbgpStackLevel newLevel)
	{
		if (level.isSameMethod(newLevel))
		{
			level = newLevel;
			needRefreshVariables = true;
			return this;
		}
		return new VdmStackFrame(stack, newLevel);
	}
}
