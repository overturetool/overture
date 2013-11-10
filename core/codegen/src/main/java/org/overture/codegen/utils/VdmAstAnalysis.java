package org.overture.codegen.utils;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;

public class VdmAstAnalysis
{

	public static List<NameViolation> usesIllegalNames(List<? extends INode> nodes, NamingComparison comparison) throws AnalysisException
	{
		List<NameViolation> allIllegals = new LinkedList<NameViolation>();
		
		for (INode currentNode : nodes)
		{
			List<NameViolation> currentIllegals = usesIllegalName(currentNode, comparison);
			
			if(!currentIllegals.isEmpty())
				allIllegals.addAll(currentIllegals);
		}
		
		return allIllegals;
	}
	
	public static List<NameViolation> usesIllegalName(INode node, NamingComparison comparison) throws AnalysisException
	{
		NameAnalysis nameAnalysis = new NameAnalysis(comparison);
		try
		{
			node.apply(nameAnalysis);
		} catch (AnalysisException e)
		{
			throw e;
		}

		return nameAnalysis.getNameViolations();
	}
}
