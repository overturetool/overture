package org.overture.ide.debug.core.model;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
import org.overture.ide.debug.utils.communication.DebugThreadProxy;
import org.overture.ide.debug.utils.communication.IDebugThreadProxyCallback;
import org.overture.ide.debug.utils.xml.XMLTagNode;
import org.overturetool.vdmj.runtime.DebuggerException;

public class VdmThread extends VdmDebugElement implements IThread
{
	private class CallbackHandler implements IDebugThreadProxyCallback
	{

		public void fireBreakpointHit()
		{
			breakpointHit("event");
			suspended(0);
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
			if (!fTerminated)
				proxy.resume();
		}

		public void fireStopped()
		{
			fTerminated = true;
			try
			{
				fTarget.shutdown();
			} catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void fireBreakpointSet(Integer tid, Integer breakpointId)
		{
			fTarget.setBreakpointId(tid, breakpointId);
		}

	}

	private String fName;
	private int id;
	private DebugThreadProxy proxy;

	private boolean fSuspended = false;
	private boolean fTerminated = false;
	private boolean fIsStepping = false;
	private VdmStackFrame[] frames = null;
	private boolean isMultiMain = false;

	public VdmThread(VdmDebugTarget target, int id, String sessionId,
			Socket socket) {
		super(target);
		this.id = id;
		this.proxy = new DebugThreadProxy(socket,
				sessionId,
				id,
				new CallbackHandler());
		this.proxy.start();
	}

	public IBreakpoint[] getBreakpoints()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() throws DebugException
	{
		return fName;
	}

	public int getPriority() throws DebugException
	{
		return 0;
	}

	public IStackFrame[] getStackFrames() throws DebugException
	{
		if (isSuspended() && !isMultiMain)
		{
			try
			{
				frames = proxy.getStack();

				for (VdmStackFrame f : frames)
				{
					f.setDebugTarget(fTarget);
					f.setThread(this, proxy);
				}
			} catch (SocketTimeoutException e)
			{
				if (Activator.DEBUG)
				{
					e.printStackTrace();
				}
				throw new DebugException(new Status(IStatus.WARNING,
						IDebugConstants.PLUGIN_ID,
						"Cannot fetch stack from debug engine",
						e));
			}
			return frames;
		} else
		{
			return new IStackFrame[0];
		}
	}

	public IStackFrame getTopStackFrame() throws DebugException
	{
		if (isSuspended() && !isMultiMain)
		{
			IStackFrame[] frames = getStackFrames();
			if (frames.length > 0)
			{
				return frames[0];
			}
		}
		return null;
	}

	public boolean hasStackFrames() throws DebugException
	{
		if (fTerminated || isMultiMain)
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
			throw new DebugException(new Status(IStatus.WARNING,
					IDebugConstants.PLUGIN_ID,
					"Cannot fetch stack depth from debug engine",
					e));

		}

		System.out.println("Stack depth is: " + s);
		return s > 0;
	}

	public boolean canResume()
	{
		return fSuspended && !fTerminated;
	}

	public boolean canSuspend()
	{
		return !fSuspended && !fTerminated;
	}

	public boolean isSuspended()
	{
		return fSuspended;
	}

	public void resume() throws DebugException
	{
		fSuspended = false;
		fIsStepping = false;
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}
		//Resume is done on main thread only
		fTarget.resume();
		//proxy.resume();
		

	}

	public void suspend() throws DebugException
	{
		fSuspended = true;

	}

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
		return fIsStepping;
	}

	public void stepInto() throws DebugException
	{
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}
		try
		{
			proxy.step_into();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new DebuggerException(e.getMessage());
		}
		fIsStepping = true;
	}

	public void stepOver() throws DebugException
	{
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}
		try
		{
			proxy.step_over();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new DebuggerException(e.getMessage());
		}
		fIsStepping = true;
	}

	public void stepReturn() throws DebugException
	{
		if (frames != null)
		{
			for (VdmStackFrame frame : frames)
			{
				frame.clearVariables();
			}
		}
		try
		{
			proxy.step_out();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new DebuggerException(e.getMessage());
		}
		fIsStepping = true;
	}

	public boolean canTerminate()
	{
		return false;// !fTerminated;
	}

	public boolean isTerminated()
	{
		return fTerminated;
	}

	public void terminate() throws DebugException
	{
		fTerminated = true;
		fTarget.terminate();
		// fireTerminateEvent();
	}

	public void setName(String name)
	{
		fName = name;
	}

	public int getId()
	{
		return id;
	}

	public DebugThreadProxy getProxy()
	{
		return proxy;
	}

	public void init(XMLTagNode tagnode) throws IOException
	{
		if (id != 1)
		{
			String sid = tagnode.getAttr("thread");
			this.fName = sid;
		} else
		{
			this.fName = "Thread [Main]";
		}
		proxy.processInit(tagnode);
	}

	public void setMultiMain()
	{
		this.isMultiMain = true;

	}

	/**
	 * Notification a breakpoint was encountered. Determine which breakpoint was hit and fire a suspend event.
	 * 
	 * @param event
	 *            debug event
	 */
	private void breakpointHit(String event)
	{
		// determine which breakpoint was hit, and set the thread's breakpoint
		int lastSpace = event.lastIndexOf(' ');
		if (lastSpace > 0)
		{
			String line = event.substring(lastSpace + 1);
			int lineNumber = Integer.parseInt(line);
			IBreakpoint[] breakpoints = DebugPlugin.getDefault()
					.getBreakpointManager()
					.getBreakpoints(IDebugConstants.ID_VDM_DEBUG_MODEL);
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
		suspended(DebugEvent.BREAKPOINT);
	}

	private void suspended(int breakpoint)
	{
		fSuspended = true;
		fireSuspendEvent(DebugEvent.BREAKPOINT);
		try
		{
			// if a thread suspends the target is suspended
			fTarget.suspend();
		} catch (DebugException e)
		{
			// no action, just a state change for the buttons
		}

	}

	public void shutdown() throws IOException
	{
		proxy.shutdown();
		
	}
}
