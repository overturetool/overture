package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;

public class IdOccurencesCollector extends DepthFirstAnalysisAdaptor
{
	private ILexNameToken name;
	private PDefinition def;
	private Set<AIdentifierPattern> idOccurences;

	public IdOccurencesCollector(ILexNameToken name, PDefinition def)
	{
		this.name = name;
		this.def = def;
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

	private boolean proceed(INode node)
	{
		if (node == def)
		{
			return true;
		}

		INode parent = node.parent();

		while (parent != null && !(parent instanceof AClassClassDefinition)
				&& parent != def)
		{
			parent = parent.parent();
		}

		return parent == def;
	}
}
