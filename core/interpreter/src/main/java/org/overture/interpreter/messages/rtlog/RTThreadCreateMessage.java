package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.ISchedulableThread;

public class RTThreadCreateMessage extends RTThreadMessage
{
	public RTThreadCreateMessage(ISchedulableThread thread,
			CPUResource cpuNumber)
	{
		super(thread, cpuNumber);
	}

	@Override
	String getInnerMessage()
	{
		// TODO: Change show trace to allow thread name to allow easier inspection of text log file //+
		// " name: "+th.getName());
		return "ThreadCreate -> id: " + thread.getId() + " period: "
				+ thread.isPeriodic() + objRefString(thread) + " cpunm: "
				+ cpuNumber.getNumber();
	}

}
