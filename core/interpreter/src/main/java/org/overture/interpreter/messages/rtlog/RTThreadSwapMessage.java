package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.ISchedulableThread;

public class RTThreadSwapMessage extends RTThreadMessage
{
	public enum SwapType
	{
		In, DelayedIn, Out
	}

	public SwapType type;
	public int overhead;
	public long delay;

	public RTThreadSwapMessage(SwapType type, ISchedulableThread thread,
			CPUResource cpuNumber, int overhead, long delay)
	{
		super(thread, cpuNumber);
		this.type = type;
		this.thread = thread;
		this.cpuNumber = cpuNumber;
		this.overhead = overhead;
		this.delay = delay;
	}

	@Override
	String getInnerMessage()
	{
		String objRefString = objRefString(thread);
		switch (type)
		{
			case In:
				return "ThreadSwapIn -> id: " + thread.getId() + objRefString
						+ " cpunm: " + cpuNumber.getNumber() + " overhead: "
						+ overhead;
			case DelayedIn:
				return "DelayedThreadSwapIn -> id: " + thread.getId()
						+ objRefString + " delay: " + delay + " cpunm: "
						+ cpuNumber.getNumber() + " overhead: " + overhead;
			case Out:
				return "ThreadSwapOut -> id: " + thread.getId() + objRefString
						+ " cpunm: " + cpuNumber.getNumber() + " overhead: "
						+ overhead;
			default:
				return null;

		}

	}

	public SwapType getType()
	{
		return this.type;
	}

}
