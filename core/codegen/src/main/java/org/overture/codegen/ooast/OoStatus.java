package org.overture.codegen.ooast;

import java.util.Set;

public class OoStatus
{
	protected Set<NodeInfo> unsupportedNodes;

	public OoStatus(Set<NodeInfo> unsupportedNodes)
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
