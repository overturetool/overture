package org.overture.ide.plugins.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;

public class CodeGenVisitor extends QuestionAnswerAdaptor<CodeGenContext, String>
{
	private static final long serialVersionUID = -7105226072509250353L;

	@Override
	public String caseAClassClassDefinition(AClassClassDefinition node,
			CodeGenContext question) throws AnalysisException
	{
		
		question.addClass(node);
		question.putInContext(node, "ClassName", node.getName().getName());
		
		return null;
	}
	
}
