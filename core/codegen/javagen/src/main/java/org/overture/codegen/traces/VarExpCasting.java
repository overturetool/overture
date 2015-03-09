package org.overture.codegen.traces;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class VarExpCasting extends DepthFirstAnalysisAdaptor
{
	private String className;
	private TransAssistantCG transAssistant;
	
	public VarExpCasting(TransAssistantCG transAssistant, String className)
	{
		this.className = className;
		this.transAssistant = transAssistant;
	}
	
	@Override
	public void defaultInSVarExpCG(SVarExpCG node) throws AnalysisException
	{
		ACastUnaryExpCG castVar = new ACastUnaryExpCG();
		transAssistant.replaceNodeWith(node, castVar);

		castVar.setType(transAssistant.consClassType(className));
		castVar.setExp(node);
	}
}
