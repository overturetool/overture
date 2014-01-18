package org.overture.codegen.ooast;

import java.util.Set;

import org.overture.ast.node.INode;

public class OoStatus
{
	protected Set<INode> unsupportedNodes;

	public OoStatus(Set<INode> unsupportedNodes)
	{
		this.unsupportedNodes = unsupportedNodes;
	}
	
	public Set<INode> getUnsupportedNodes()
	{
		return unsupportedNodes;
	}
	
	public boolean canBeGenerated()
	{
		return unsupportedNodes.size() == 0;
	}
}
