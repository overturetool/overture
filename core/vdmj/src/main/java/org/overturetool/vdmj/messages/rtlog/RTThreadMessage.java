package org.overturetool.vdmj.messages.rtlog;

import org.overturetool.vdmj.scheduler.CPUResource;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.InitThread;
import org.overturetool.vdmj.scheduler.MainThread;
import org.overturetool.vdmj.values.ObjectValue;

public abstract class RTThreadMessage extends RTMessage
{
	protected ISchedulableThread thread;
	protected CPUResource cpuNumber;
	
	String objRef;
	String clnm ="nil";
	
	public RTThreadMessage(ISchedulableThread thread, CPUResource cpuNumber)
	{
		this.thread = thread;
		this.cpuNumber = cpuNumber;
		
	}
	
	protected String objRefString(ISchedulableThread thread)
	{
		ObjectValue obj = thread.getObject(); 
		
		if(obj == null && objRef == null)
		{
			objRef = "nil";
			if(thread instanceof InitThread)
			{
				clnm = "INIT";
				objRef = getStaticId(clnm).toString();
			}else if(thread instanceof MainThread)
			{
				clnm = "ENTRY";
				objRef = getStaticId(clnm).toString();
			}
		}else if (objRef ==null)
		{
			objRef = ""+obj.objectReference;
			clnm =  obj.type.toString() ;
		}
		
		
		return " objref: " + objRef + " clnm: \"" + clnm+"\"";
	}
	
	@Override
	public void generateStaticDeploys()
	{
		ObjectValue obj = thread.getObject(); 
		if(obj == null)
		{
			if(thread instanceof InitThread)
			{
				clnm = "INIT";
				objRef = getStaticId(clnm).toString();
			}else if(thread instanceof MainThread)
			{
				clnm = "ENTRY";
				objRef = getStaticId(clnm).toString();
			}
		}
	}
	
	public ISchedulableThread getThread()
	{
		return this.thread;
	}
}
