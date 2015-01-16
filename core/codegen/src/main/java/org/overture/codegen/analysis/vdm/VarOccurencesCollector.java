package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;


public class VarOccurencesCollector extends DepthFirstAnalysisAdaptor
{
	private ILexLocation defLoc;
	private Set<AVariableExp> varOccurences;
	private List<? extends PDefinition> defsOutsideScope;
	
	public VarOccurencesCollector(ILexLocation defLoc)
	{
		this(defLoc, null);
	}
	
	public VarOccurencesCollector(ILexLocation defLoc, List<? extends PDefinition> defsOutsideScope)
	{
		this.defLoc = defLoc;
		this.varOccurences = new HashSet<AVariableExp>();
		this.defsOutsideScope = defsOutsideScope;
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
		
		if (defsOutsideScope != null)
		{
			for (PDefinition d : defsOutsideScope)
			{
				if (d == node.getVardef())
				{
					return;
				}
			}
		}
		
		if(node.getVardef().getLocation().equals(defLoc))
		{
			varOccurences.add(node);
		}
	}
}
