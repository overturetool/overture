package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.nodes.ClassCG;
import org.overture.codegen.nodes.MethodDeinitionCG;
import org.overture.codegen.nodes.ValueDefinitionCG;

public class DefVisitorCG extends QuestionAnswerAdaptor<CodeGenContextMap, String>
{
	private static final long serialVersionUID = 81602965450922571L;
	
	private CodeGenVisitor rootVisitor;
	
	public DefVisitorCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
	}
	
	@Override
	public String caseAClassClassDefinition(AClassClassDefinition node,
			CodeGenContextMap question) throws AnalysisException
	{
		String className = node.getName().apply(rootVisitor, question);
		String accessSpecifier = node.getAccess().apply(rootVisitor, question);

		ClassCG classCg = new ClassCG(className, accessSpecifier);
		
		question.registerCodeGenClass(classCg);
		
		LinkedList<PDefinition> definitions = node.getDefinitions();

		for (PDefinition def : definitions)
			def.apply(rootVisitor.getDefVisitor(), question);
		
		return null;
	}
	
	@Override
	public String caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, CodeGenContextMap question)
			throws AnalysisException
	{	
		String accessSpecifier = node.getAccess().apply(rootVisitor, question);
		String operationName = node.getName().apply(rootVisitor, question);
		String returnType = node.getActualResult().apply(rootVisitor.getTypeVisitor(), question);

		String className = node.getClassDefinition().getName().apply(rootVisitor, question);
		ClassCG codeGenClass = question.getCodeGenClass(className);
		codeGenClass.addMethod(new MethodDeinitionCG(accessSpecifier, returnType, operationName));
		
		node.getBody().apply(rootVisitor.getStmVisitor(), question);
		
		return null;
	}
		
	@Override
	public String caseAValueDefinition(AValueDefinition node,
			CodeGenContextMap question) throws AnalysisException
	{
		String accessSpecifier = node.getAccess().apply(rootVisitor, question);
		String type = node.getType().apply(rootVisitor.getTypeVisitor(), question);
		
		String pattern = node.getPattern().apply(rootVisitor.getPatternVisitor(), question);
		String exp = node.getExpression().apply(rootVisitor.getExpVisitor(), question);
		//CodeGenAssistant.formatExpression(node.getExpression());

		String className = node.getClassDefinition().getName().apply(rootVisitor, question);
		ClassCG codeGenClass = question.getCodeGenClass(className); 
		codeGenClass.addValueDefinition(new ValueDefinitionCG(accessSpecifier, type, pattern, exp));

		return null;
	}

}
