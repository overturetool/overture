package org.overture.pog.utility;


import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.pog.obligation.POCaseContext;
import org.overture.pog.obligation.PONotCaseContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligationList;
import org.overture.typechecker.assistant.expression.ACaseAlternativeAssistantTC;

public class ACaseAlternativeAssistantPOG extends ACaseAlternativeAssistantTC {

	protected IPogAssistantFactory af;
	
	public ACaseAlternativeAssistantPOG(IPogAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public IProofObligationList getProofObligations(ACaseAlternative node,
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> rootVisitor,
			IPOContextStack question, PType type) throws AnalysisException {

		PPattern pattern = node.getPattern();
		PExp cexp = node.getCexp();
		
		ProofObligationList obligations = new ProofObligationList();
		question.push(new POCaseContext(pattern, type,  cexp.clone()));
		obligations.addAll(node.getResult().apply(rootVisitor,question));
		question.pop();
		question.push(new PONotCaseContext(pattern, type, cexp.clone()));

		return obligations;
	}

}
