package org.overture.ide.debug.core.model;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Vector;

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
import org.overture.ide.debug.utils.communication.DebugThreadProxy;
import org.overture.ide.debug.utils.communication.IDebugThreadProxyCallback;

public class VdmThread extends VdmDebugElement implements IThread,
		IVdmExecution
{
	private class CallbackHandler implements IDebugThreadProxyCallback
	{

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
			state.setState(DebugState.Suspended);
			fireSuspendEvent(DebugEvent.BREAKPOINT);
		}

		public void firePrintErr(String text)
		{
			fTarget.printErr(text);
		}

		public void firePrintMessage(boolean output, String message)
		{
			fTarget.printMessage(output, message);
		}

		public void firePrintOut(String text)
		{
			fTarget.printOut(text);
		}

		public void fireStarted()
		{
			if (!state.inState(DebugState.Terminated))
			{
				doResume(this);
			}
		}

		public void fireStopped()
		{
			state.setState(DebugState.Terminated);
			fireTerminateEvent();
		}

		public void fireBreakpointSet(Integer tid, Integer breakpointId)
		{
			fTarget.setBreakpointId(tid, breakpointId);
		}

		public void suspended(int reason)
		{
			state.setState(DebugState.Suspended);
			fireSuspendEvent(DebugEvent.STEP_END);
		}

		public void deadlockDetected()
		{
			state.setState(DebugState.Suspended);
			fireSuspendEvent(DebugEvent.UNSPECIFIED);
			markDeadlocked(this);
			fireDeadlockedEvent();
		}

	}

	private String fName;
	private int id;
	private DebugThreadProxy proxy;

	private List<VdmStackFrame> frames = new Vector<VdmStackFrame>();

	private VdmDebugState state = new VdmDebugState();

	public VdmThread(VdmDebugTarget target, int id, String name,
			String sessionId, Socket socket)
	{
		super(target);
		this.id = id;
		this.fName = name;
		this.proxy = new DebugThreadProxy(socket, sessionId, id, new CallbackHandler());
		this.proxy.start();

	}

	// IThread
	public IStackFrame[] getStackFrames() throws DebugException
	{
		if (isSuspended())
		{
			//updateStackFrames();
			return frames.toArray(new IStackFrame[frames.size()]);
		} else
		{
			return new IStackFrame[0];
		}

	}

	private void updateStackFrames() throws DebugException
	{
		try
		{
			VdmStackFrame[] newFrames = proxy.getStack();
			synchronized (frames)
			{

				for (int i = 0; i < newFrames.length; i++)
				{
					VdmStackFrame frame = newFrames[i];
					if (i > frames.size()-1)
					{
						frames.add(frame);
					} else
					{
						frames.get(i).updateWith(frame);
					}

					frame.setDebugTarget(fTarget);
					frame.setThread(this, proxy);
				}
				if (frames.size() > newFrames.length)
				{
					frames = frames.subList(0, newFrames.length);
				}

			}
		} catch (SocketTimeoutException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "Cannot fetch stack from debug engine", e));
		}
	}

	public boolean hasStackFrames() throws DebugException
	{
		if (!isSuspended())
		{
			return false;
		}
		Integer s;
		try
		{
			s = proxy.getStackDepth();
		} catch (SocketTimeoutException e)
		{

			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "Cannot fetch stack depth from debug engine", e));

		}
		return s > 0;
	}

	public int getPriority() throws DebugException
	{
		return 0;
	}

	public IStackFrame getTopStackFrame() throws DebugException
	{
		if (isSuspended())
		{
			updateStackFrames();
			if (frames.size() > 0)
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
		return true;
	}

	public boolean canStepOver()
	{
		return true;
	}

	public boolean canStepReturn()
	{
		return true;
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
		} catch (IOException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "doStepInto", e));
		}
	}

	public void doStepOver(Object source) throws DebugException
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
			proxy.step_over();
		} catch (IOException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "doStepOver", e));
		}
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
		} catch (IOException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.WARNING, IDebugConstants.PLUGIN_ID, "doStepReturn", e));
		}
	}

	public void doSuspend(Object source)
	{
		// not supported yet
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
}
