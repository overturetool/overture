package org.overture.codegen.analysis.violations;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;

public class ViolationAnalysis extends DepthFirstAnalysisAdaptor
{
	private List<Violation> violations;
	
	public ViolationAnalysis()
	{
		this.violations = new LinkedList<Violation>();
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
