package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenThreadSwapEvent extends NextGenThreadEvent implements
		Serializable, INextGenEvent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1987896851673961008L;

	public enum ThreadEventSwapType
	{
		SWAP_IN, DELAYED_IN, SWAP_OUT
	}

	public ThreadEventSwapType swapType;
	public int overhead;
	public long delay;

	public NextGenThreadSwapEvent(NextGenThread thread, NextGenTimeStamp time,
			ThreadEventSwapType swapType, int overhead, long delay)
	{
		super(thread, time, ThreadEventType.SWAP);
		this.swapType = swapType;
		this.overhead = overhead;
		this.delay = delay;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append("Thread");
		switch (this.swapType)
		{
			case SWAP_IN:
				s.append("SwapIn");
				break;
			case DELAYED_IN:
				s.append("DelayedIn");
				break;
			case SWAP_OUT:
				s.append("SwapOut");
				break;
			default:
				break;
		}

		s.append(" -> id:");
		s.append(this.thread.id);
		s.append(" objref: ");
		s.append(this.thread.object == null ? "no object"
				: this.thread.object.id);
		s.append(" clnm: ");
		s.append(this.thread.object == null ? "no object"
				: this.thread.object.classDef.name);
		s.append(" overhead: ");
		s.append(this.overhead);
		s.append(" time: ");
		s.append(time.toString());

		return s.toString();
	}

}
