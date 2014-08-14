package org.overture.ct.ctruntime.tests.util;

import org.overture.interpreter.traces.TraceReductionType;

public class TraceReductionInfo
{
	private float subset;
	private TraceReductionType reductionType;
	private long seed;

	public TraceReductionInfo()
	{
		this.subset = 1.0F;
		this.reductionType = TraceReductionType.NONE;
		this.seed = 999;
	}

	public TraceReductionInfo(float subset, TraceReductionType reductionType,
			long seed)
	{
		this.subset = subset;
		this.reductionType = reductionType;
		this.seed = seed;
	}

	public float getSubset()
	{
		return subset;
	}

	public TraceReductionType getReductionType()
	{
		return reductionType;
	}

	public long getSeed()
	{
		return seed;
	}

	@Override
	public String toString()
	{
		return String.format("%.2f", subset * 100) + "%" + reductionType;
	}
}
