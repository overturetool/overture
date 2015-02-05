package org.overture.codegen.traces;

import java.util.HashSet;
import java.util.Set;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.logging.Logger;

public class IdentifierPatternCollector extends DepthFirstAnalysisAdaptor
{
	private INode topNode;
	private Set<AIdentifierPatternCG> idOccurences;
	
	public IdentifierPatternCollector()
	{
		this.topNode = null;
		this.idOccurences = null;
	}

	public void setTopNode(INode topNode)
	{
		this.topNode = topNode;
	}
	
	public Set<AIdentifierPatternCG> findOccurences()
	{
		this.idOccurences = new HashSet<AIdentifierPatternCG>();
		
		try
		{
			topNode.apply(this);
		} catch (AnalysisException e)
		{
			Logger.getLog().printErrorln("Problems finding identifier "
					+ "pattern occurences in 'IdentifierPatternCollector' for node: " + topNode);
			e.printStackTrace();
		}
		
		return idOccurences;
	}

	@Override
	public void caseAIdentifierPatternCG(AIdentifierPatternCG node)
			throws AnalysisException
	{
		if (proceed(node))
		{
			idOccurences.add(node);
		}
	}

	private boolean proceed(INode node)
	{
		if(topNode == null)
		{
			return false;
		}
		
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
