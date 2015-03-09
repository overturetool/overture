package org.overture.codegen.traces;

import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;

public class TraceNodeData
{
	private AIdentifierVarExpCG nodeVar;
	private ABlockStmCG stms;
	
	public TraceNodeData(AIdentifierVarExpCG nodeVar, ABlockStmCG stms)
	{
		this.nodeVar = nodeVar;
		this.stms = stms;
	}

	public AIdentifierVarExpCG getNodeVar()
	{
		return nodeVar;
	}

	public ABlockStmCG getStms()
	{
		return stms;
	}	
}
