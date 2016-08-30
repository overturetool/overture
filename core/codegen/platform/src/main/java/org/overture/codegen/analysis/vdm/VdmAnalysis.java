package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.node.INode;

public abstract class VdmAnalysis extends DepthFirstAnalysisAdaptor
{
	protected INode topNode;

	public VdmAnalysis(INode topNode)
	{
		this.topNode = topNode;
	}

	protected boolean proceed(INode node)
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
			visited.add(parent);
			parent = parent.parent();
		}

		return this.topNode == parent;
	}
}