package org.overture.codegen.tests.util;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;

public class FirstVarFinder extends DepthFirstAnalysisAdaptor
{
	private AIdentifierVarExpCG var = null;
	
	@Override
	public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
			throws AnalysisException
	{
		var = node;
	}
	
	public AIdentifierVarExpCG getVar()
	{
		return var;
	}
}