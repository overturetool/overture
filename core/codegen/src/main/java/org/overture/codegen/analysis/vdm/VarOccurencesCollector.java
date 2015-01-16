package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;


public class VarOccurencesCollector extends DepthFirstAnalysisAdaptor
{
	private PDefinition def;
	private Set<AVariableExp> varOccurences;
	private List<? extends PDefinition> defsOutsideScope;
	
	public VarOccurencesCollector(PDefinition def, List<? extends PDefinition> defsOutsideScope)
	{
		this.def = def;
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
		
		for(PDefinition d : defsOutsideScope)
		{
			if(d == node.getVardef())
			{
				return;
			}
		}
		
		if(node.getVardef().getLocation().equals(def.getLocation()))
		{
			varOccurences.add(node);
		}
	}
}
