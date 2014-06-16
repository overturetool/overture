package org.overture.codegen.ir;

import java.util.Set;

import org.overture.codegen.cgast.SExpCG;

public class IRExpStatus extends IRStatus
{
	private SExpCG expCg;
	
	public IRExpStatus(SExpCG expCg, Set<NodeInfo> unsupportedNodes)
	{
		super(unsupportedNodes);
		this.expCg = expCg;
	}

	public SExpCG getExpCg()
	{
		return expCg;
	}

}
