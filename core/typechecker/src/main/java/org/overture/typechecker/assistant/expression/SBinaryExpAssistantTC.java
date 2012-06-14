package org.overture.typechecker.assistant.expression;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class SBinaryExpAssistantTC {

	
	public static ABooleanBasicType binaryCheck(SBooleanBinaryExp node,
			ABooleanBasicType expected,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws Throwable {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isType(node.getLeft().getType(),expected.getClass()))
		{
			TypeCheckerErrors.report(3065, "Left hand of " + node.getOp() + " is not " + expected,node.getLocation(),node);
		}

		if (!PTypeAssistantTC.isType(node.getRight().getType(),expected.getClass()))
		{
			TypeCheckerErrors.report(3066, "Right hand of " + node.getOp() + " is not " + expected,node.getLocation(),node);
		}

		node.setType(expected);
		return (ABooleanBasicType) node.getType();
		
	} 

	public static LexNameList getOldNames(SBinaryExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getLeft());
		list.addAll(PExpAssistantTC.getOldNames(expression.getRight()));
		return list;
	}

}
