package org.overture.isapog;

public class IsaPogResult
{
	final String modelthy;
	final String posthy;
	final boolean negative;

	public IsaPogResult(String modelthy, String posthy, boolean negative)
	{
		super();
		this.modelthy = modelthy;
		this.posthy = posthy;
		this.negative = negative;
	}

	public String getModelthy()
	{
		return modelthy;
	}

	public String getPosthy()
	{
		return posthy;
	}

	public boolean isNegative()
	{
		return negative;
	}

}
