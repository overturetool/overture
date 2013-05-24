package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.statements.PStmCG;


public class StmVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PStmCG>
{
	
	private static final long serialVersionUID = -602593891699169007L;

	public StmVisitorCG()
	{
	}
	
	@Override
	public PStmCG caseAElseIfStm(AElseIfStm node, CodeGenInfo question)
			throws AnalysisException
	{
		//Dont visit it but create it directly if needed in the ifStm in order to avoid casting
		return null;
	}
	
	@Override
	public PStmCG caseAIfStm(AIfStm node, CodeGenInfo question)
			throws AnalysisException
	{
		PExpCG ifExp = node.getIfExp().apply(question.getExpVisitor(), question);
		PStmCG thenStm = node.getThenStm().apply(question.getStatementVisitor(), question);
		
		
		AIfStmCG ifStm = new AIfStmCG();
		
		ifStm.setIfExp(ifExp);
		ifStm.setThenStm(thenStm);
		
		LinkedList<AElseIfStm> elseIfs = node.getElseIf();	
		
		for (AElseIfStm stm : elseIfs)
		{
			ifExp = stm.getElseIf().apply(question.getExpVisitor(), question);
			thenStm = stm.getThenStm().apply(question.getStatementVisitor(), question);
			
			AElseIfStmCG elseIfStm = new AElseIfStmCG();
			elseIfStm.setElseIf(ifExp);
			elseIfStm.setThenStm(thenStm);
			
			
			ifStm.getElseIf().add(elseIfStm);
		}
		
		if(node.getElseStm() != null)
		{
			PStmCG elseStm = node.getElseStm().apply(question.getStatementVisitor(), question);
			ifStm.setElseStm(elseStm);
		}
		
		return ifStm;
		
	}
	
	@Override
	public PStmCG caseASkipStm(ASkipStm node, CodeGenInfo question)
			throws AnalysisException
	{
		return new ASkipStmCG();
	}
	
}
