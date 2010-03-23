package org.overture.ide.debug.core.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

public class VdmDebugTarget extends VdmDebugElement implements IDebugTarget {

	
	private ILaunch fLaunch;
	private IProcess fProcess;
	private List<IThread> fThreads;
	private IThread fThread;
	
	public VdmDebugTarget(ILaunch launch, IProcess process, Socket s) {
		super(null);
		fTarget = this;
		fLaunch = launch;
		fProcess = process;
		
		fThread = new VdmThread(this);
		fThreads = new ArrayList<IThread>();
		fThreads.add(fThread);
		
	}
	
	public String getName() throws DebugException {
		return "VdmVM";
	}

	public IProcess getProcess() {
		return fProcess;
	}
	
	public ILaunch getLaunch(){
		return this.fLaunch;
	}

	public IThread[] getThreads() throws DebugException {
		IThread[] result = new IThread[fThreads.size()];
		System.arraycopy(fThreads.toArray(), 0, result, 0, fThreads.size());
		return result;
	}

	public boolean hasThreads() throws DebugException {
		return fThreads.size()>0;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canTerminate() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	public void terminate() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	public boolean canResume() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canSuspend() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	public void resume() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	public void suspend() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
		
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
		
	}

	public boolean canDisconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	public void disconnect() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean supportsStorageRetrieval() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
