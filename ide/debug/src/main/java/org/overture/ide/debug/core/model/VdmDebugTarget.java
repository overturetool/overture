package org.overture.ide.debug.core.model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.swt.SWT;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;
import org.overture.ide.debug.utils.communication.IDebugThreadProxyCallback;
import org.overture.ide.debug.utils.xml.XMLOpenTagNode;
import org.overture.ide.debug.utils.xml.XMLTagNode;
import org.overture.ide.ui.internal.util.ConsoleWriter;

public class VdmDebugTarget extends VdmDebugElement implements IDebugTarget {
	private class CallbackHandler implements IDebugThreadProxyCallback {

		public void fireBreakpointHit() {
			breakpointHit("event");

			for (IThread t : fThreads) {
				if (t instanceof VdmThread) {
					try {
						((VdmThread) t).suspend();
					} catch (DebugException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		public void firePrintErr(String text) {
			printErr(text);
		}

		public void firePrintMessage(boolean output, String message) {
			printMessage(output, message);
		}

		public void firePrintOut(String text) {
			printOut(text);
		}

		public void fireStarted() {
			started();
		}

		public void fireStopped() {
			try {
				terminate();
			} catch (DebugException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void fireBreakpointSet(Integer tid, Integer breakpointId) {
			synchronized (breakpointMap) {
				if (breakpointMap.containsKey(tid)) {

					VdmLineBreakpoint bp = breakpointMap.get(tid);
					bp.setId(breakpointId);
					breakpointMap.remove(tid);
				}
			}

		}

		

	}

	// private DebugThreadProxy proxy;
	private ILaunch fLaunch;
	private IProcess fProcess;
	private List<IThread> fThreads;
	private VdmThread fThread;
	private boolean fTerminated = false;
	private boolean fSuspended = false;
	private boolean fDisconected = false;
	private boolean logging = false;
	private ConsoleWriter loggingConsole;
	private ConsoleWriter console;
	private String sessionId;
	private HashMap<Integer, VdmLineBreakpoint> breakpointMap = new HashMap<Integer, VdmLineBreakpoint>();
	

	public VdmDebugTarget(ILaunch launch) throws DebugException {
		super(null);
		fTarget = this;
		fLaunch = launch;

		fThreads = new ArrayList<IThread>();

		console = loggingConsole = new ConsoleWriter("Overture Debug");
		console.clear();
		console.Show();

		try {
			setLogging(launch);
		} catch (CoreException e) {
			// OK
		}

		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(
				this);

	}

	public void setProcess(IProcess process) {
		fProcess = process;
	}

	private void setLogging(ILaunch launch) throws CoreException {
		if (launch.getLaunchConfiguration().getAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false)) {
			logging = true;
			loggingConsole = new ConsoleWriter("VDM Debug logging");
		}

	}

	public String getName() throws DebugException {
		return "VdmVM";
	}

	public IProcess getProcess() {
		return fProcess;
	}

	@Override
	public ILaunch getLaunch() {
		return this.fLaunch;
	}

	public IThread[] getThreads() throws DebugException {
		IThread[] result = new IThread[fThreads.size()];
		System.arraycopy(fThreads.toArray(), 0, result, 0, fThreads.size());
		return result;
	}

	public boolean hasThreads() throws DebugException {
		return fThreads.size() > 0;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canTerminate() {
		return !fTerminated;
	}

	public boolean isTerminated() {
		return fTerminated;
	}

	public void terminate() throws DebugException {
		fTerminated = true;
		// proxy.terminate();
		for (IThread thread : fThreads) {
			thread.terminate();
		}
		fProcess.terminate();
		fTerminated = true;
	}

	public boolean canResume() {
		return (fSuspended && !fTerminated);
	}

	public boolean canSuspend() {
		return !fSuspended && !fTerminated;
	}

	public boolean isSuspended() {
		return fSuspended;
	}

	public void resume() throws DebugException {

		fThread.getProxy().resume();
		fSuspended = false;
		fireResumeEvent(DebugEvent.RESUME);

	}

	/**
	 * 
	 * @param detail
	 *            - see DebugEvent detail constants;
	 * @throws DebugException
	 */
	public void suspend() throws DebugException {
		fSuspended = true;

	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		if (fTerminated) {
			return;
		}
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled()) {
					try {

						int line = ((ILineBreakpoint) breakpoint)
								.getLineNumber();
						File file = ((VdmLineBreakpoint) breakpoint).getFile();

						String path = file.getAbsolutePath();

						// TODO: Other OSs paths
						path = path.replace("\\", "/");
						path = "file:/" + path;

						int xid = fThread.getProxy().breakpointAdd(line, path);

						synchronized (breakpointMap) {
							breakpointMap.put(new Integer(xid),
									(VdmLineBreakpoint) breakpoint);
						}

					} catch (CoreException e) {
					}
				}
			} catch (CoreException e) {
			}
		}

	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub

	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub

	}

	public boolean canDisconnect() {
		return false;
	}

	public void disconnect() throws DebugException {
		// TODO Disconnect here
		fDisconected = true;
	}

	public boolean isDisconnected() {
		return fDisconected;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return null;
	}

	public boolean supportsStorageRetrieval() {
		return false;
	}

	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	private void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager().getBreakpoints(
						IDebugConstants.ID_VDM_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}

	private void started() {

		

		fireCreationEvent();
		installDeferredBreakpoints();
		try {
			resume();
		} catch (DebugException e) {
		}

	}

	private void suspended(int breakpoint) {
		fSuspended = true;
		fireSuspendEvent(DebugEvent.BREAKPOINT);

	}

	/**
	 * Notification a breakpoint was encountered. Determine which breakpoint was
	 * hit and fire a suspend event.
	 * 
	 * @param event
	 *            debug event
	 */
	private void breakpointHit(String event) {
		// determine which breakpoint was hit, and set the thread's breakpoint
		int lastSpace = event.lastIndexOf(' ');
		if (lastSpace > 0) {
			String line = event.substring(lastSpace + 1);
			int lineNumber = Integer.parseInt(line);
			IBreakpoint[] breakpoints = DebugPlugin.getDefault()
					.getBreakpointManager().getBreakpoints(
							IDebugConstants.ID_VDM_DEBUG_MODEL);
			for (int i = 0; i < breakpoints.length; i++) {
				IBreakpoint breakpoint = breakpoints[i];
				if (supportsBreakpoint(breakpoint)) {
					if (breakpoint instanceof ILineBreakpoint) {
						ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;
						try {
							if (lineBreakpoint.getLineNumber() == lineNumber) {
								// TODO: is this needed ?
								// fThread.setBreakpoints(new
								// IBreakpoint[]{breakpoint});
								break;
							}
						} catch (CoreException e) {
						}
					}
				}
			}
		}
		suspended(DebugEvent.BREAKPOINT);
	}

	public void newThread(XMLTagNode tagnode, Socket s) throws DebugException {
		String sid = tagnode.getAttr("thread");
		int id = -1;
		// Either "123" or "123 on <CPU name>" for VDM-RT
		int space = sid.indexOf(' ');

		if (space == -1) {
			id = Integer.parseInt(sid);
		} else {
			id = Integer.parseInt(sid.substring(0, space));
		}

		VdmThread t = new VdmThread(this, id, new DebugThreadProxy(s,
				sessionId, id, new CallbackHandler()));

		if (id == 1) { // Assuming 1 is main thread;

			fThread = t; // TODO: assuming main thread is number 1;
			fThread.setName("Thread [Main]");
			fThreads.add(fThread);
			started();
			
		}
		else{
			fThreads.add(fThread);
		}
		// proxy = new DebugThreadProxy(s, sessionId, new Integer(fThread
		// .getId()), new CallbackHandler());
		// proxy.start();

	}

	public void printMessage(boolean outgoing, String message) {
		if (logging) {
			if (outgoing) {
				loggingConsole.ConsolePrint(message, SWT.COLOR_DARK_BLUE);
			} else {
				loggingConsole.ConsolePrint(message, SWT.COLOR_DARK_YELLOW);
			}
			loggingConsole.Show();
		}
	}

	public void printOut(String message) {
		console.ConsolePrint(message);
	}

	public void printErr(String message) {
		console.ConsolePrint(message, SWT.COLOR_RED);
	}

}
