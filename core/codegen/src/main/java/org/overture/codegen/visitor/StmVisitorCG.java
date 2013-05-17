package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.PStm;
import org.overture.codegen.assistant.StmAssistantCG;
import org.overture.codegen.nodes.ClassCG;
import org.overture.codegen.nodes.DeclarationStmCG;
import org.overture.codegen.nodes.MethodDeinitionCG;
import org.overture.codegen.nodes.ReturnStatementCG;

public class StmVisitorCG extends QuestionAnswerAdaptor<CodeGenContextMap, String>
{
	private static final long serialVersionUID = 5210069834877599547L;
	
	private CodeGenVisitor rootVisitor;
	private StmAssistantCG stmAssistant;
	
	public StmVisitorCG(CodeGenVisitor rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.stmAssistant = new StmAssistantCG(rootVisitor);
	}
	
	@Override
	public String caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
			CodeGenContextMap question) throws AnalysisException
	{
		MethodDeinitionCG methodDef = stmAssistant.getMethodDefinition(node, question);
		
		LinkedList<PDefinition> assignmentDefs = node.getAssignmentDefs();
		
		for (PDefinition def : assignmentDefs)
		{	
			AAssignmentDefinition assignment = (AAssignmentDefinition) def;
			String type = assignment.getType().apply(rootVisitor.getTypeVisitor(), question);
			String name = assignment.getName().apply(rootVisitor, question);
			String exp = assignment.getExpression().apply(rootVisitor.getExpVisitor(), question);
			
			methodDef.addStatement(new DeclarationStmCG(type, name, exp));
		}
	
		LinkedList<PStm> statements = node.getStatements();
		
		for (PStm stm : statements)
		{
			stm.apply(this, question);
		}
		
		return null;
	}		
}
