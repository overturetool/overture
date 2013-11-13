package org.overture.codegen.analysis;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;

public class QuoteAnalysis extends DepthFirstAnalysisAdaptor
{
	private boolean found = false;
	
	public boolean isFound()
	{
		return found;
	}
	
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof AQuoteLiteralExpCG)
		{
			found = true;
			throw new AnalysisException();
		}
	}
	
}