package org.overture.codegen.runtime.traces;

import java.util.LinkedList;
import java.util.List;

public class InMemoryTestAccumulator implements TestAccumulator
{
	private static final long serialVersionUID = 1898551533520957210L;

	private List<TraceTest> tests;

	private int nextIdx;

	public InMemoryTestAccumulator()
	{
		this.tests = new LinkedList<TraceTest>();
		this.nextIdx = 0;
	}

	@Override
	public void registerTest(TraceTest test)
	{
		this.tests.add(test);
	}

	public boolean hasNext()
	{
		return nextIdx < tests.size();
	}

	public TraceTest getNext()
	{
		return tests.get(nextIdx++);
	}

	public List<TraceTest> getAllTests()
	{
		return tests;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (TraceTest t : tests)
		{
			sb.append(t.toString()).append('\n');
		}

		return sb.toString();
	}
}
