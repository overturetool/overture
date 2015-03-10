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
import java.util.Set;

import org.overture.codegen.cgast.INode;

public class IRStatus<T extends INode>
{
	protected Set<VdmNodeInfo> unsupportedInIr;
	protected Set<IrNodeInfo> transformationWarnings;
	protected T node;
	protected String irNodeName;

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
}
