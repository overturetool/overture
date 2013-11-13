package org.overture.codegen.analysis;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;

public class SequenceAnalysis extends DepthFirstAnalysisAdaptor
{
	private boolean found = false;
	
	public boolean isFound()
	{
		return found;
	}
	
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof ASeqSeqTypeCG)
		{
			found = true;
			throw new AnalysisException();
		}
	}
	
}
