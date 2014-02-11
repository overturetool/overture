package org.overture.codegen.assistant;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.codegen.ooast.NodeInfo;

public class LocationAssistantCG
{
	public static ILexLocation findLocation(INode node)
	{
		Map<String, Object> children = node.getChildren(true);
		
		Set<String> allKeys = children.keySet();
		
		for (String key : allKeys)
		{
			Object child = children.get(key);
			
			if(child instanceof ILexLocation)
				return (ILexLocation) child;
		}
		
		return null;
	}
	
	public static int compareLocations(ILexLocation firstLoc, ILexLocation secondLoc)
	{
		String firstModule = firstLoc.getModule();
		String secondModule = secondLoc.getModule();
		
		if(!firstModule.equals(secondModule))
				return firstModule.compareTo(secondModule);
		
		int firstLine = firstLoc.getStartLine();
		int secondLine = secondLoc.getStartLine();
		
		if(firstLine == secondLine)
		{
			int firstPos = firstLoc.getStartPos();
			int secondPos = secondLoc.getStartPos();
			
			return firstPos - secondPos;
		}
		else
		{
			return firstLine - secondLine;
		}
	}
	
	public static List<NodeInfo> getNodesLocationSorted(Set<NodeInfo> nodes)
	{
		List<NodeInfo> list = new LinkedList<NodeInfo>(nodes);
		
		Collections.sort(list,new Comparator<NodeInfo>()
		{
			@Override
			public int compare(NodeInfo first, NodeInfo second)
			{
				ILexLocation firstLoc = findLocation(first.getNode());
				
				if(firstLoc == null)
					return -1;
				
				ILexLocation secondLoc = findLocation(second.getNode());
				
				if(secondLoc == null)
					return 1;
				

				return compareLocations(firstLoc, secondLoc);
			}
		});
		
		return list;
	}
}
