package org.overture.codegen.trans;

import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.APreCondRuntimeErrorExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.ARaiseErrorStmIR;
import org.overture.codegen.ir.types.AErrorTypeIR;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class PreCheckTrans extends DepthFirstAnalysisAdaptor {

	private TransAssistantIR transAssistant;
	private Object conditionalCallTag;
	
	public PreCheckTrans(TransAssistantIR transAssistant, Object conditionalCallTag)
	{
		this.transAssistant = transAssistant;
		this.conditionalCallTag = conditionalCallTag;
	}
	
	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException {
		
		if(!transAssistant.getInfo().getSettings().generatePreCondChecks())
		{
			return;
		}
		
		SDeclIR preCond = node.getPreCond();
		
		if(preCond == null)
		{
			return;
		}
		
		if(!(preCond instanceof AMethodDeclIR))
		{
			Logger.getLog().printErrorln("Expected pre condition to be a method declaration at this point. Got: " + preCond);
			return;
		}

		AMethodDeclIR preCondMethod = (AMethodDeclIR) preCond;
		
		AApplyExpIR preCondCall = transAssistant.consConditionalCall(node, preCondMethod);
		
		if(preCondCall == null)
		{
			return;
		}
		
		preCondCall.setTag(conditionalCallTag);
		
		SStmIR body = node.getBody();
		
		APreCondRuntimeErrorExpIR runtimeError = new APreCondRuntimeErrorExpIR();
		runtimeError.setType(new AErrorTypeIR());
		runtimeError.setMessage(String.format("Precondition failure: pre_%s", node.getName()));
		
		ARaiseErrorStmIR raiseError= new ARaiseErrorStmIR();
		raiseError.setError(runtimeError);
		
		AIfStmIR ifCheck = new AIfStmIR();
		ifCheck.setIfExp(transAssistant.getInfo().getExpAssistant().negate(preCondCall));
		ifCheck.setThenStm(raiseError);
		
		ABlockStmIR newBody = new ABlockStmIR();
		newBody.getStatements().add(ifCheck);
		newBody.getStatements().add(body.clone());
		
		transAssistant.replaceNodeWith(body, newBody);
	}
}
