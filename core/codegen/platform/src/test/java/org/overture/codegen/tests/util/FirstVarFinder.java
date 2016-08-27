package org.overture.codegen.tests.util;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;

public class FirstVarFinder extends DepthFirstAnalysisAdaptor
{
	private AIdentifierVarExpIR var = null;

	@Override
	public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node)
			throws AnalysisException
	{
		var = node;
	}

	public AIdentifierVarExpIR getVar()
	{
		return var;
	}
}