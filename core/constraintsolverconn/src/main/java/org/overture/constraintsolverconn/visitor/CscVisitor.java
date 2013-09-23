package org.overture.constraintsolverconn.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;

public class CscVisitor extends QuestionAnswerAdaptor<String, String>{

	private static final long serialVersionUID = 8993841651356537402L;

	@Override
	public String caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			String question) throws AnalysisException {

		String left = node.getLeft().apply(this, "Some information #1");
		String right = node.getRight().apply(this, "Some information #2");
		
		String answer = "(" + left + " plus " + right + ")";
		
		return answer;
	}
	
	@Override
	public String caseAIntLiteralExp(AIntLiteralExp node, String question)
			throws AnalysisException {
		
		System.out.println("Question is: " + question);	
		String answer = node.getValue().toString(); 
		
		return answer;
	}
	
	
}
