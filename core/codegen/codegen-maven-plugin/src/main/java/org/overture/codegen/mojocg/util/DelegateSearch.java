package org.overture.codegen.mojocg.util;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.ANotImplementedStmCG;

public class DelegateSearch extends DepthFirstAnalysisAdaptor
{
	private boolean isDelegateCall = false;
	
	@Override
	public void caseANotImplementedStmCG(ANotImplementedStmCG node) throws AnalysisException
	{
		isDelegateCall = true;
	}
	
	public boolean isDelegateCall()
	{
		return isDelegateCall;
	}
}
