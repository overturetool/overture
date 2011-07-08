package org.overture.ast.expressions.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;

public class SBinaryExpAssistant {

	
	public static ABooleanBasicType binaryCheck(SBooleanBinaryExp node,
			ABooleanBasicType expected,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
	
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistant.isType(node.getLeft().getType(),expected.getClass()))
		{
			TypeCheckerErrors.report(3065, "Left hand of " + node.getOp() + " is not " + expected,node.getLocation(),node);
		}

		if (!PTypeAssistant.isType(node.getRight().getType(),expected.getClass()))
		{
			TypeCheckerErrors.report(3066, "Right hand of " + node.getOp() + " is not " + expected,node.getLocation(),node);
		}

		node.setType(expected);
		return (ABooleanBasicType) node.getType();
		
	}

}
