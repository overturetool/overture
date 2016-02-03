package org.overture.codegen.tests.util;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;

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