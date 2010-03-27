package org.overture.ide.debug.core.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.utils.communication.DebugThreadProxy;

public class VdmStackFrame extends VdmDebugElement implements IStackFrame
{

	private int charEnd=-1;
	private int charStart=-1;
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
		if(charEnd==-1)
		{
			return charStart+1;
		}
		return charEnd;
	}

	public int getCharStart() throws DebugException
	{
		//return charStart;
		return -1;
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
		Map<String,Integer> contextNames = proxy.getContextNames();
		
		List<IVariable> variables = new Vector<IVariable>();
		
		for (String name : contextNames.keySet())
		{
			if(contextNames.get(name)!=0)
			{
			VdmVariable v = new VdmVariable(null, name, "",new VdmValue(null,"","",proxy.getVariables(level, contextNames.get(name))));
			variables.add(v);
			}else
			{
				//get locals
				for (IVariable iVariable : proxy.getVariables(level,0))
				{
					variables.add(iVariable);
				}
				
			}
		}
		return variables.toArray(new IVariable[variables.size()]);
	}

	public boolean hasRegisterGroups() throws DebugException
	{
		return false;
	}

	public boolean hasVariables() throws DebugException
	{
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

	public String getSourceName() {
		if(nameIsFileUri){
			int i = name.lastIndexOf('/');
			String fileName =  name.substring(i+1);
			return fileName;
		}
		return null;
	}

}
