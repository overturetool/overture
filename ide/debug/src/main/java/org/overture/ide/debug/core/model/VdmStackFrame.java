package org.overture.ide.debug.core.model;

import java.io.File;

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
	private String where;
	private IThread thread;
	DebugThreadProxy proxy;
	boolean nameIsFileUri=false;

	public VdmStackFrame(VdmDebugTarget target, String name,boolean nameIsFileUri ,int charStart,
			int charEnd, int lineNumber, int level,String where) {
		super(target);
		this.charEnd = charEnd;
		this.charStart = charStart;
		this.lineNumber = lineNumber;
		this.name = name;
		this.level = level;
		this.nameIsFileUri = nameIsFileUri;
		this.where = where;
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
		String w="";
		if(where!=null && where.trim().length()>0)
		{
			w = " ("+where+") ";
		}
		if(nameIsFileUri)
		{
			return new File(name).getName()+w+" line: "+ lineNumber;
		}
		return name+w;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		return new IRegisterGroup[0];
	}

	public IThread getThread()
	{
		return thread;
	}

	public IVariable[] getVariables() throws DebugException
	{
		return proxy.getVariables(level);
	}

	public boolean hasRegisterGroups() throws DebugException
	{
		return false;
	}

	public boolean hasVariables() throws DebugException
	{
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canStepInto()
	{
		return false;
	}

	public boolean canStepOver()
	{
		return false;
	}

	public boolean canStepReturn()
	{
		return false;
	}

	public boolean isStepping()
	{
		return false;
	}

	public void stepInto() throws DebugException
	{
	}

	public void stepOver() throws DebugException
	{
	}

	public void stepReturn() throws DebugException
	{
	}

	public boolean canResume()
	{
		return false;
	}

	public boolean canSuspend()
	{
		return false;
	}

	public boolean isSuspended()
	{
		return false;
	}

	public void resume() throws DebugException
	{
	}

	public void suspend() throws DebugException
	{
	}

	public boolean canTerminate()
	{
		return false;
	}

	public boolean isTerminated()
	{
		return false;
	}

	public void terminate() throws DebugException
	{
	}

}
