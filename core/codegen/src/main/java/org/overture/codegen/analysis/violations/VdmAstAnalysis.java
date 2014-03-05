package org.overture.codegen.analysis.violations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantManager;

public class VdmAstAnalysis
{
	private AssistantManager assistantManager;
	
	public VdmAstAnalysis(AssistantManager assistantManager)
	{
		this.assistantManager = assistantManager != null ? assistantManager : new AssistantManager();	
	}
	
	public Set<Violation> usesIllegalNames(List<? extends INode> nodes, NamingComparison comparison) throws AnalysisException
	{
		NameViolationAnalysis namingAnalysis = new NameViolationAnalysis(assistantManager, comparison);
		ViolationAnalysisApplication application = new ViolationAnalysisApplication(namingAnalysis);
		
		return findViolations(nodes, application);
	}
	
	public Set<Violation> usesUnsupportedModelingConstructs(List<? extends INode> nodes) throws AnalysisException
	{
		ModelingViolationAnalysis modelingAnalysis = new ModelingViolationAnalysis(assistantManager);
		ViolationAnalysisApplication application = new ViolationAnalysisApplication(modelingAnalysis);
		
		return findViolations(nodes, application);
	}
	
	private Set<Violation> findViolations(List<? extends INode> nodes, ViolationAnalysisApplication application) throws AnalysisException
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
	
	private class ViolationAnalysisApplication
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
		
		private List<Violation> applyViolationVisitor(INode node, ViolationAnalysis analysis) throws AnalysisException
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
