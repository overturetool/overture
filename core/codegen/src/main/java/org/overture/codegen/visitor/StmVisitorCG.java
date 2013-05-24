package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.AIfThenElseStmCG;
import org.overture.codegen.cgast.statements.AIfThenStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;


public class StmVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PStmCG>
{
	
	private static final long serialVersionUID = -602593891699169007L;

	public StmVisitorCG()
	{
	}
	
	@Override
	public PStmCG caseAIfStm(AIfStm node, CodeGenInfo question)
			throws AnalysisException
	{
		if(node.getElseIf().size() == 0 && node.getElseStm() == null)
		{
			//handle if then
			//return if then
		}
		
		PExpCG condition = node.getIfExp().apply(question.getExpVisitor(), question);
		PStmCG thenBody = node.getThenStm().apply(question.getStatementVisitor(), question);
		
		AIfThenElseStmCG stm = new AIfThenElseStmCG();
		
		stm.setCondition(condition);
		stm.setThenBody(thenBody);
		
		LinkedList<AElseIfStm> elseIfs = node.getElseIf();
		
		//Assume there are some:
		
		AIfThenElseStmCG tailStm = stm;
		
		for (AElseIfStm eStm : elseIfs)
		{
			AIfThenElseStmCG currentElseIf = new AIfThenElseStmCG();
			
			
			PExpCG elseIfCondition = eStm.getElseIf().apply(question.getExpVisitor(), question);
			PStmCG elseIfBody = eStm.getThenStm().apply(question.getStatementVisitor(), question);
			
			currentElseIf.setCondition(elseIfCondition);
			currentElseIf.setThenBody(elseIfBody);
			
			tailStm.setElseBody(currentElseIf);
			tailStm = currentElseIf;
		}
		
		if(node.getElseStm() != null)
		{
			PStmCG elseStm = node.getElseStm().apply(question.getStatementVisitor(), question);
			stm.setElseBody(elseStm);
		}
		
		return stm;
	}
	
	@Override
	public PStmCG caseASkipStm(ASkipStm node, CodeGenInfo question)
			throws AnalysisException
	{
		return new ASkipStmCG();
	}
	
//
//	@Override
//	public String caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		MethodDeinitionCG methodDef = stmAssistant.getMethodDefinition(node, question);
//
//		LinkedList<PDefinition> assignmentDefs = node.getAssignmentDefs();
//
//		for (PDefinition def : assignmentDefs)
//		{
//			AAssignmentDefinition assignment = (AAssignmentDefinition) def;
//			String type = assignment.getType().apply(rootVisitor.getTypeVisitor(), question);
//			String name = assignment.getName().apply(rootVisitor, question);
//			String exp = assignment.getExpression().apply(rootVisitor.getExpVisitor(), question);
//
//			methodDef.addStatement(new DeclarationStmCG(type, name, exp));
//		}
//
//		LinkedList<PStm> statements = node.getStatements();
//
//		for (PStm stm : statements)
//		{
//			stm.apply(this, question);
//		}
//
//		return null;
//	}
//
//	@Override
//	public String caseAIfStm(AIfStm node, CodeGenContextMap question)
//			throws AnalysisException
//	{
//
//		CodeGenContext context = new CodeGenContext();
//
//		context.put(TemplateParameters.IF_STM_TEST, "hejhej1");
//		context.put(TemplateParameters.IF_STM_THEN_STM, "hejhej2");
//		context.put(TemplateParameters.IF_STM_ELSE_STM, "hejhej3");
//
//		Template t = Vdm2CppUtil.getTemplate("if_statement.vm");
//
//		PrintWriter out = new PrintWriter(System.out);
//
//		System.out.println("***");
//		t.merge(context.getVelocityContext(), out);
//		out.flush();
//		out.println();
//		System.out.println("***");
//
//		return super.caseAIfStm(node, question);
//	}
}
