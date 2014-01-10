package org.overture.codegen.utils;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;

public class ViolationAnalysis extends DepthFirstAnalysisAdaptor
{
	private List<Violation> nameViolations;
	
	public ViolationAnalysis()
	{
		this.nameViolations = new LinkedList<Violation>();
	}
	
	public List<Violation> getNameViolations()
	{
		return nameViolations;
	}
	
	public void addViolation(Violation violation)
	{
		nameViolations.add(violation);
	}
}
