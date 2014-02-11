package org.overture.codegen.ooast;

import org.overture.ast.node.INode;

public class NodeInfo
{
	private INode node;
	private String reason;
	
	public NodeInfo(INode node)
	{
		super();
		this.node = node;
		this.reason = null;
	}
	
	public NodeInfo(INode node, String reason)
	{
		super();
		this.node = node;
		this.reason = reason;
	}
	
	public INode getNode()
	{
		return node;
	}
	
	public String getReason()
	{
		return reason;
	}
}
