package org.overture.typecheck.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.ast.expressions.assistants.PExpAssistant;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;


public class SNumericBasicTypeAssistant {

	public static void checkNumeric(
			SNumericBinaryExp node, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistant.isNumeric(node.getLeft().getType()))
		{
			PExpAssistant.report(3139, "Left hand of " + node.getOp() + " is not numeric",node);
			PExpAssistant.detail("Actual", node.getLeft().getType());
			node.getLeft().setType(new ARealNumericBasicType(node.getLocation(),false));
		}

		if (!PTypeAssistant.isNumeric(node.getRight().getType()))
		{
			PExpAssistant.report(3140, "Right hand of " + node.getOp() + " is not numeric",node);
			PExpAssistant.detail("Actual", node.getRight().getType());
			node.getRight().setType(new ARealNumericBasicType(node.getLocation(),false));
		}
		
	}

}
