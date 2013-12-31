package org.overture.codegen.assistant;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;

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
	
	public static List<INode> getNodeLocationsSorted(Set<INode> nodes)
	{
		List<INode> list = new LinkedList<INode>(nodes);
		
		Collections.sort(list,new Comparator<INode>()
		{
			@Override
			public int compare(INode first, INode second)
			{
				ILexLocation firstLoc = findLocation(first);
				
				if(firstLoc == null)
					return -1;
				
				ILexLocation secondLoc = findLocation(second);
				
				if(secondLoc == null)
					return 1;
				
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
		});
		
		return list;
	}
}
