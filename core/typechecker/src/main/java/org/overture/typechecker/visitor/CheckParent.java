package org.overture.typechecker.visitor;

import java.util.*;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.EDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.node.NodeEnum;

public class CheckParent extends DepthFirstAnalysisAdaptor
{
	private List<INode> list;
	
	public CheckParent()
	{
		list = new Vector<INode>();
	}
	
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		list.add(node);
	}
	
	public List<INode> findInconsistentNodes()
	{
		List<INode> problems = new Vector<INode>();
		
		
		for (INode node : list)
		{
			INode current = node;
		
			while(current.parent() != null)
			{
				
				if(current.kindNode()==NodeEnum.MODULES)
					break;
				
				if(current.kindNode()==NodeEnum.DEFINITION && ((PDefinition)current).kindPDefinition() == EDefinition.CLASS)
					break;
				
				current = current.parent();
			}
			
			if(current.parent() == null)
				if(current.kindNode()!=NodeEnum.MODULES)
					if(current.kindNode()==NodeEnum.DEFINITION && ((PDefinition)current).kindPDefinition() != EDefinition.CLASS)
						problems.add(current);			
		}
		
		return problems;
	}
	
}
