package org.overture.typechecker.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class SBinaryExpAssistantTC
{

	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SBinaryExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public ABooleanBasicType binaryCheck(SBooleanBinaryExp node,
			ABooleanBasicType expected,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isType(node.getLeft().getType(), expected.getClass()))
		{
			TypeCheckerErrors.report(3065, "Left hand of " + node.getOp()
					+ " is not " + expected, node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isType(node.getRight().getType(), expected.getClass()))
		{
			TypeCheckerErrors.report(3066, "Right hand of " + node.getOp()
					+ " is not " + expected, node.getLocation(), node);
		}

		node.setType(expected);
		return (ABooleanBasicType) node.getType();

	}

}
