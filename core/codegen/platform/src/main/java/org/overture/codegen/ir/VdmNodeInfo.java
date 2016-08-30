/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.ir;

import org.overture.ast.node.INode;

public class VdmNodeInfo
{
	private INode node;
	private String reason;

	public VdmNodeInfo(INode node)
	{
		super();
		this.node = node;
		this.reason = null;
	}

	public VdmNodeInfo(INode node, String reason)
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

		if (!(obj instanceof VdmNodeInfo))
		{
			return false;
		}

		VdmNodeInfo other = (VdmNodeInfo) obj;

		INode otherNode = other.node;
		String otherReason = other.reason;

		return matches(this, otherNode, otherReason);
	}

	public static boolean matches(VdmNodeInfo vdmNodeInfo, INode otherNode,
			String otherReason)
	{
		if (vdmNodeInfo.node == null && otherNode != null
				|| vdmNodeInfo.node != null
						&& !vdmNodeInfo.node.equals(otherNode))
		{
			return false;
		}

		if (vdmNodeInfo.reason == null && otherReason != null
				|| vdmNodeInfo.reason != null
						&& !vdmNodeInfo.reason.equals(otherReason))
		{
			return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "Node: " + node + ". Reason: " + reason;
	}
}
