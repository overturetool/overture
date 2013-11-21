package org.overture.codegen.analysis;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;

public class SequenceAnalysis extends AbstractAnalysis
{
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof ASeqSeqTypeCG)
		{
			setFound();
			throw new AnalysisException();
		}
	}
	
}
