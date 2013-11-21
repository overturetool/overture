package org.overture.codegen.analysis;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;

public class TupleAnalysis extends AbstractAnalysis
{
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof ATupleTypeCG || node instanceof ATupleExpCG)
		{
			setFound();
			throw new AnalysisException();
		}
	}
	
}
