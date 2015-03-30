package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;

public class IdOccurencesCollector extends VdmAnalysis
{
	private ILexNameToken name;
	private Set<AIdentifierPattern> idOccurences;

	public IdOccurencesCollector(ILexNameToken name, INode topNode)
	{
		super(topNode);
		this.name = name;
		this.idOccurences = new HashSet<AIdentifierPattern>();
	}

	public Set<AIdentifierPattern> getIdOccurences()
	{
		return idOccurences;
	}

	@Override
	public void caseAIdentifierPattern(AIdentifierPattern node)
			throws AnalysisException
	{
		if(proceed(node))
		{
			if (node.getName().equals(name))
			{
				idOccurences.add(node);
			}
		}
	}
}
