package org.overture.codegen.traces;

import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;

public class TraceNodeData
{
	private AIdentifierVarExpIR nodeVar;
	private ABlockStmIR stms;
	private ABlockStmIR nodeVarScope;

	public TraceNodeData(AIdentifierVarExpIR nodeVar, ABlockStmIR stms,
			ABlockStmIR nodeVarScope)
	{
		this.nodeVar = nodeVar;
		this.stms = stms;
		this.nodeVarScope = nodeVarScope;
	}

	public AIdentifierVarExpIR getNodeVar()
	{
		return nodeVar;
	}

	public ABlockStmIR getStms()
	{
		return stms;
	}

	public ABlockStmIR getNodeVarScope()
	{
		return nodeVarScope;
	}
}
