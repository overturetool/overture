package org.overture.ide.debug.core.model;

import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;
import org.overture.ide.debug.utils.xml.XMLOpenTagNode;

public class VdmThread extends VdmDebugElement implements IThread{

	private String fName;
	private ArrayList<IStackFrame> fFrames;
	private int id;
	private DebugThreadProxy proxy;
	
	private boolean fSuspended = false;
	private boolean fTerminated = false;
	
	public VdmThread(VdmDebugTarget target,int id, DebugThreadProxy proxy) {
		super(target);
		this.id = id;
		fFrames = new ArrayList<IStackFrame>();
		this.proxy = proxy;
		this.proxy.start();
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
		//return (IStackFrame[]) fFrames.toArray(new IStackFrame[fFrames.size()]);
		try
		{
			VdmStackFrame[] fs= proxy.getStack();
			for (VdmStackFrame f : fs)
			{
				f.setDebugTarget(fTarget);
				f.setThread(this,proxy);
			}
			return fs;
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasStackFrames() throws DebugException {		
		
		try
		{
			Integer s = proxy.getStackDepth();
			System.out.println("Stack depth is: "+s);
			return s>0;
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fFrames.size() > 0;
	}

	public boolean canResume() {
		return fSuspended && !fTerminated;
	}

	public boolean canSuspend() {
		return !fSuspended && !fTerminated;
	}

	public boolean isSuspended() {
		return fSuspended;
	}

	public void resume() throws DebugException {
		fSuspended = false;
	}

	public void suspend() throws DebugException {
		proxy.setVdmThread(this);
//		getStackMsg();
		fSuspended = true;
		
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
		return !fTerminated;
	}

	public boolean isTerminated() {
		return fTerminated;
	}

	public void terminate() throws DebugException {
		fTerminated = true;
	}
	
	public void setName(String name){
		fName = name;
	}

	public int getId() {
		return id;
	}

	public DebugThreadProxy getProxy(){
		return proxy;
	}
	
//	private void getStackMsg(){
//		proxy.getStack();
//	}

//	public void setStackFrame(XMLOpenTagNode node) {
//		System.out.println(node.toString());
//		
//	}
	
	
}
