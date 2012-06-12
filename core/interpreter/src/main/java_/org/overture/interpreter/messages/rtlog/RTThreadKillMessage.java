package org.overture.interpreter.messages.rtlog;

import org.overturetool.vdmj.scheduler.CPUResource;
import org.overturetool.vdmj.scheduler.ISchedulableThread;

public class RTThreadKillMessage extends RTThreadMessage
{
	public RTThreadKillMessage(ISchedulableThread thread, CPUResource cpuNumber)
	{
		super(thread, cpuNumber);
	}

	@Override
	String getInnerMessage()
	{
		return "ThreadKill -> id: " + thread.getId() + " cpunm: " + cpuNumber.getNumber();
	}

}
