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

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.*;

import java.util.*;

public class LocationAssistantIR extends AssistantBase
{
	public LocationAssistantIR(AssistantManager assistantManager)
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

	public INode getVdmNode(IrNodeInfo info)
	{
		if (info == null)
		{
			return null;
		}

		if (info.getNode() == null || !(info.getNode() instanceof PIR))
		{
			return null;
		}

		SourceNode sourceNode = ((PIR) info.getNode()).getSourceNode();

		if (sourceNode == null)
		{
			return null;
		}

		return sourceNode.getVdmNode();
	}

	public ILexLocation findLocation(IrNodeInfo info)
	{
		INode vdmNode = getVdmNode(info);

		if (vdmNode == null)
		{
			return null;
		}

		return findLocation(vdmNode);
	}

	public List<IrNodeInfo> getIrNodeInfoLocationSorted(Set<IrNodeInfo> nodes)
	{
		List<IrNodeInfo> list = new LinkedList<IrNodeInfo>(nodes);

		Collections.sort(list, new Comparator<IrNodeInfo>()
		{

			@Override
			public int compare(IrNodeInfo first, IrNodeInfo second)
			{
				INode vdmNode = getVdmNode(first);
				ILexLocation firstLoc = vdmNode == null ? null
						: findLocation(vdmNode);

				if (firstLoc == null)
				{
					return -1;
				}

				vdmNode = getVdmNode(second);
				ILexLocation secondLoc = vdmNode == null ? null
						: findLocation(vdmNode);

				if (secondLoc == null)
				{
					return 1;
				}

				return compareLocations(firstLoc, secondLoc);
			}
		});

		return list;
	}

	public List<VdmNodeInfo> getVdmNodeInfoLocationSorted(
			Set<VdmNodeInfo> nodes)
	{
		List<VdmNodeInfo> list = new LinkedList<VdmNodeInfo>(nodes);

		Collections.sort(list, new Comparator<VdmNodeInfo>()
		{
			@Override
			public int compare(VdmNodeInfo first, VdmNodeInfo second)
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


	public String consVdmNodeInfoStr(STypeIR t) {

		StringBuilder sb = new StringBuilder();
		INode vdmNode = AssistantBase.getVdmNode(t);

		if(vdmNode != null)
		{
			sb.append("VDM node: " + vdmNode + ".");

			ILexLocation loc = findLocation(vdmNode);

			if(loc != null)
			{
				sb.append(" VDM node location: " + loc + ".");
			}
		}

		return sb.toString();
	}
}
