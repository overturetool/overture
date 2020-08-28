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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.overture.ide.debug.core.ExtendedDebugEventDetails;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpNotification;
import org.overture.ide.debug.core.dbgp.IDbgpNotificationListener;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.IDbgpStatusInterpreterThreadState;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;
import org.overture.ide.debug.core.dbgp.commands.IDbgpExtendedCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.IDbgpTerminationListener;
import org.overture.ide.debug.core.dbgp.internal.utils.Util;
import org.overture.ide.debug.core.model.DebugEventHelper;
import org.overture.ide.debug.core.model.IDebugLaunchConstants;
import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationEngine;
import org.overture.ide.debug.core.model.internal.eval.VdmEvaluationEngine;
import org.overture.ide.debug.core.model.internal.operations.DbgpDebugger;
import org.overture.ide.debug.logging.LogItem;

public class VdmThread extends VdmDebugElement implements IVdmThread,
		IThreadManagement, IDbgpTerminationListener,
		VdmThreadStateManager.IStateChangeHandler
{

	private VdmThreadStateManager stateManager;

	private final IVdmThreadManager manager;

	private final VdmStack stack;

	// Session
	private final IDbgpSession session;

	// State variables
	private final IVdmDebugTarget target;

	private IVdmEvaluationEngine evalEngine;

	// private int currentStackLevel;

	private boolean terminated = false;

	private int propertyPageSize = 32;

	private IDbgpStatusInterpreterThreadState interpreterThreadState;

	private boolean errorState;

	// VdmThreadStateManager.IStateChangeHandler
	public void handleSuspend(int detail)
	{
		DebugEventHelper.fireExtendedEvent(this, ExtendedDebugEventDetails.BEFORE_SUSPEND);

		// if (handleSmartStepInto()) {
		// return;
		// }

		this.target.printLog(new LogItem(this.session.getInfo(), "Break", false, "Suspend"));
		DebugEventHelper.fireChangeEvent(this);
		DebugEventHelper.fireSuspendEvent(this, detail);

		stack.update(true);

	}

	// private boolean handleSmartStepInto() {
	// if (stateManager.isStepInto()
	// && getVdmDebugTarget().isUseStepFilters()
	// && stack.getFrames().length > currentStackLevel) {
	// stateManager.setStepInto(false);
	// IVdmDebugTarget target = this.getVdmDebugTarget();
	// String[] filters = target.getFilters();
	// IDLTKLanguageToolkit toolkit = this.getVdmDebugTarget()
	// .getLanguageToolkit();
	// if (toolkit != null) {
	// ISmartStepEvaluator evaluator = SmartStepEvaluatorManager
	// .getEvaluator(toolkit.getNatureId());
	// if (evaluator != null) {
	// if (evaluator.isFiltered(filters, this)) {
	// try {
	// this.stepReturn();
	// return true;
	// } catch (DebugException e) {
	// if (DLTKCore.DEBUG) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// }
	// }
	// return false;
	// }

	public void handleResume(int detail)
	{
		DebugEventHelper.fireExtendedEvent(this, ExtendedDebugEventDetails.BEFORE_RESUME);

		DebugEventHelper.fireResumeEvent(this, detail);
		DebugEventHelper.fireChangeEvent(this);
	}

	public void handleTermination(DbgpException e)
	{
		if (e != null)
		{
			VdmDebugPlugin.log(e);
			IVdmStreamProxy proxy = getVdmDebugTarget().getStreamProxy();
			if (proxy != null)
			{

				proxy.writeStderr("\n" + this.getName() + " " + e.getMessage() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				stack.update(false);
				IStackFrame[] frames = stack.getFrames();
				if (frames.length > 0)
				{
					proxy.writeStderr("\nStack trace:\n"); //$NON-NLS-1$
					try
					{
						for (int i = 0; i < frames.length; i++)
						{
							IVdmStackFrame frame = (IVdmStackFrame) frames[i];
							String line = "\t#" + frame.getLevel() + " file:" //$NON-NLS-1$ //$NON-NLS-2$
									+ frame.getSourceURI().getPath() + " [" //$NON-NLS-1$
									+ frame.getLineNumber() + "]\n"; //$NON-NLS-1$
							proxy.writeStderr(line);
						}
					} catch (DebugException e2)
					{
						if (VdmDebugPlugin.DEBUG)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}

		session.requestTermination();
		try
		{
			session.waitTerminated();
		} catch (InterruptedException ee)
		{
			ee.printStackTrace();
		}
		manager.terminateThread(this);
	}

	public VdmThread(IVdmDebugTarget target, IDbgpSession session,
			IVdmThreadManager manager) throws DbgpException, CoreException
	{

		this.target = target;

		this.manager = manager;

		this.session = session;
		this.session.addTerminationListener(this);

		this.stateManager = new VdmThreadStateManager(this);

		this.stack = new VdmStack(this);
	}

	public void initialize(IProgressMonitor monitor) throws DbgpException
	{
		monitor.beginTask(Util.EMPTY_STRING, 10);
		try
		{

			final DbgpDebugger engine = this.stateManager.getEngine();

			// if (VdmDebugPlugin.DEBUG) {
			// DbgpDebugger.printEngineInfo(engine);
			// }

			engine.setMaxChildren(propertyPageSize);
			engine.setMaxDepth(2);
			engine.setMaxData(8192);
			monitor.worked(2);

			manager.configureThread(engine, this);
			monitor.worked(6);

			final boolean isDebugConsole = IDebugLaunchConstants.isDebugConsole(target.getLaunch());

			if (isDebugConsole
					&& engine.isFeatureSupported(IDbgpExtendedCommands.STDIN_COMMAND))
			{
				engine.redirectStdin();
			}
			engine.setNotifyOk(true);
			if (true)
			{
				engine.redirectStdout();
				engine.redirectStderr();
			}
			monitor.worked(2);

			// HotCodeReplaceManager.getDefault().addHotCodeReplaceListener(this);
		} finally
		{
			monitor.done();
		}
		final IDbgpExtendedCommands extended = session.getExtendedCommands();
		session.getNotificationManager().addNotificationListener(new IDbgpNotificationListener()
		{
			private final BufferedReader reader = new BufferedReader(new InputStreamReader(getStreamProxy().getStdin()));

			public void dbgpNotify(IDbgpNotification notification)
			{
				try
				{
					extended.sendStdin(reader.readLine() + "\n");
				} catch (IOException e)
				{
					// TODO: log exception
					e.printStackTrace();
				} catch (DbgpException e)
				{
					// TODO: log exception
					e.printStackTrace();
				}
			}
		});
	}

	public boolean hasStackFrames()
	{
		return isSuspended() && !isTerminated() && stack.hasFrames();
	}

	boolean isStackInitialized()
	{
		return stack.isInitialized();
	}

	// IThread
	public IStackFrame[] getStackFrames() throws DebugException
	{
		if (!isSuspended())
		{
			try
			{
				Thread.sleep(100);
			} catch (Exception e)
			{
			}
			if (!isSuspended())
			{
				return VdmStack.NO_STACK_FRAMES;
			}
		}

		return session.getDebugOptions().filterStackLevels(stack.getFrames());
	}

	public int getPriority() throws DebugException
	{
		return 0;
	}

	public IStackFrame getTopStackFrame()
	{
		return stack.getTopFrame();
	}

	public String getName()
	{
		String name = session.getInfo().getThreadId();
		if (name.length() > 0)
		{
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		// TODO remove state from name
		return name
				+ new String(interpreterThreadState == null ? "" : " - "
						+ interpreterThreadState.getState().toString());
	}

	public IBreakpoint[] getBreakpoints()
	{
		return DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(getModelIdentifier());
	}

	// ISuspendResume

	// Suspend
	public int getModificationsCount()
	{
		return stateManager.getModificationsCount();
	}

	public boolean isSuspended()
	{
		return stateManager.isSuspended();
	}

	public boolean canSuspend()
	{
		return stateManager.canSuspend();
	}

	public void suspend() throws DebugException
	{
		stateManager.suspend();
		this.target.printLog(new LogItem(this.session.getInfo(), "REQUEST", true, "Suspend"));
	}

	// Resume
	public boolean canResume()
	{
		if (errorState)
		{
			return false;
		}
		return stateManager.canResume();
	}

	public void resume() throws DebugException
	{
		this.manager.resume();
	}

	public void resumeInner() throws DebugException
	{
		this.target.printLog(new LogItem(this.session.getInfo(), "REQUEST", true, "Resume"));
		stateManager.resume();
	}

	public void initialStepInto()
	{
		stateManager.setSuspended(false, DebugEvent.CLIENT_REQUEST);
		stateManager.getEngine().stepInto();
		this.target.printLog(new LogItem(this.session.getInfo(), "REQUEST", true, "Initial Step Into"));
	}

	// IStep
	public boolean isStepping()
	{
		return stateManager.isStepping();
	}

	boolean isStepInto()
	{
		return stateManager.isStepInto();
	}

	// Step into
	public boolean canStepInto()
	{
		if (errorState)
		{
			return false;
		}
		return stateManager.canStepInto();
	}

	public void stepInto() throws DebugException
	{
		this.manager.stepInto();
		// currentStackLevel = this.stack.getFrames().length;
		// stateManager.stepInto();
	}

	public void stepIntoInner() throws DebugException
	{
		// currentStackLevel = this.stack.getFrames().length;
		stateManager.stepInto();
		this.target.printLog(new LogItem(this.session.getInfo(), "REQUEST", true, "Step Into"));
	}

	// Step over
	public boolean canStepOver()
	{
		if (errorState)
		{
			return false;
		}
		return stateManager.canStepOver();
	}

	public void stepOver() throws DebugException
	{
		this.manager.stepOver();
	}

	public void stepOverInner() throws DebugException
	{
		stateManager.stepOver();
		this.target.printLog(new LogItem(this.session.getInfo(), "REQUEST", true, "Step Over"));
	}

	// Step return
	public boolean canStepReturn()
	{
		if (errorState)
		{
			return false;
		}
		return stateManager.canStepReturn();
	}

	public void stepReturn() throws DebugException
	{
		this.manager.stepReturn();
	}

	public void stepReturnInner() throws DebugException
	{
		stateManager.stepReturn();
		this.target.printLog(new LogItem(this.session.getInfo(), "REQUEST", true, "Step Return"));
	}

	// ITerminate
	public boolean isTerminated()
	{
		return terminated || stateManager.isTerminated();
	}

	public boolean canTerminate()
	{
		return !isTerminated();
	}

	public void terminate() throws DebugException
	{
		target.terminate();
	}

	public void sendTerminationRequest() throws DebugException
	{
		stateManager.terminate();
	}

	public IDbgpSession getDbgpSession()
	{
		return session;
	}

	public IDbgpBreakpoint getDbgpBreakpoint(String id)
	{
		try
		{
			return session.getCoreCommands().getBreakpoint(id);
		} catch (DbgpException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	public IVdmStreamProxy getStreamProxy()
	{
		return target.getStreamProxy();
	}

	public IDebugTarget getDebugTarget()
	{
		return target.getDebugTarget();
	}

	public IVdmEvaluationEngine getEvaluationEngine()
	{
		if (evalEngine == null)
		{
			evalEngine = new VdmEvaluationEngine(this);
		}

		return evalEngine;
	}

	// IDbgpTerminationListener
	public void objectTerminated(Object object, Exception e)
	{
		terminated = true;
		Assert.isTrue(object == session);
		// HotCodeReplaceManager.getDefault().removeHotCodeReplaceListener(this);
		manager.terminateThread(this);
	}

	// Object
	public String toString()
	{
		return "Thread (" + session.getInfo().getThreadId() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void notifyModified()
	{
		stateManager.notifyModified();
	}

	// public void hotCodeReplaceFailed(IVdmDebugTarget target,
	// DebugException exception) {
	// if (isSuspended()) {
	// stack.updateFrames();
	// DebugEventHelper.fireChangeEvent(this);
	// }
	// }
	//
	// public void hotCodeReplaceSucceeded(IVdmDebugTarget target) {
	// if (isSuspended()) {
	// stack.updateFrames();
	// DebugEventHelper.fireChangeEvent(this);
	// }
	// }

	public int getPropertyPageSize()
	{
		return propertyPageSize;
	}

	public boolean retrieveGlobalVariables()
	{
		return target.retrieveGlobalVariables();
	}

	public boolean retrieveClassVariables()
	{
		return target.retrieveClassVariables();
	}

	public boolean retrieveLocalVariables()
	{
		return target.retrieveLocalVariables();
	}

	public void updateStackFrames()
	{
		stack.updateFrames();
		DebugEventHelper.fireChangeEvent(VdmThread.this.getDebugTarget());
	}

	void updateStack()
	{
		stack.update(true);
	}

	boolean isValidStack()
	{
		return session.getDebugOptions().isValidStack(stack.getFrames());
	}

	public void resumeAfterAccept()
	{
		try
		{
			this.resumeInner();
		} catch (DebugException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setInterpreterState(
			IDbgpStatusInterpreterThreadState interpreterThreadState)
	{
		this.interpreterThreadState = interpreterThreadState;
	}

	public IDbgpStatusInterpreterThreadState getInterpreterState()
	{
		return this.interpreterThreadState;
	}

	public void setErrorState()
	{
		errorState = true;

	}

	@Override
	public Boolean handleCoverage() {
		
		return manager.handleCustomTerminationCommands();

	}
}
