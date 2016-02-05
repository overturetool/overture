package org.overture.ct.ctruntime.utils;

public class ReductionTestData {

    private String name;
    private TraceReductionInfo reductionInfo;
    private  int expectedResultSize;
    
	public ReductionTestData(String name, TraceReductionInfo reductionInfo, int expectedResultSize)
	{
		super();
		this.name = name;
		this.reductionInfo = reductionInfo;
		this.expectedResultSize = expectedResultSize;
	}

	public String getName()
	{
		return name;
	}

	public TraceReductionInfo getReductionInfo()
	{
		return reductionInfo;
	}

	public int getExpectedResultSize()
	{
		return expectedResultSize;
	}
}
