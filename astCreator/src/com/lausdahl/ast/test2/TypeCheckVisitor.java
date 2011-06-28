package com.lausdahl.ast.test2;

import generated.node.ABoolType;
import generated.node.AIntType;
import generated.node.Node;
import generated.node.PType;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ABinopExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.ALazyAndBinop;
import org.overture.ast.expressions.ALazyOrBinop;
import org.overture.ast.expressions.AMinusBinop;
import org.overture.ast.expressions.APlusBinop;



public class TypeCheckVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType>
{
	@Override
	public PType caseABinopExp(ABinopExp node, TypeCheckInfo question)
	{
		PType expected = null;

		if (node.getBinop() instanceof APlusBinop
				|| node.getBinop() instanceof AMinusBinop)
		{
			expected = new AIntType();
		} else if (node.getBinop() instanceof ALazyAndBinop
				|| node.getBinop() instanceof ALazyOrBinop)
		{
			expected = new ABoolType();
		}

		Node ltype = node.getLeft().apply(this,question);
		Node rtype = node.getRight().apply(this,question);

		if (!expected.getClass().isInstance(ltype))
		{
			report(3065, "Left hand of " + node.getBinop() + " is not "
					+ expected);
		}

		if (!expected.getClass().isInstance(rtype))
		{
			report(3066, "Right hand of " + node.getBinop() + " is not "
					+ expected);
		}
		node.setType((PType) expected);
		return expected;
	}

	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question)
	{
		node.setType(new ABoolType());
		return node.getType();
	}

	@Override
	public PType caseAIntConstExp(AIntConstExp node, TypeCheckInfo question)
	{
		try
		{
			Integer.parseInt(node.getNumbersLiteral().getText());
			node.setType(new AIntType());
			return node.getType();
		} catch (Exception e)
		{

		}
		return null;
	}

	private void report(Integer i, String string)
	{
		System.err.println(i + ": " + string);
		System.err.flush();
	}
}
