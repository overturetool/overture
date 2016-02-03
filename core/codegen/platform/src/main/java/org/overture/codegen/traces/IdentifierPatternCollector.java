package org.overture.codegen.traces;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.logging.Logger;

public class IdentifierPatternCollector extends DepthFirstAnalysisAdaptor
{
	private INode topNode;
	private List<AIdentifierPatternCG> idOccurences;
	
	public IdentifierPatternCollector()
	{
		this.topNode = null;
		this.idOccurences = null;
	}

	public void setTopNode(INode topNode)
	{
		this.topNode = topNode;
	}
	
	public List<AIdentifierPatternCG> findOccurences()
	{
		idOccurences = new LinkedList<AIdentifierPatternCG>();
		
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
