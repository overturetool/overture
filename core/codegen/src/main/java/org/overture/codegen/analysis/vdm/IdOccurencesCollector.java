package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;

public class IdOccurencesCollector extends DepthFirstAnalysisAdaptor
{
	private ILexNameToken name;
	private INode topNode;
	private Set<AIdentifierPattern> idOccurences;

	public IdOccurencesCollector(ILexNameToken name, INode topNode)
	{
		this.name = name;
		this.topNode = topNode;
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
		if (node == topNode)
		{
			return true;
		}

		INode parent = node.parent();
		
		Set<INode> visited = new HashSet<INode>();

		while (parent != null && !visited.contains(parent)
				&& this.topNode != parent)
		{
			parent = parent.parent();
			visited.add(parent);
		}

		return this.topNode == parent;
	}
}
