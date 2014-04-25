package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.type.SNumericBasicTypeAssistant;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SNumericBasicTypeAssistantTC extends SNumericBasicTypeAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public SNumericBasicTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static void checkNumeric(SNumericBinaryExp node,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(node.getLeft().getType()))
		{
			TypeCheckerErrors.report(3139, "Left hand of " + node.getOp()
					+ " is not numeric", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getLeft().getType());
			node.getLeft().setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		}

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3140, "Right hand of " + node.getOp()
					+ " is not numeric", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
			node.getRight().setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		}

	}

	public static PType typeOf(long value, ILexLocation location)
	{
		if (value > 0)
		{
			return AstFactory.newANatOneNumericBasicType(location);
		} else if (value >= 0)
		{
			return AstFactory.newANatNumericBasicType(location);
		} else
		{
			return AstFactory.newAIntNumericBasicType(location);
		}
	}

}
