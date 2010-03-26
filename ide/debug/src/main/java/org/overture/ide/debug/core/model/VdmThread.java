package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;

public class VdmThread extends VdmDebugElement implements IThread
{

	private String fName;
	private int id;
	private DebugThreadProxy proxy;

	private boolean fSuspended = false;
	private boolean fTerminated = false;

	public VdmThread(VdmDebugTarget target, int id, DebugThreadProxy proxy) {
		super(target);
		this.id = id;
		this.proxy = proxy;
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
		if (isSuspended())
		{
			VdmStackFrame[] frames = proxy.getStack();
			for (VdmStackFrame f : frames)
			{
				f.setDebugTarget(fTarget);
				f.setThread(this, proxy);
			}
			return frames;
		} else
		{
			return new IStackFrame[0];
		}
	}

	public IStackFrame getTopStackFrame() throws DebugException
	{
		if (isSuspended())
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

		Integer s = proxy.getStackDepth();
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
	}

	public void suspend() throws DebugException
	{
		fSuspended = true;

	}

	public boolean canStepInto()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepOver()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepReturn()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStepping()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void stepInto() throws DebugException
	{
		// TODO Auto-generated method stub

	}

	public void stepOver() throws DebugException
	{
		// TODO Auto-generated method stub

	}

	public void stepReturn() throws DebugException
	{
		// TODO Auto-generated method stub

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
		fTerminated = true;
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
}
