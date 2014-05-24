package org.overture.codegen.ir;

import java.util.Set;

import org.overture.codegen.cgast.expressions.PExpCG;

public class ExpStatus extends IRStatus
{
	private PExpCG expCg;
	
	public ExpStatus(PExpCG expCg, Set<NodeInfo> unsupportedNodes)
	{
		super(unsupportedNodes);
		this.expCg = expCg;
	}

	public PExpCG getExpCg()
	{
		return expCg;
	}

}
