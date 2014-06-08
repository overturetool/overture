package org.overture.codegen.ir;

import org.overture.ast.node.INode;

public class SourceNode
{
	private INode vdmNode;

	public SourceNode(INode vdmNode)
	{
		super();
		this.vdmNode = vdmNode;
	}

	public INode getVdmNode()
	{
		return vdmNode;
	}
}
