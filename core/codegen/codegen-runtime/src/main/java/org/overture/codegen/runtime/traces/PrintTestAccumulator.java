package org.overture.codegen.runtime.traces;

public class PrintTestAccumulator implements TestAccumulator
{
	private static final long serialVersionUID = 2360833370013107375L;

	@Override
	public void registerTest(TraceTest test)
	{
		System.out.println(test);
	}
}
