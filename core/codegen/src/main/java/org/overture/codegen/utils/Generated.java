package org.overture.codegen.utils;

import java.util.Set;

import org.overture.codegen.ooast.NodeInfo;

public class Generated
{
	protected String content;
	protected Set<NodeInfo> unsupportedNodes;
	
	public Generated(String content, Set<NodeInfo> unsupportedNodes)
	{
		this.content = content;
		this.unsupportedNodes = unsupportedNodes;
	}

	public String getContent()
	{
		return content;
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
