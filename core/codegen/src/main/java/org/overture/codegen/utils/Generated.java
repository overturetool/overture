package org.overture.codegen.utils;

import java.util.Set;

import org.overture.ast.node.INode;

public class Generated
{
	protected String content;
	protected Set<INode> unsupportedNodes;
	
	public Generated(String content, Set<INode> unsupportedNodes)
	{
		this.content = content;
		this.unsupportedNodes = unsupportedNodes;
	}

	public String getContent()
	{
		return content;
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
