package org.overture.codegen.runtime.traces;

public class IdGenerator
{
	private int i;

	public IdGenerator()
	{
		reset();
	}

	public void reset()
	{
		i = 0;
	}

	public int inc()
	{
		return ++i;
	}

	public int value()
	{
		return i;
	}
}
