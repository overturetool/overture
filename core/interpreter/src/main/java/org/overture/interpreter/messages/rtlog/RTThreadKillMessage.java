package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.ISchedulableThread;

public class RTThreadKillMessage extends RTThreadMessage
{
	public RTThreadKillMessage(ISchedulableThread thread, CPUResource cpuNumber)
	{
		super(thread, cpuNumber);
	}

	@Override
	String getInnerMessage()
	{
		return "ThreadKill -> id: " + thread.getId() + " cpunm: "
				+ cpuNumber.getNumber();
	}

}
