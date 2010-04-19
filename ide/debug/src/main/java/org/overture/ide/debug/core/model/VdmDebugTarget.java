package org.overture.ide.debug.core.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.swt.SWT;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.model.VdmDebugState.DebugState;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overturetool.vdmj.runtime.DebuggerException;

public class VdmDebugTarget extends VdmDebugElement implements IDebugTarget,
		IVdmExecution
{
	private IProcess fProcess;
	private List<VdmThread> fThreads;
	private VdmThread fThread;
	private boolean logging = false;
	private ConsoleWriter loggingConsole;
	private ConsoleWriter console;
	private HashMap<Integer, VdmLineBreakpoint> breakpointMap = new HashMap<Integer, VdmLineBreakpoint>();
	private IVdmProject project;

	private VdmDebugState state = new VdmDebugState();

	private List<IBreakpoint> fBreakpoints = new ArrayList<IBreakpoint>();

	public VdmDebugTarget(ILaunch launch) throws DebugException
	{
		super(null);
		super.fTarget = this;
		super.fLaunch = launch;

		fThreads = new ArrayList<VdmThread>();

		console = new ConsoleWriter("Overture Debug");
		console.clear();
		console.Show();

		try
		{
			setLogging(launch);
		} catch (CoreException e)
		{
			// OK
		}

		DebugPlugin.getDefault().addDebugEventListener(new VdmDebugEventListener(this));
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
		fireCreationEvent();

		state.setState(DebugState.Resumed);
	}

	// IDebugTarget
	public IProcess getProcess()
	{
		return fProcess;
	}

	public IThread[] getThreads() throws DebugException
	{
		IThread[] result = new IThread[fThreads.size()];
		System.arraycopy(fThreads.toArray(), 0, result, 0, fThreads.size());
		return result;
	}

	public boolean hasThreads() throws DebugException
	{
		return !state.inState(DebugState.Terminated) && fThreads.size() > 0;
	}

	public String getName() throws DebugException
	{
		return "VDM Application";
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		return true; // Yes VDMJ supports breakpoints always
	}

	// ITerminate
	public boolean canTerminate()
	{
		return state.canChange(DebugState.Terminated);
	}

	public boolean isTerminated()
	{
		return state.inState(DebugState.Terminated);
	}

	public void terminate() throws DebugException
	{
		doTerminate(this);
		fireChangeEvent(DebugEvent.CONTENT); // force threads to be removed
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
	}

	public void suspend() throws DebugException
	{
		doSuspend(this);
	}

	// IBreakpointListener
	public void breakpointAdded(IBreakpoint breakpoint)
	{
		if (isTerminated())
		{
			return;
		}
		if (supportsBreakpoint(breakpoint))
		{
			try
			{
				if (breakpoint.isEnabled())
				{
					IResource resource = breakpoint.getMarker().getResource();
					if (resource instanceof IFile)
					{
						// check that the breakpoint is from this project's
						// IFile resource
						if (resource.getProject().getName().equals(project.getName()))
						{
							// int line = ((ILineBreakpoint) breakpoint).getLineNumber();
							// File file = ((VdmLineBreakpoint) breakpoint).getFile();
							// int xid = fThread.getProxy()
							// .breakpointAdd(line,
							// file.toURI().toASCIIString());
							int xid = fThread.getProxy().breakpointAdd(breakpoint);

							synchronized (breakpointMap)
							{
								breakpointMap.put(new Integer(xid), (VdmLineBreakpoint) breakpoint);
							}
						}
					}
				}
			} catch (CoreException e)
			{
			}
		}

	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		System.out.println("Breakpoint changed");
		if (supportsBreakpoint(breakpoint))
		{
			try
			{
				if (breakpoint.isEnabled())
				{
					breakpointAdded(breakpoint);
				} else
				{
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e)
			{
			}
		}
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		if (!isTerminated())
		{
			if (breakpoint instanceof VdmLineBreakpoint)
			{
				fThread.getProxy().breakpointRemove((VdmLineBreakpoint) breakpoint);
			}
		}

	}

	// IDisconnect
	public boolean canDisconnect()
	{
		return false;
	}

	public void disconnect() throws DebugException
	{
		try
		{
			fThread.getProxy().detach();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new DebuggerException(e.getMessage());
		}
		state.setState(DebugState.Disconnected);
	}

	public boolean isDisconnected()
	{
		return state.inState(DebugState.Disconnected);
	}

	// IMemoryBlockRetrieval
	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException
	{
		return null;
	}

	public boolean supportsStorageRetrieval()
	{
		return false;
	}

	// END IMemoryBlockRetrieval

	public void setProcess(IProcess process)
	{
		fProcess = process;
	}

	private void setLogging(ILaunch launch) throws CoreException
	{
		if (launch.getLaunchConfiguration().getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false))
		{
			logging = true;
			loggingConsole = new ConsoleWriter(IDebugConstants.CONSOLE_LOGGING_NAME);
		}

	}

	/**
	 * Install breakpoints that are already registered with the breakpoint manager.
	 */
	private void installDeferredBreakpoints()
	{
		// installPreviousBreakpointMarkers();
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IDebugConstants.ID_VDM_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++)
		{
			breakpointAdded(breakpoints[i]);
		}
	}

	public void newThread(VdmThread vdmThread) throws DebugException
	{
		if (vdmThread.getId() == 1)
		{ // Assuming 1 is main thread;

			fThread = vdmThread; // TODO: assuming main thread is number 1;
			installDeferredBreakpoints();
		}
		fThreads.add(vdmThread);
	}

	public void printMessage(boolean outgoing, String message)
	{
		if (logging)
		{
			if (outgoing)
			{
				loggingConsole.ConsolePrint(message, SWT.COLOR_DARK_BLUE);
			} else
			{
				loggingConsole.ConsolePrint(message, SWT.COLOR_DARK_YELLOW);
			}
			loggingConsole.Show();
		}
	}

	public void printOut(String message)
	{
		console.ConsolePrint(message);
	}

	public void printErr(String message)
	{
		console.ConsolePrint(message, SWT.COLOR_RED);
	}

	public void setProject(IVdmProject project)
	{
		this.project = project;

	}

	public void setBreakpointId(Integer tid, Integer breakpointId)
	{
		synchronized (breakpointMap)
		{
			if (breakpointMap.containsKey(tid))
			{
				VdmLineBreakpoint bp = breakpointMap.get(tid);
				bp.setId(breakpointId);
				breakpointMap.remove(tid);
				getBreakpoints().add(bp);
			}
		}
	}

	/**
	 * Returns the collection of breakpoints installed in this debug target.
	 * 
	 * @return list of installed breakpoints - instances of <code>IJavaBreakpoint</code>
	 */
	public List<IBreakpoint> getBreakpoints()
	{
		return fBreakpoints;
	}

	// IVdmExecution
	public void doResume(Object source) throws DebugException
	{
		for (VdmThread t : fThreads)
		{
			if (!t.equals(source))
			{
				t.doResume(source);
			}
		}
		state.setState(DebugState.Resumed);
	}

	public void doStepInto(Object source) throws DebugException
	{
		for (VdmThread t : fThreads)
		{
			if (!t.equals(source))
			{
				t.doStepInto(source);
			}
		}
	}

	public void doStepOver(Object source) throws DebugException
	{
		for (VdmThread t : fThreads)
		{
			if (!t.equals(source))
			{
				t.doStepOver(source);
			}
		}
	}

	public void doStepReturn(Object source) throws DebugException
	{
		for (VdmThread t : fThreads)
		{
			if (!t.equals(source))
			{
				t.doStepReturn(source);
			}
		}
	}

	public void doSuspend(Object source) throws DebugException
	{

		for (VdmThread t : fThreads)
		{
			if (!t.equals(source))
			{
				t.doSuspend(source);
			}
		}
		state.setState(DebugState.Suspended);
	}

	public void doTerminate(Object source) throws DebugException
	{
		try
		{
			if (fThread != null && fThread.getProxy() != null)
			{
				fThread.getProxy().allstop();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new DebuggerException(e.getMessage());
		}

		fProcess.terminate();
		state.setState(DebugState.Terminated);
		console.flush();
		console.Show();
	}

	/**
	 * Handles termination events from
	 * 
	 * @param source
	 * @throws DebugException
	 */
	public void handleTerminate(Object source) throws DebugException
	{
		boolean allTerminated = true;
		for (VdmThread t : fThreads)
		{
			if (!t.isTerminated())
			{
				allTerminated = false;
			}
		}

		if (allTerminated)
		{
			doTerminate(this);
			fireChangeEvent(DebugEvent.CONTENT);
		}
	}

}
