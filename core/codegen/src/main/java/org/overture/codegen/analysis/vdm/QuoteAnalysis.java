package org.overture.codegen.analysis.vdm;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;

public class QuoteAnalysis extends AbstractAnalysis
{
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof AQuoteLiteralExpCG)
		{
			setFound();
			throw new AnalysisException();
		}
	}
	
}