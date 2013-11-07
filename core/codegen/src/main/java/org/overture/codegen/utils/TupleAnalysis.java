package org.overture.codegen.utils;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;

public class TupleAnalysis extends DepthFirstAnalysisAdaptor
{

	private static final long serialVersionUID = -1777600236754158171L;
	
	private boolean found = false;
	
	public boolean isFound()
	{
		return found;
	}
	
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof ATupleTypeCG || node instanceof ATupleExpCG)
		{
			found = true;
			throw new AnalysisException();
		}
	}
	
}
