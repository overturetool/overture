package org.overture.ide.plugins.combinatorialtesting.views;

import org.overture.interpreter.traces.TraceReductionType;

public class TraceOptionsDisplayState
{
	private int subset = -1;
	private int seed = -1;
	private String reductionType;
	
	public TraceOptionsDisplayState(int subset, int seed, String reductionType)
	{
		super();
		this.subset = subset;
		this.seed = seed;
		this.reductionType = reductionType;
	}

	public int getSubset()
	{
		return subset;
	}

	public int getSeed()
	{
		return seed;
	}

	public String getReductionType()
	{
		return reductionType;
	}
	
}
