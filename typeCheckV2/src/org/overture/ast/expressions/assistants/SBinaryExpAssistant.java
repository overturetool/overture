package org.overture.ast.expressions.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;

public class SBinaryExpAssistant {

	
	public static ABooleanBasicType binaryCheck(SBooleanBinaryExp node,
			ABooleanBasicType expected,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
	
		node.setLtype(node.getLeft().apply(rootVisitor, question));
		node.setRtype(node.getRight().apply(rootVisitor, question));

		if (!PTypeAssistant.isType(node.getLtype(),expected.getClass()))
		{
			PExpAssistant.report(3065, "Left hand of " + node.getOp() + " is not " + expected,node);
		}

		if (!PTypeAssistant.isType(node.getRtype(),expected.getClass()))
		{
			PExpAssistant.report(3066, "Right hand of " + node.getOp() + " is not " + expected,node);
		}

		node.setType(expected);
		return (ABooleanBasicType) node.getType();
		
	}

}
