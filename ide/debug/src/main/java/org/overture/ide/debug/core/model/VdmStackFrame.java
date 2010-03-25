package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;

public class VdmStackFrame extends VdmDebugElement implements IStackFrame
{

	private int charEnd;
	private int charStart;
	private int lineNumber;
	private int level;
	private String name;
	private IThread thread;
	DebugThreadProxy proxy;

	public VdmStackFrame(VdmDebugTarget target, String name, int charStart,
			int charEnd, int lineNumber, int level) {
		super(target);
		this.charEnd = charEnd;
		this.charStart = charStart;
		this.lineNumber = lineNumber;
		this.name = name;
		this.level = level;
	}

	public void setDebugTarget(VdmDebugTarget target)
	{
		super.fTarget = target;
	}

	public void setThread(IThread thread, DebugThreadProxy proxy)
	{
		this.thread = thread;
		this.proxy = proxy;
	}

	public int getCharEnd() throws DebugException
	{
		return charEnd;
	}

	public int getCharStart() throws DebugException
	{
		return charStart;
	}

	public int getLineNumber() throws DebugException
	{
		return lineNumber;
	}

	public String getName() throws DebugException
	{
		return name;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IThread getThread()
	{
		return thread;
	}

	public IVariable[] getVariables() throws DebugException
	{
		try
		{
			return proxy.getVariables(level);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public boolean hasRegisterGroups() throws DebugException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasVariables() throws DebugException
	{
		// TODO Auto-generated method stub
		return true;
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

	public boolean canResume()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canSuspend()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSuspended()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void resume() throws DebugException
	{
		// TODO Auto-generated method stub

	}

	public void suspend() throws DebugException
	{
		// TODO Auto-generated method stub

	}

	public boolean canTerminate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTerminated()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void terminate() throws DebugException
	{
		// TODO Auto-generated method stub

	}

}
