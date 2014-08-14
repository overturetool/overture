package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenTimeStamp implements Serializable,
		Comparable<NextGenTimeStamp>
{
	private static final long serialVersionUID = 5818819583183722218L;

	private long absoluteTime; // Time compared to wall clock
	private int relativeTime; // Sequence no. among events happening at same wall clock time

	public NextGenTimeStamp(long absoluteTime, int relativeTime)
	{
		this.absoluteTime = absoluteTime;
		this.relativeTime = relativeTime;
	}

	public long getAbsoluteTime()
	{
		return absoluteTime;
	}

	public int getRelativeTime()
	{
		return relativeTime;
	}

	@Override
	public String toString()
	{
		return absoluteTime + "-" + relativeTime;
	}

	public int compareTo(NextGenTimeStamp o)
	{
		if (this.absoluteTime != o.absoluteTime)
		{
			return (int) (this.absoluteTime - o.absoluteTime);
		} else
		{
			return this.relativeTime - o.relativeTime;
		}
	}

}
