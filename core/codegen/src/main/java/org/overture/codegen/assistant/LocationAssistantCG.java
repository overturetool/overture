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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.NodeInfo;

public class LocationAssistantCG extends AssistantBase
{
	public LocationAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public ILexLocation findLocation(INode node)
	{
		Map<String, Object> children = node.getChildren(true);

		Set<String> allKeys = children.keySet();

		for (String key : allKeys)
		{
			Object child = children.get(key);

			if (child instanceof ILexLocation)
			{
				return (ILexLocation) child;
			}
		}

		return null;
	}

	public int compareLocations(ILexLocation firstLoc, ILexLocation secondLoc)
	{
		String firstModule = firstLoc.getModule();
		String secondModule = secondLoc.getModule();

		if (!firstModule.equals(secondModule))
		{
			return firstModule.compareTo(secondModule);
		}

		int firstLine = firstLoc.getStartLine();
		int secondLine = secondLoc.getStartLine();

		if (firstLine == secondLine)
		{
			int firstPos = firstLoc.getStartPos();
			int secondPos = secondLoc.getStartPos();

			return firstPos - secondPos;
		} else
		{
			return firstLine - secondLine;
		}
	}

	public List<NodeInfo> getNodesLocationSorted(Set<NodeInfo> nodes)
	{
		List<NodeInfo> list = new LinkedList<NodeInfo>(nodes);

		Collections.sort(list, new Comparator<NodeInfo>()
		{
			@Override
			public int compare(NodeInfo first, NodeInfo second)
			{
				ILexLocation firstLoc = findLocation(first.getNode());

				if (firstLoc == null)
				{
					return -1;
				}

				ILexLocation secondLoc = findLocation(second.getNode());

				if (secondLoc == null)
				{
					return 1;
				}

				return compareLocations(firstLoc, secondLoc);
			}
		});

		return list;
	}
}
