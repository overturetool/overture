package org.overture.codegen.analysis.violations;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.assistant.AssistantManager;

public class ViolationAnalysis extends DepthFirstAnalysisAdaptor
{
	private List<Violation> violations;
	protected AssistantManager assistantManager;
	
	public ViolationAnalysis(AssistantManager assistantManager)
	{
		this.violations = new LinkedList<Violation>();
		this.assistantManager = assistantManager;
	}

	public List<Violation> getViolations()
	{
		return violations;
	}
	
	public void addViolation(Violation violation)
	{
		if(violations.contains(violation))
			return;
		
		violations.add(violation);
	}
}
