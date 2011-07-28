package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexLocation;


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

	public static PType typeOf(long value, LexLocation location) {
		if (value > 0)
		{
			return new ANatOneNumericBasicType(location,false);
		}
		else if (value >= 0)
		{
			return new ANatNumericBasicType(location,false);
		}
		else
		{
			return new AIntNumericBasicType(location,false);
		}
	}

	public static int getWeight(SNumericBasicType subn) {
		switch(subn.kindSNumericBasicType())
		{
			case INT:
				return 2;
			case NAT:
				return 1;
			case NATONE:
				return 0;
			case RATIONAL:
				return 3;
			case REAL:
				return 4;
		}			
		return -1;
	}

}
