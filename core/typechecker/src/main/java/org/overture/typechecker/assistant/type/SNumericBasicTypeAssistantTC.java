package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.type.SNumericBasicTypeAssistant;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;


public class SNumericBasicTypeAssistantTC extends SNumericBasicTypeAssistant {

	public static void checkNumeric(
			SNumericBinaryExp node, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isNumeric(node.getLeft().getType()))
		{
			TypeCheckerErrors.report(3139, "Left hand of " + node.getOp() + " is not numeric",node.getLocation(),node);
			TypeCheckerErrors.detail("Actual", node.getLeft().getType());
			node.getLeft().setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		}

		if (!PTypeAssistantTC.isNumeric(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3140, "Right hand of " + node.getOp() + " is not numeric",node.getLocation(),node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
			node.getRight().setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		}
		
	}

	public static PType typeOf(long value, LexLocation location) {
		if (value > 0)
		{
			return AstFactory.newANatOneNumericBasicType(location);
		}
		else if (value >= 0)
		{
			return AstFactory.newANatNumericBasicType(location);
		}
		else
		{
			return AstFactory.newAIntNumericBasicType(location);
		}
	}

	

}
