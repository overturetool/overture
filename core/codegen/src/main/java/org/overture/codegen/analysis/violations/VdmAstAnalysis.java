package org.overture.codegen.analysis.violations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;

public class VdmAstAnalysis
{
	public static Set<Violation> usesIllegalNames(List<? extends INode> nodes, NamingComparison comparison) throws AnalysisException
	{
		NameViolationAnalysis namingAnalysis = new NameViolationAnalysis(comparison);
		ViolationAnalysisApplication application = new ViolationAnalysisApplication(namingAnalysis);
		
		return findViolations(nodes, application);
	}
	
	public static Set<Violation> usesUnsupportedModelingConstructs(List<? extends INode> nodes) throws AnalysisException
	{
		ModelingViolationAnalysis modelingAnalysis = new ModelingViolationAnalysis();
		ViolationAnalysisApplication application = new ViolationAnalysisApplication(modelingAnalysis);
		
		return findViolations(nodes, application);
	}
	
	private static Set<Violation> findViolations(List<? extends INode> nodes, ViolationAnalysisApplication application) throws AnalysisException
	{
		Set<Violation> allViolations = new HashSet<Violation>();
		
		for (INode currentNode : nodes)
		{
			List<Violation> currentViolations = application.execute(currentNode);
			
			if(!currentViolations.isEmpty())
				allViolations.addAll(currentViolations);
		}
		
		return allViolations;
	}
	
	private static class ViolationAnalysisApplication
	{
		private ViolationAnalysis violationAnalysis;
		
		public ViolationAnalysisApplication(ViolationAnalysis violationAnalysis)
		{
			this.violationAnalysis = violationAnalysis;
		}
		
		public List<Violation> execute(INode node) throws AnalysisException
		{
			return applyViolationVisitor(node, violationAnalysis);
		}
		
		private static List<Violation> applyViolationVisitor(INode node, ViolationAnalysis analysis) throws AnalysisException
		{
			try
			{
				node.apply(analysis);
			} catch (AnalysisException e)
			{
				throw e;
			}
			
			return analysis.getViolations();
		}
	}
}
