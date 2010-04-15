package org.overture.ide.debug.core.model;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.utils.xml.XMLTagNode;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overturetool.vdmj.runtime.DebuggerException;

public class VdmDebugTarget extends VdmDebugElement implements IDebugTarget
{
	private ILaunch fLaunch;
	private IProcess fProcess;
	private List<VdmThread> fThreads;
	private VdmThread fThread;
	private boolean fTerminated = false;
	private boolean fSuspended = false;
	private boolean fDisconected = false;
	private boolean logging = false;
	private ConsoleWriter loggingConsole;
	private ConsoleWriter console;
	private String sessionId;
	private HashMap<Integer, VdmLineBreakpoint> breakpointMap = new HashMap<Integer, VdmLineBreakpoint>();
	private IVdmProject project;

	private List<IBreakpoint> fBreakpoints = new ArrayList<IBreakpoint>();
	
	public VdmDebugTarget(ILaunch launch) throws DebugException {
		super(null);
		fTarget = this;
		fLaunch = launch;

		fThreads = new ArrayList<VdmThread>();

		console = loggingConsole = new ConsoleWriter("Overture Debug");
		console.clear();
		console.Show();

		try
		{
			setLogging(launch);
		} catch (CoreException e)
		{
			// OK
		}

		DebugPlugin.getDefault()
				.getBreakpointManager()
				.addBreakpointListener(this);

	}

	public void setProcess(IProcess process)
	{
		fProcess = process;
	}

	private void setLogging(ILaunch launch) throws CoreException
	{
		if (launch.getLaunchConfiguration()
				.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING,
						false))
		{
			logging = true;
			loggingConsole = new ConsoleWriter("VDM Debug log");
		}

	}

	public String getName() throws DebugException
	{
		return "VDM Application";
	}

	public IProcess getProcess()
	{
		return fProcess;
	}

	@Override
	public ILaunch getLaunch()
	{
		return this.fLaunch;
	}

	public IThread[] getThreads() throws DebugException
	{
		IThread[] result = new IThread[fThreads.size()];
		System.arraycopy(fThreads.toArray(), 0, result, 0, fThreads.size());
		return result;
	}

	public boolean hasThreads() throws DebugException
	{
		return fThreads.size() > 0;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		return true; // Yes VDMJ supports breakpoints always
	}

	public boolean canTerminate()
	{
		return !fTerminated;
	}

	public boolean isTerminated()
	{
		return fTerminated;
	}

	public void terminate() throws DebugException
	{
		try
		{
			if (fThread != null && fThread.getProxy() != null)
				fThread.getProxy().allstop();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new DebuggerException(e.getMessage());
		}
shutdown();
	}
	
	public void shutdown() throws DebugException
	{
		for (IThread thread : fThreads)
		{
			if(thread instanceof VdmThread)
			{
				try
				{
					((VdmThread)thread).shutdown();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
		}
		fThreads.clear();
		fProcess.terminate();
		fTerminated = true;
		fireTerminateEvent();
	}

	public boolean canResume()
	{
		return (fSuspended && !fTerminated);
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
		//Control of remune is only done on main thread
		//fThread.getProxy().resume();
		 for (VdmThread thread : fThreads) {
		 thread.getProxy().resume();
		 }
		// fireResumeEvent(DebugEvent.RESUME);

	}

	/**
	 * 
	 * @param detail
	 *            - see DebugEvent detail constants;
	 * @throws DebugException
	 */
	public void suspend() throws DebugException
	{
		fSuspended = true;

	}

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
					IResource resource = breakpoint.getMarker()
							.getResource();
					if (resource instanceof IFile)
					{
						// check that the breakpoint is from this project's
						// IFile resource
						if (resource.getProject()
								.getName()
								.equals(project.getName()))
						{
//								int line = ((ILineBreakpoint) breakpoint).getLineNumber();
//								File file = ((VdmLineBreakpoint) breakpoint).getFile();
//								int xid = fThread.getProxy()
//										.breakpointAdd(line,
//												file.toURI().toASCIIString());
							int xid = fThread.getProxy().breakpointAdd(breakpoint);
							

							synchronized (breakpointMap)
							{
								breakpointMap.put(new Integer(xid),
										(VdmLineBreakpoint) breakpoint);
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
		System.out.println("Breakpoint changed" );
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
				fThread.getProxy()
						.breakpointRemove((VdmLineBreakpoint) breakpoint);
			}
		}

	}

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
		fDisconected = true;
	}

	public boolean isDisconnected()
	{
		return fDisconected;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException
	{
		return null;
	}

	public boolean supportsStorageRetrieval()
	{
		return false;
	}

	/**
	 * Install breakpoints that are already registered with the breakpoint manager.
	 */
	private void installDeferredBreakpoints()
	{
		// installPreviousBreakpointMarkers();
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(IDebugConstants.ID_VDM_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++)
		{
			breakpointAdded(breakpoints[i]);
		}
	}

	public void newThread(XMLTagNode tagnode, Socket s) throws DebugException
	{
		String sid = tagnode.getAttr("thread");
		int id = -1;
		// Either "123" or "123 on <CPU name>" for VDM-RT
		int space = sid.indexOf(' ');

		if (space == -1)
		{
			id = Integer.parseInt(sid);
		} else
		{
			id = Integer.parseInt(sid.substring(0, space));
		}

		VdmThread t = new VdmThread(this, id, sessionId, s);

		if (id == 1)
		{ // Assuming 1 is main thread;

			fThread = t; // TODO: assuming main thread is number 1;
			installDeferredBreakpoints();
		}

		fThreads.add(t);

		try
		{
			t.init(tagnode);
		} catch (IOException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			throw new DebugException(new Status(IStatus.ERROR,
					IDebugConstants.PLUGIN_ID,
					"Cannot init thread",
					e));

		}

		if (fThreads.size() > 1)
		{
			for (int i = 0; i < fThreads.size(); i++)
			{
				VdmThread thread = (VdmThread) fThreads.get(i);
				if (thread.getId() == 1)
				{
					 thread.setMultiMain();
					break;
				}
			}
		}
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
	 * Returns the collection of breakpoints installed in this
	 * debug target.
	 * 
	 * @return list of installed breakpoints - instances of 
	 * 	<code>IJavaBreakpoint</code>
	 */
	public List<IBreakpoint> getBreakpoints() {
		return fBreakpoints;
	}

	public boolean isAvailable() {
		return !isTerminated();
	}

}
