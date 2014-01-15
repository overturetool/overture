package org.overture.constraintsolverconn.visitor;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;

public class DemoVisitor extends AnalysisAdaptor {

	@Override
	public void caseAPlusNumericBinaryExp(APlusNumericBinaryExp node)
			throws AnalysisException {
	
		node.getLeft().apply(this);
		node.getRight().apply(this);
		
		System.out.println("Got in plus numeric binary expression!");
	}
	
	@Override
	public void caseAIntLiteralExp(AIntLiteralExp node)
			throws AnalysisException {
		
		System.out.println("Found int literal: " + node.getValue().toString());
	}
	
}
