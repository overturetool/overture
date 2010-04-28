package org.overture.ide.debug.core.model;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.model.VdmDebugState.DebugState;
import org.overture.ide.debug.utils.communication.DBGPProxyException;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;

public class VdmStackFrame extends VdmDebugElement implements IStackFrame
{

	private int charEnd = -1;
	private int charStart = -1;
	private int lineNumber;
	public int level;
	private String name;
	private String where;
	private VdmThread thread;
	public DebugThreadProxy proxy;
	boolean nameIsFileUri = false;
	private IVariable[] variables = null;

	public VdmStackFrame(VdmDebugTarget target, String name,
			boolean nameIsFileUri, int charStart, int charEnd, int lineNumber,
			int level, String where) {
		super(target);
		this.charEnd = charEnd;
		this.charStart = charStart;
		this.lineNumber = lineNumber;
		this.name = name;
		this.level = level;
		this.nameIsFileUri = nameIsFileUri;
		this.where = where;
	}

	public void setDebugTarget(VdmDebugTarget target)
	{
		super.fTarget = target;
	}

	public VdmDebugTarget getDebugTarget()
	{
		return super.fTarget;
	}

	public void setThread(VdmThread thread, DebugThreadProxy proxy)
	{
		this.thread = thread;
		this.proxy = proxy;
	}

	public int getCharEnd() throws DebugException
	{
		if (charEnd == -1)
		{
			return charStart + 1;
		}
		return charEnd;
	}

	public int getCharStart() throws DebugException
	{
		// return charStart;
		return -1;
	}

	public int getLineNumber() throws DebugException
	{
		return lineNumber;
	}

	public String getName() throws DebugException
	{
		String w = "";
		if (where != null && where.trim().length() > 0)
		{
			w = " (" + where + ") ";
		}
		if (nameIsFileUri)
		{
			return new File(name).getName() + w + " line: " + lineNumber;
		}
		return name + w;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		return new IRegisterGroup[0];
	}

	public IThread getThread()
	{
		return thread;
	}

	public IVariable[] getVariables() throws DebugException
	{
		if(!thread.isSuspended())
		{
			return new IVariable[0];
		}
		if (variables != null)
		{
			return variables;
		}
		try
		{
			Map<String, Integer> contextNames = proxy.getContextNames();

			List<VdmVariable> variables = new Vector<VdmVariable>();

			for (String name : contextNames.keySet())
			{
				if (contextNames.get(name) != 0)
				{
					VdmVariable v = new VdmVariable(null,
							name,
							"",
							new VdmMultiValue("",
									"",
									null,
									0,
									proxy.getVariables(level,
											contextNames.get(name))),true);
					variables.add(v);
				} else
				{
					// get locals

					for (VdmVariable iVariable : proxy.getVariables(level, 0))
					{
						variables.add(iVariable);
					}

				}
			}

			for (VdmVariable iVariable : variables)
			{
				iVariable.setStackFrame(this);
			}

			this.variables = variables.toArray(new IVariable[variables.size()]);
		} catch (DBGPProxyException e)
		{
			//TODO
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			// Assume that the thread is running again and don't loop in the read DBGP Command loop
			thread.getDebugState().setState(DebugState.Resumed);
			
			throw new DebugException(new Status(IStatus.WARNING,
					IDebugConstants.PLUGIN_ID,
					"Cannot fetch variables from debug engine",
					e));
		}
		return this.variables;
	}

	public boolean hasRegisterGroups() throws DebugException
	{
		return false;
	}

	public boolean hasVariables() throws DebugException
	{
		return true;
	}

	//IStep
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

	public String getSourceName()
	{
		if (nameIsFileUri)
		{
			int i = name.lastIndexOf('/');
			String fileName = name.substring(i + 1);
			return fileName;
		}
		return null;
	}

	public void clearVariables()
	{
		variables = null;
	}

	
}
