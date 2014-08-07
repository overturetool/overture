package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.scheduler.InitThread;
import org.overture.interpreter.scheduler.MainThread;
import org.overture.interpreter.values.ObjectValue;

public abstract class RTThreadMessage extends RTMessage
{
	public ISchedulableThread thread;
	public CPUResource cpuNumber;

	String objRef;
	String clnm = "nil";

	public RTThreadMessage(ISchedulableThread thread, CPUResource cpuNumber)
	{
		this.thread = thread;
		this.cpuNumber = cpuNumber;

	}

	protected String objRefString(ISchedulableThread thread)
	{
		try
		{
			ObjectValue obj = thread.getObject();

			if (obj == null && objRef == null)
			{
				objRef = "nil";
				if (thread instanceof InitThread)
				{
					clnm = "INIT";
					objRef = getStaticId(clnm, cpuNumber).toString();
				} else if (thread instanceof MainThread)
				{
					clnm = "ENTRY";
					objRef = getStaticId(clnm, cpuNumber).toString();
				}
			} else if (objRef == null)
			{
				objRef = "" + obj.objectReference;
				clnm = obj.type.toString();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return " objref: " + objRef + " clnm: \"" + clnm + "\"";
	}

	@Override
	public void generateStaticDeploys()
	{
		ObjectValue obj = thread.getObject();
		try
		{
			if (obj == null)
			{
				if (thread instanceof InitThread)
				{
					clnm = "INIT";
					objRef = getStaticId(clnm, cpuNumber).toString();
				} else if (thread instanceof MainThread)
				{
					clnm = "ENTRY";
					objRef = getStaticId(clnm, cpuNumber).toString();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public ISchedulableThread getThread()
	{
		return this.thread;
	}
}
