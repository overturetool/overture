package org.overture.interpreter.traces.util;

import java.util.Iterator;

import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.traces.RepeatTraceNode;
import org.overture.interpreter.traces.TestSequence;

public class LazyTestSequence extends TestSequence
{
	/**
	 * serial
	 */
	private static final long serialVersionUID = 1L;
	private RepeatTraceNode repeatTraceNode;

	public LazyTestSequence(RepeatTraceNode repeatTraceNode)
	{
		this.repeatTraceNode = repeatTraceNode;
	}

	@Override
	public synchronized int size()
	{
		return this.repeatTraceNode.size2();
	}

	@Override
	public synchronized CallSequence get(int index)
	{
		return this.repeatTraceNode.get(index);
	}

	@Override
	public synchronized Iterator<CallSequence> iterator()
	{
		return new Iterator<CallSequence>()
		{
			int index = 0;

			@Override
			public boolean hasNext()
			{
				return index < LazyTestSequence.this.size();
			}

			@Override
			public CallSequence next()
			{
				return LazyTestSequence.this.get(index++);
			}

			@Override
			public void remove()
			{

			}
		};
	}
}