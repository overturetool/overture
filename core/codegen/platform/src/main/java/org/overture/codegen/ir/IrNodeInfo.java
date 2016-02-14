package org.overture.codegen.ir;

import org.overture.codegen.ir.INode;

public class IrNodeInfo
{
	private INode node;
	private String reason;

	public IrNodeInfo(INode node)
	{
		super();
		this.node = node;
		this.reason = null;
	}

	public IrNodeInfo(INode node, String reason)
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

	@Override
	public int hashCode()
	{
		int hash = 0;

		if (node != null)
		{
			hash += node.hashCode();
		}

		if (reason != null)
		{
			hash += reason.hashCode();
		}

		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof IrNodeInfo))
		{
			return false;
		}

		IrNodeInfo other = (IrNodeInfo) obj;

		if (this.node == null && other.node != null || this.node != null
				&& !this.node.equals(other.node))
		{
			return false;
		}

		if (this.reason == null && other.reason != null || this.reason != null
				&& !this.reason.equals(other.reason))
		{
			return false;
		}

		return true;
	}
	
	@Override
	public String toString()
	{
		return node.getClass().getSimpleName() + ". Reason: " + reason;
	}
}
