package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;


public class VarOccurencesCollector extends DepthFirstAnalysisAdaptor
{
	private ILexLocation defLoc;
	private Set<AVariableExp> varOccurences;
	
	public VarOccurencesCollector(ILexLocation defLoc)
	{
		this.defLoc = defLoc;
		this.varOccurences = new HashSet<AVariableExp>();
	}

	public Set<AVariableExp> getVars()
	{
		return varOccurences;
	}
	
	@Override
	public void caseAVariableExp(AVariableExp node) throws AnalysisException
	{
		if(node.getVardef() == null)
		{
			return;
		}
		
		if(node.getVardef().getLocation().equals(defLoc))
		{
			varOccurences.add(node);
		}
	}
}
