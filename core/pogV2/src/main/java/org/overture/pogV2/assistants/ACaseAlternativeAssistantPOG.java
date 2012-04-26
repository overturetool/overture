package org.overture.pogV2.assistants;


import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.pogV2.obligations.POCaseContext;
import org.overture.pogV2.obligations.POContextStack;
import org.overture.pogV2.obligations.PONotCaseContext;
import org.overture.pogV2.obligations.ProofObligationList;

public class ACaseAlternativeAssistantPOG {

	public static ProofObligationList getProofObligations(ACaseAlternative node,
			QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor,
			POContextStack question, PType type) {

		PPattern pattern = node.getPattern();
		PExp cexp = node.getCexp();
		
		ProofObligationList obligations = new ProofObligationList();
		question.push(new POCaseContext(pattern, type,  cexp));
		obligations.addAll(node.getResult().apply(rootVisitor,question));
		question.pop();
		question.push(new PONotCaseContext(pattern, type, cexp));

		return obligations;
	}

}
