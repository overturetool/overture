package org.overture.codegen.analysis;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;

public class AbstractAnalysis extends DepthFirstAnalysisAdaptor
{
	private boolean found = false;
	
	public void setFound()
	{
		this.found = true;
	}
	
	public boolean isFound()
	{
		return found;
	}
}
