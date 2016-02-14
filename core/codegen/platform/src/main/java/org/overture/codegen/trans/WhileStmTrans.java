package org.overture.codegen.trans;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ABreakStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AWhileStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class WhileStmTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transAssistant;
	private String whileCondExpPrefix;

	public WhileStmTrans(TransAssistantIR transAssistant, String whileCondExpPrefix)
	{
		this.transAssistant = transAssistant;
		this.whileCondExpPrefix = whileCondExpPrefix;
	}

	@Override
	public void caseAWhileStmIR(AWhileStmIR node) throws AnalysisException
	{
		// while(boolExp) { body; }
		//
		// boolExp is replaced with a variable expression 'whileCond' that is
		// computed as set for each iteration in the while loop:
		//
		// boolean whileCond = true;
		//
		// while(whileCond)
		// {
		//   whileCond = boolExp;
		//   if (!whileCond) { break; }
		//   body;
		// }
		//
		// This is needed for cases where the while condition is a complex
		// expression that needs to be transformed. For example, when the
		// while condition is a quantified expression
		
		SExpIR exp = node.getExp().clone();
		SStmIR body = node.getBody().clone();
		
		String whileCondName = transAssistant.getInfo().getTempVarNameGen().nextVarName(whileCondExpPrefix);
		
		SExpIR whileCondVar = transAssistant.consBoolCheck(whileCondName, false);
		
		AIfStmIR whileCondCheck = new AIfStmIR();
		whileCondCheck.setIfExp(transAssistant.consBoolCheck(whileCondName, true));
		whileCondCheck.setThenStm(new ABreakStmIR());
		
		ABlockStmIR newWhileBody = new ABlockStmIR();
		newWhileBody.getStatements().add(transAssistant.consBoolVarAssignment(exp, whileCondName));
		newWhileBody.getStatements().add(whileCondCheck);
		newWhileBody.getStatements().add(body);
		
		AWhileStmIR newWhileStm = new AWhileStmIR();
		newWhileStm.setExp(whileCondVar);
		newWhileStm.setBody(newWhileBody);
		
		ABlockStmIR declBlock = new ABlockStmIR();
		AVarDeclIR whileCondVarDecl = transAssistant.consBoolVarDecl(whileCondName, true);
		declBlock.getLocalDefs().add(whileCondVarDecl);
		declBlock.getStatements().add(newWhileStm);
		
		transAssistant.replaceNodeWith(node, declBlock);

		newWhileStm.getBody().apply(this);
	}
}
