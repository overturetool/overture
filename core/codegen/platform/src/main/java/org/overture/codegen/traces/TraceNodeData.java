package org.overture.codegen.traces;

import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.statements.ABlockStmCG;

public class TraceNodeData
{
	private AIdentifierVarExpCG nodeVar;
	private ABlockStmCG stms;
	private ABlockStmCG nodeVarScope;
	
	public TraceNodeData(AIdentifierVarExpCG nodeVar, ABlockStmCG stms, ABlockStmCG nodeVarScope)
	{
		this.nodeVar = nodeVar;
		this.stms = stms;
		this.nodeVarScope = nodeVarScope;
	}

	public AIdentifierVarExpCG getNodeVar()
	{
		return nodeVar;
	}

	public ABlockStmCG getStms()
	{
		return stms;
	}

	public ABlockStmCG getNodeVarScope()
	{
		return nodeVarScope;
	}
}
