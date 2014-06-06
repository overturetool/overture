package org.overture.codegen.ir;

import java.util.Set;

import org.overture.codegen.cgast.SExpCG;

public class ExpStatus extends IRStatus
{
	private SExpCG expCg;
	
	public ExpStatus(SExpCG expCg, Set<NodeInfo> unsupportedNodes)
	{
		super(unsupportedNodes);
		this.expCg = expCg;
	}

	public SExpCG getExpCg()
	{
		return expCg;
	}

}
