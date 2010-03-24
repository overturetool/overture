package org.overture.ide.debug.core.model;

import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class VdmThread extends VdmDebugElement implements IThread{

	private String fName;
	private ArrayList<IStackFrame> fFrames;
	private int id;
	
	public VdmThread(VdmDebugTarget target,int id) {
		super(target);
		this.id = id;
		fFrames = new ArrayList<IStackFrame>();
		// TODO Auto-generated constructor stub
	}

	public IBreakpoint[] getBreakpoints() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() throws DebugException {
		return fName;
	}

	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasStackFrames() throws DebugException {		
		return fFrames.size() > 0;
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

	public boolean canStepInto() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepOver() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepReturn() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStepping() {
		// TODO Auto-generated method stub
		return false;
	}

	public void stepInto() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	public void stepOver() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	public void stepReturn() throws DebugException {
		// TODO Auto-generated method stub
		
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
	
	public void setName(String name){
		fName = name;
	}

	public int getId() {
		return id;
	}

	

}
