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
package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.PIR;

public abstract class AssistantBase
{
	protected AssistantManager assistantManager;

	public AssistantBase(AssistantManager assistantManager)
	{
		super();
		this.assistantManager = assistantManager;
	}
	
	public static org.overture.ast.node.INode getVdmNode(PIR irNode)
	{
		if(irNode != null && irNode.getSourceNode() != null)
		{
			return irNode.getSourceNode().getVdmNode();
		}
		else
		{
			return null;
		}
	}
	
	public <T extends INode> List<T> cloneNodes(List<T> list, Class<T> nodeType)
	{
		List<T> cloneList = new LinkedList<T>();
		
		for(T e : list)
		{
			Object clone = e.clone();
			
			if(nodeType.isInstance(clone))
			{
				cloneList.add(nodeType.cast(clone));
			}
		}
		
		return cloneList;
	}
}
