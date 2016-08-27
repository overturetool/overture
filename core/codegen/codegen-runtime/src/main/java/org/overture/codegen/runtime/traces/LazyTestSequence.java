package org.overture.codegen.runtime.traces;

import java.util.Iterator;

public class LazyTestSequence extends TestSequence
{
	private static final long serialVersionUID = -8449551289160946386L;

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
	public synchronized boolean isEmpty()
	{
		return !iterator().hasNext();
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
				CallSequence test = LazyTestSequence.this.get(index++);

				markFiltered(test);

				return test;
			}

			@Override
			public void remove()
			{
				// Not supported
			}
		};
	}
}