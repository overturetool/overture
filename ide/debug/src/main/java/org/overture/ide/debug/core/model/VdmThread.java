package org.overture.ide.debug.core.model;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.model.VdmDebugState.DebugState;
import org.overture.ide.debug.logging.LogItem;
import org.overture.ide.debug.utils.communication.DBGPProxyException;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;
import org.overture.ide.debug.utils.communication.IDebugThreadProxyCallback;
import org.overturetool.vdmj.scheduler.RunState;

public class VdmThread extends VdmDebugElement implements IThread,
		IVdmExecution
{
	private class CallbackHandler implements IDebugThreadProxyCallback
	{
		private VdmThread thread = null;

		public CallbackHandler(VdmThread thread)
		{
			this.thread = thread;
		}

		public void fireBreakpointHit()
		{
			String event = "event";
			// determine which breakpoint was hit, and set the thread's breakpoint
			int lastSpace = event.lastIndexOf(' ');
			if (lastSpace > 0)
			{
				String line = event.substring(lastSpace + 1);
				int lineNumber = Integer.parseInt(line);
				IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IDebugConstants.ID_VDM_DEBUG_MODEL);
				for (int i = 0; i < breakpoints.length; i++)
				{
					IBreakpoint breakpoint = breakpoints[i];
					if (fTarget.supportsBreakpoint(breakpoint))
					{
						if (breakpoint instanceof ILineBreakpoint)
						{
							ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;
							try
							{
								if (lineBreakpoint.getLineNumber() == lineNumber)
								{
									// TODO: is this needed ?
									// fThread.setBreakpoints(new
									// IBreakpoint[]{breakpoint});
									break;
								}
							} catch (CoreException e)
							{
							}
						}
					}
				}
			}
			try
			{
				doSuspend(thread);
			} catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fireSuspendEvent(DebugEvent.BREAKPOINT);
		}

		public void firePrintErr(String text)
		{
			fTarget.printErr(text);
		}

//		public void firePrintMessage(LogItem item)
//		{
//			fTarget.printMessage(item);
//		}
//
//		public void firePrintErrorMessage(LogItem item)
//		{
//			fTarget.printErrorMessage(item);
//		}
		public void fireLogEvent(LogItem item)
		{
			fTarget.printLog(item);
		}

		public void firePrintOut(String text)
		{
			fTarget.printOut(text);
		}

		public void fireStarted()
		{
			if (!state.inState(DebugState.Terminated)&& !state.inState(DebugState.Suspended))
			{
				doResume(thread);
			}
		}

		public void fireStopped()
		{
			state.setState(DebugState.Terminated);
			if (id == 1)
			{
				fireTerminateEvent();
			} else
			{
				fTarget.removeThread(thread);
			}
		}

		public void fireBreakpointSet(Integer tid, Integer breakpointId)
		{
			fTarget.setBreakpointId(tid, breakpointId);
		}

		public void suspended()
		{

			try
			{
				doSuspend(thread);
			} catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fireSuspendEvent(DebugEvent.STEP_END);
		}

		public void deadlockDetected()
		{
			state.setState(DebugState.Suspended);
			try
			{
				doSuspend(thread);
			} catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fireSuspendEvent(DebugEvent.UNSPECIFIED);
			markDeadlocked(thread);
			fireDeadlockedEvent();
		}

		public void updateInternalState(String id, String name, RunState state)
		{
			internalState = state;
		}

	}

	private String fName;
	private int id;
	private DebugThreadProxy proxy;
	RunState internalState = RunState.CREATED;
	private List<VdmStackFrame> frames = null;

	private VdmDebugState state = new VdmDebugState(null);
	private Object lock = new Object();
	private boolean updating = false;

	public VdmThread(VdmDebugTarget target, int id, String name,
			String sessionId, Socket socket,DebugState initialState)
	{
		super(target);
		this.state=new VdmDebugState(initialState);
		this.id = id;
		this.fName = name;
		this.proxy = new DebugThreadProxy(socket, sessionId, id, new CallbackHandler(this));
		this.proxy.start();

	}

	// IThread
	public IStackFrame[] getStackFrames() throws DebugException
	{
		if (isSuspended())
		{
			if (frames == null)
			{
				updateStackFrames();
			} else if (frames != null)
			{
				return frames.toArray(new IStackFrame[frames.size()]);
			}
			return new IStackFrame[0];
		} else
		{
			return new IStackFrame[0];
		}

	}

	private void updateStackFrames() throws DebugException
	{
		synchronized (lock)
		{
			if (updating)
			{
				return;
			} else
			{
				updating = true;
			}
		}

		try
		{
			VdmStackFrame[] newFrames = proxy.getStack();

			for (int i = 0; i < newFrames.length; i++)
			{
				VdmStackFrame frame = newFrames[i];
				// if (i > frames.size()-1)
				// {
				// frames.add(frame);
				// } else
				// {
				// frames.get(i).updateWith(frame);
				// }
				//
				frame.setDebugTarget(fTarget);
				frame.setThread(this, proxy);
			}
			// if (frames.size() > newFrames.length)
			// {
			// frames = frames.subList(0, newFrames.length);
			// }
			frames = Arrays.asList(newFrames);

		} catch (DBGPProxyException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			// todo
			// Assume that the thread is running again and don't loop in the read DBGP Command loop
			state.setState(DebugState.Resumed);
			synchronized (lock)
			{
				updating = false;
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "Cannot fetch stack from debug engine", e));
		}
		synchronized (lock)
		{
			updating = false;
		}
	}

	public boolean hasStackFrames() throws DebugException
	{
		if (!isSuspended())
		{
			return false;
		}
		// Integer s;
		// try
		// {
		// s = proxy.getStackDepth();
		// } catch (SocketTimeoutException e)
		// {
		//
		// if (Activator.DEBUG)
		// {
		// e.printStackTrace();
		// }
		// throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID,
		// "Cannot fetch stack depth from debug engine", e));
		//
		// }
		// return s > 0;
		if (frames == null)
		{
			updateStackFrames();
		} else if (frames != null)
		{
			return frames.size() > 0;
		}
		return false;
	}

	public int getPriority() throws DebugException
	{
		return 0;
	}

	public IStackFrame getTopStackFrame() throws DebugException
	{
		if (internalState== RunState.RUNNING && isSuspended())
		{
			if (frames == null)
			{
				updateStackFrames();
			}
			if (frames != null && frames.size() > 0)
			{
				return frames.get(0);
			}
		}

			return null;
	}

	public String getName() throws DebugException
	{
		return fName;
	}

	public IBreakpoint[] getBreakpoints()
	{
		// TODO filter
		return DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(getModelIdentifier());
	}

	// ISuspendResume
	public boolean canResume()
	{
		return state.canChange(DebugState.Resumed);
	}

	public boolean canSuspend()
	{
		return state.canChange(DebugState.Suspended);
	}

	public boolean isSuspended()
	{
		return state.inState(DebugState.Suspended);
	}

	public void resume() throws DebugException
	{

		doResume(this);
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);

	}

	public void suspend() throws DebugException
	{
		doSuspend(this);
		fireSuspendEvent(DebugEvent.CLIENT_REQUEST);

	}

	// IStep

	public boolean canStepInto()
	{
		return state.inState(DebugState.Suspended);
	}

	public boolean canStepOver()
	{
		return state.inState(DebugState.Suspended);
	}

	public boolean canStepReturn()
	{
		return state.inState(DebugState.Suspended);
	}

	public boolean isStepping()
	{
		return state.inState(DebugState.IsStepping);
	}

	public void stepInto() throws DebugException
	{
		doStepInto(this);
		fireResumeEvent(DebugEvent.STEP_INTO);

	}

	public void stepOver() throws DebugException
	{
		doStepOver(this);
		fireResumeEvent(DebugEvent.STEP_OVER);

	}

	public void stepReturn() throws DebugException
	{
		doStepReturn(this);
		fireResumeEvent(DebugEvent.STEP_RETURN);

	}

	// ITerminate
	public boolean canTerminate()
	{
		return false;// !fTerminated;
	}

	public boolean isTerminated()
	{
		return state.inState(DebugState.Terminated);
	}

	public void terminate() throws DebugException
	{
		doTerminate(this);
		fireTerminateEvent();
	}

	// END ITerminate

	public int getId()
	{
		return id;
	}

	public DebugThreadProxy getProxy()
	{
		return proxy;
	}

	// IVdmExecution
	public void doResume(Object source)
	{
		state.setState(DebugState.Resumed);
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}

		proxy.resume();
	}

	public void doStepInto(Object source) throws DebugException
	{
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}

		state.setState(DebugState.IsStepping);

		try
		{
			proxy.step_into();
		} catch (DBGPProxyException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "doStepInto", e));
		}
		state.setState(DebugState.Resumed);
	}

	public void doStepOver(Object source) throws DebugException
	{
		System.out.println("StepOver: "+ state);
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}

		state.setState(DebugState.IsStepping);
		try
		{
			proxy.step_over();
		} catch (DBGPProxyException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "doStepOver", e));
		}
		state.setState(DebugState.Resumed);
	}

	public void doStepReturn(Object source) throws DebugException
	{
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}

		state.setState(DebugState.IsStepping);
		try
		{
			proxy.step_out();
		} catch (DBGPProxyException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "doStepReturn", e));
		}
		state.setState(DebugState.Resumed);
	}

	public void doSuspend(Object source) throws DebugException
	{

		frames = null;

		fireModelSpecificEvent(PRE_SUSPEND_REQUEST);
		state.setState(DebugState.Suspended);
		// fireChangeEvent(DebugEvent.CONTENT);
		// fireChangeEvent();
	}

	public void doTerminate(Object source)
	{
		state.setState(DebugState.Terminated);
		proxy.terminate();
	}

	public void markDeadlocked(Object source)
	{
		state.setState(DebugState.Deadlocked);
		this.fName += " - DEADLOCKED";

	}

	public void doPreSuspendRequest(Object source) throws DebugException
	{
		if (isSuspended())
		{
			updateStackFrames();
		}

	}

	/**
	 * Should be removed
	 * 
	 * @return
	 */
	public VdmDebugState getDebugState()
	{
		return state;
	}
}
