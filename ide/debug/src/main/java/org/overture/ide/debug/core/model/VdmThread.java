package org.overture.ide.debug.core.model;

import java.io.IOException;
import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;
import org.overturetool.vdmj.runtime.DebuggerException;

public class VdmThread extends VdmDebugElement implements IThread
{

	private String fName;
	private int id;
	private DebugThreadProxy proxy;

	private boolean fSuspended = false;
	private boolean fTerminated = false;
	private boolean fIsStepping = false;

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
		if(fTerminated){
			return false;
		}
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
		
		proxy.resume();
		fSuspended = false;
		fIsStepping = false;
		
		
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
		return false;//!fTerminated;
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
