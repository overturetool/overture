package org.overturetool.vdmj.messages.rtlog;

import org.overturetool.vdmj.scheduler.CPUResource;
import org.overturetool.vdmj.scheduler.ISchedulableThread;

public class RTThreadCreateMessage extends RTThreadMessage
{
	public RTThreadCreateMessage(ISchedulableThread thread, CPUResource cpuNumber)
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
