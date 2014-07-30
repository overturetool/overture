package org.overture.interpreter.traces.util;

import java.util.Iterator;

import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.traces.IIterableTraceNode;
import org.overture.interpreter.traces.TestSequence;

public class LazyTestSequence extends TestSequence
{
	/**
	 * serial
	 */
	private static final long serialVersionUID = 1L;
	private IIterableTraceNode node;

	public LazyTestSequence(IIterableTraceNode node)
	{
		this.node = node;
	}

	@Override
	public synchronized int size()
	{
		return this.node.size();
	}

	@Override
	public synchronized CallSequence get(int index)
	{
		return this.node.get(index);
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