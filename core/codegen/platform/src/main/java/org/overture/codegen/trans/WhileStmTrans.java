package org.overture.codegen.trans;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.ir.statements.ABreakStmCG;
import org.overture.codegen.ir.statements.AIfStmCG;
import org.overture.codegen.ir.statements.AWhileStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class WhileStmTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;
	private String whileCondExpPrefix;

	public WhileStmTrans(TransAssistantCG transAssistant, String whileCondExpPrefix)
	{
		this.transAssistant = transAssistant;
		this.whileCondExpPrefix = whileCondExpPrefix;
	}

	@Override
	public void caseAWhileStmCG(AWhileStmCG node) throws AnalysisException
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
		
		SExpCG exp = node.getExp().clone();
		SStmCG body = node.getBody().clone();
		
		String whileCondName = transAssistant.getInfo().getTempVarNameGen().nextVarName(whileCondExpPrefix);
		
		SExpCG whileCondVar = transAssistant.consBoolCheck(whileCondName, false);
		
		AIfStmCG whileCondCheck = new AIfStmCG();
		whileCondCheck.setIfExp(transAssistant.consBoolCheck(whileCondName, true));
		whileCondCheck.setThenStm(new ABreakStmCG());
		
		ABlockStmCG newWhileBody = new ABlockStmCG();
		newWhileBody.getStatements().add(transAssistant.consBoolVarAssignment(exp, whileCondName));
		newWhileBody.getStatements().add(whileCondCheck);
		newWhileBody.getStatements().add(body);
		
		AWhileStmCG newWhileStm = new AWhileStmCG();
		newWhileStm.setExp(whileCondVar);
		newWhileStm.setBody(newWhileBody);
		
		ABlockStmCG declBlock = new ABlockStmCG();
		AVarDeclCG whileCondVarDecl = transAssistant.consBoolVarDecl(whileCondName, true);
		declBlock.getLocalDefs().add(whileCondVarDecl);
		declBlock.getStatements().add(newWhileStm);
		
		transAssistant.replaceNodeWith(node, declBlock);

		newWhileStm.getBody().apply(this);
	}
}
