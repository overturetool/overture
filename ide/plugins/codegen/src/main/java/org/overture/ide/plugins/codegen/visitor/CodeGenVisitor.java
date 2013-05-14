package org.overture.ide.plugins.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ide.plugins.codegen.naming.TemplateParameters;
import org.overture.ide.plugins.codegen.nodes.ClassCG;
import org.overture.ide.plugins.codegen.nodes.ValueDefinitionCG;

public class CodeGenVisitor extends
		QuestionAnswerAdaptor<CodeGenContextMap, String>
{
	private static final long serialVersionUID = -7105226072509250353L;

	private CodeGenAssistant assistant = CodeGenAssistant.GetInstance();

	@Override
	public String caseAClassClassDefinition(AClassClassDefinition node,
			CodeGenContextMap question) throws AnalysisException
	{
		String className = node.getName().getName();
		String accessSpecifier = node.getAccess().getAccess().toString();

		ClassCG classCg = new ClassCG(className, accessSpecifier);
		
		question.registerCodeGenClass(classCg);
		
		LinkedList<PDefinition> definitions = node.getDefinitions();

		for (PDefinition def : definitions)
			def.apply(this, question);
		
		return null;
	}

	@Override
	public String caseAValueDefinition(AValueDefinition node,
			CodeGenContextMap question) throws AnalysisException
	{
		String accessSpecifier = node.getAccess().getAccess().toString();
		String type = assistant.formatType(node.getType());
		String pattern = node.getPattern().toString();
		String exp = assistant.formatExpression(node.getExpression());

		String className = node.getClassDefinition().getName().getName();
		question.getCodeGenClass(className).addValueDefinition(new ValueDefinitionCG(accessSpecifier, type, pattern, exp));

		return null;
	}

	@Override
	public String caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, CodeGenContextMap question)
			throws AnalysisException
	{
		String accessSpecifier = node.getAccess().getAccess().toString();
		String operationName = node.getName().getName();
		String returnType = assistant.formatType(node.getActualResult());

		String className = node.getClassDefinition().getName().getName();
		CodeGenContext context = question.getContext(className);

		context.put(TemplateParameters.METHOD_ACCESS_SPECIFIER, accessSpecifier);
		context.put(TemplateParameters.METHOD_RETURN_TYPE, returnType);
		context.put(TemplateParameters.METHOD_NAME, operationName);

		return null;
	}

}
