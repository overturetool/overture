package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.PPattern;

public class PatternVisitorCG extends QuestionAnswerAdaptor<CodeGenContextMap, String>
{
	private CodeGenVisitor rootVisitor;

	public PatternVisitorCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
	}
	
	@Override
	public String defaultPPattern(PPattern node, CodeGenContextMap question)
			throws AnalysisException
	{
		return node.toString();
	}
	
}
