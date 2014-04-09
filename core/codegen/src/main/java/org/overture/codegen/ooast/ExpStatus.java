package org.overture.codegen.ooast;

import java.util.Set;

import org.overture.codegen.cgast.expressions.PExpCG;

public class ExpStatus extends OoStatus
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
