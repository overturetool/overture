package org.overture.codegen.analysis;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.types.ASetSetTypeCG;

public class SetAnalysis extends AbstractAnalysis
{
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof ASetSetTypeCG)
		{
			setFound();
			throw new AnalysisException();
		}
	}
	
}
