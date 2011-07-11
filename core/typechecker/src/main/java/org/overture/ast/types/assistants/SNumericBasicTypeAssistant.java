package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.ast.expressions.assistants.PExpAssistant;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;


public class SNumericBasicTypeAssistant {

	public static void checkNumeric(
			SNumericBinaryExp node, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistant.isNumeric(node.getLeft().getType()))
		{
			TypeCheckerErrors.report(3139, "Left hand of " + node.getOp() + " is not numeric",node.getLocation(),node);
			TypeCheckerErrors.detail("Actual", node.getLeft().getType());
			node.getLeft().setType(new ARealNumericBasicType(node.getLocation(),false,null));
		}

		if (!PTypeAssistant.isNumeric(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3140, "Right hand of " + node.getOp() + " is not numeric",node.getLocation(),node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
			node.getRight().setType(new ARealNumericBasicType(node.getLocation(),false,null));
		}
		
	}

}
