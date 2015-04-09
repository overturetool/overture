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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.codegen.cgast.INode;

public final class IRStatus<T extends INode>
{
	protected String irNodeName;
	protected T node;
	protected Set<VdmNodeInfo> unsupportedInIr;
	protected Set<IrNodeInfo> transformationWarnings;

	public IRStatus(Set<VdmNodeInfo> unsupportedInIr)
	{
		this.unsupportedInIr = unsupportedInIr;
		this.transformationWarnings = new HashSet<IrNodeInfo>();
	}

	public IRStatus(String nodeName, T node, Set<VdmNodeInfo> unsupportedNodes)
	{
		this(unsupportedNodes);
		this.irNodeName = nodeName;
		this.node = node;
	}
	
	public IRStatus(String nodeName, T node, Set<VdmNodeInfo> unsupportedNodes, Set<IrNodeInfo> transformationWarnings)
	{
		this(nodeName, node, unsupportedNodes);
		this.transformationWarnings = transformationWarnings;
	}

	public Set<VdmNodeInfo> getUnsupportedInIr()
	{
		return unsupportedInIr;
	}

	public void addTransformationWarnings(Set<IrNodeInfo> transformationWarnings)
	{
		this.transformationWarnings.addAll(transformationWarnings);
	}

	public boolean canBeGenerated()
	{
		return unsupportedInIr.size() == 0;
	}

	public Set<IrNodeInfo> getTransformationWarnings()
	{
		return transformationWarnings;
	}
	
	public void setTransformationWarnings(Set<IrNodeInfo> transformationWarnings)
	{
		this.transformationWarnings = transformationWarnings;
	}

	public T getIrNode()
	{
		return node;
	}

	public void setIrNode(T newNode)
	{
		this.node = newNode;
	}

	public String getIrNodeName()
	{
		return irNodeName;
	}

	public void setIrNodeName(String irNodeName)
	{
		this.irNodeName = irNodeName;
	}
	
	@Override
	public String toString()
	{
		return getIrNodeName();
	}
	
	public static <T extends INode> IRStatus<T> extract(
			IRStatus<INode> inputStatus, Class<T> type)
	{
		String name = inputStatus.getIrNodeName();
		INode node = inputStatus.getIrNode();
		Set<VdmNodeInfo> unsupportedInIr = inputStatus.getUnsupportedInIr();
		Set<IrNodeInfo> warnings = inputStatus.getTransformationWarnings();

		if (node != null && type != null && type.isInstance(node))
		{
			return new IRStatus<T>(name, type.cast(node), unsupportedInIr, warnings);
		} else
		{
			return null;
		}
	}

	public static <T extends INode> List<IRStatus<T>> extract(
			List<IRStatus<INode>> inputStatuses, Class<T> type)
	{
		List<IRStatus<T>> outputStatuses = new LinkedList<IRStatus<T>>();

		for (IRStatus<INode> status : inputStatuses)
		{
			IRStatus<T> converted = extract(status, type);

			if (converted != null)
			{
				outputStatuses.add(converted);
			}
		}

		return outputStatuses;
	}
	
	public static <T extends INode> IRStatus<INode> extract(IRStatus<T> inputStatus)
	{
		String name = inputStatus.getIrNodeName();
		INode node = inputStatus.getIrNode();
		Set<VdmNodeInfo> unsupportedInIr = inputStatus.getUnsupportedInIr();
		Set<IrNodeInfo> warnings = inputStatus.getTransformationWarnings();

		return new IRStatus<INode>(name, node, unsupportedInIr, warnings);
	}
	
	public static <T extends INode> List<IRStatus<INode>> extract(List<IRStatus<T>> inputStatuses)
	{
		List<IRStatus<INode>> outputStatuses = new LinkedList<IRStatus<INode>>();
		
		for(IRStatus<T> status : inputStatuses)
		{
			IRStatus<INode> converted = extract(status);

			if (converted != null)
			{
				outputStatuses.add(converted);
			}
		}
		
		return outputStatuses;
	}
}
