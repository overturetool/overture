package org.overture.codegen.traces;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;

public class IdentifierPatternCollector extends DepthFirstAnalysisAdaptor
{
	private INode topNode;
	private List<AIdentifierPatternIR> idOccurences;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public IdentifierPatternCollector()
	{
		this.topNode = null;
		this.idOccurences = null;
	}

	public void setTopNode(INode topNode)
	{
		this.topNode = topNode;
	}

	public List<AIdentifierPatternIR> findOccurences()
	{
		idOccurences = new LinkedList<AIdentifierPatternIR>();

		try
		{
			topNode.apply(this);
		} catch (AnalysisException e)
		{
			log.error("Could not find identifier pattern occurences for node: "
					+ topNode);
			e.printStackTrace();
		}

		return idOccurences;
	}

	@Override
	public void caseAIdentifierPatternIR(AIdentifierPatternIR node)
			throws AnalysisException
	{
		if (proceed(node))
		{
			idOccurences.add(node);
		}
	}

	private boolean proceed(INode node)
	{
		if (topNode == null)
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
