package org.overture.codegen.analysis;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.types.ASetSetTypeCG;

public class SetAnalysis extends DepthFirstAnalysisAdaptor
{

	private static final long serialVersionUID = -8268052875734570861L;
	
	private boolean found = false;
	
	public boolean isFound()
	{
		return found;
	}
	
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof ASetSetTypeCG)
		{
			found = true;
			throw new AnalysisException();
		}
	}
	
}
