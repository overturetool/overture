package org.overture.codegen.ir;

import java.util.Set;

public class IRStatus
{
	protected Set<NodeInfo> unsupportedNodes;

	public IRStatus(Set<NodeInfo> unsupportedNodes)
	{
		this.unsupportedNodes = unsupportedNodes;
	}
	
	public Set<NodeInfo> getUnsupportedNodes()
	{
		return unsupportedNodes;
	}
	
	public boolean canBeGenerated()
	{
		return unsupportedNodes.size() == 0;
	}
}
