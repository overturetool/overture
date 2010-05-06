package org.overture.ide.debug.core.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.model.VdmDebugState.DebugState;
import org.overture.ide.debug.logging.LogItem;
import org.overture.ide.debug.logging.LogView;
import org.overture.ide.debug.utils.communication.DBGPProxyException;
import org.overture.ide.debug.utils.communication.DebugThreadProxy.DebugProxyState;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overturetool.vdmj.runtime.DebuggerException;

public class VdmDebugTarget extends VdmDebugElement implements IDebugTarget,
		IVdmExecution, IBreakpointManagerListener
{
	private IProcess fProcess;
	private List<VdmThread> fThreads;
	private VdmThread fThread;
	private boolean logging = false;
	private LogView logView;
	private ConsoleWriter console;
	private HashMap<Integer, VdmLineBreakpoint> breakpointMap = new HashMap<Integer, VdmLineBreakpoint>();
	private IVdmProject project;

	private VdmDebugState state = new VdmDebugState(DebugState.Resumed);

	private List<IBreakpoint> fBreakpoints = new ArrayList<IBreakpoint>();
	private File outputFolder;

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
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointManagerListener(this);

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
		// check process since no event handler is available on the process to check
		if (fProcess.isTerminated() && !isRemoteDebug())
		{
			state.setState(DebugState.Terminated);
		}
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

		if (!DebugPlugin.getDefault().getBreakpointManager().isEnabled())
			return;

		try
		{
			System.out.println("Breakpoint changed" + breakpoint.toString()
					+ breakpoint.isEnabled());
		} catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
		if (isSuspended())
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
		} catch (DBGPProxyException e)
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
			final IWorkbench wb = PlatformUI.getWorkbench();
			if (wb.getWorkbenchWindowCount() > 0)
			{

				wb.getDisplay().syncExec(new Runnable()
				{

					public void run()
					{
						IWorkbenchPage page = wb.getWorkbenchWindows()[0].getActivePage();
						IViewPart v;
						try
						{
							v = page.showView(IDebugConstants.LogViewId);
							if (v instanceof LogView)
								logView = ((LogView) v);
						} catch (PartInitException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

			}
			
			logView.clear();
		}

	}

	/**
	 * Install breakpoints that are already registered with the breakpoint manager.
	 */
	private void installDeferredBreakpoints()
	{
		if (!DebugPlugin.getDefault().getBreakpointManager().isEnabled())
			return;

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
			handlePreLaunchCommands();
		}
		fThreads.add(vdmThread);
	}

	private void handlePreLaunchCommands()
	{
		// try
		// {
		// File logDir = new File(new File(outputFolder, "logs"), fLaunch.getLaunchConfiguration().getName());
		// logDir.mkdirs();
		// DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		// Date date = new Date();
		// String logFilename = dateFormat.format(date) + ".logrt";
		// File f = new File(logDir, logFilename);
		// fThread.getProxy().xcmd_overture_log(f.toURI().toASCIIString());
		// } catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void printLog(LogItem item)
	{
		if (logging)
		{
			logView.log(item);
			logView.setFocus();
		}
	}

	public void printOut(String message)
	{
		console.ConsolePrint(message);
	}

	public void printErr(String message)
	{
		console.ConsolePrint(message, SWT.COLOR_RED);
		if (!logging)
		{
			console.Show();
		}
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
		// fireChangeEvent(DebugEvent.STATE);

	}

	public void doTerminate(Object source) throws DebugException
	{
		if (fThread != null
				&& fThread.getProxy().getDebugState() == DebugProxyState.Ready)
		{
			handlePostLaunchCommands();

			try
			{
				if (fThread != null && fThread.getProxy() != null
						&& !fThread.equals(source))
				{
					fThread.getProxy().allstop();
				}
			} catch (DBGPProxyException e)
			{
				e.printStackTrace();
				throw new DebuggerException(e.getMessage());
			}
		}
		fProcess.terminate();
		state.setState(DebugState.Terminated);
		console.flush();
		console.Show();
	}

	private boolean isCoverageEnabled() throws CoreException
	{
		return fLaunch.getLaunchConfiguration().getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, false);
	}

	private void handlePostLaunchCommands()
	{
		try
		{
			if (isCoverageEnabled())
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
				File coverageDir = new File(new File(outputFolder, "coverage"), dateFormat.format(new Date()));
				fThread.getProxy().xcmd_overture_writecoverage(coverageDir);

				for (IVdmSourceUnit source : project.getSpecFiles())
				{
					String name = source.getSystemFile().getName();

					writeFile(coverageDir, name + "cov", getContent(source));
				}
			}
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}

	}

	private String getContent(IVdmSourceUnit source) throws CoreException,
			IOException
	{
		InputStreamReader reader = new InputStreamReader(source.getFile().getContents());
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = reader.read()) != -1)
		{
			sb.append((char) inLine);
		}
		return sb.toString();
	}

	public static void writeFile(File outputFolder, String fileName,
			String content) throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder, fileName));
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
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

		if (allTerminated || source instanceof VdmThread
				&& ((VdmThread) source).getId() == 1)// or main thread
		{
			doTerminate(this);
			fireChangeEvent(DebugEvent.CONTENT);
		}
	}

	public void doPreSuspendRequest(Object source) throws DebugException
	{
		// not used
	}

	public void markDeadlocked(Object source) throws DebugException
	{
		for (VdmThread t : fThreads)
		{
			if (!t.equals(source))
			{
				t.markDeadlocked(source);
			}
		}
		state.setState(DebugState.Deadlocked);
		fireChangeEvent(DebugEvent.CONTENT);// update all thread names
	}

	public void breakpointManagerEnablementChanged(boolean enabled)
	{

		if (!isSuspended())
		{
			return;
		}
		Iterator<VdmBreakpoint> breakpoints = ((ArrayList<VdmBreakpoint>) ((ArrayList<IBreakpoint>) getBreakpoints()).clone()).iterator();
		while (breakpoints.hasNext())
		{
			VdmBreakpoint breakpoint = breakpoints.next();
			try
			{
				if (enabled)
				{
					breakpointAdded(breakpoint);
				} else if (breakpoint.shouldSkipBreakpoint())
				{
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e)
			{
				// logError(e);
			}
		}

	}

	public void setOutputFolder(File outputFolder)
	{
		this.outputFolder = outputFolder;

	}

	public void removeThread(VdmThread thread)
	{
		this.fThreads.remove(thread);

	}

	private boolean isRemoteDebug()
	{
		try
		{
			return fLaunch != null
					&& fLaunch.getLaunchConfiguration() != null
					&& fLaunch.getLaunchConfiguration().getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		} catch (CoreException e)
		{
			return false;
		}
	}

}
