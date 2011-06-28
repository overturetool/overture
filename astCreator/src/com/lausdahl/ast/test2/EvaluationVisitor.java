package com.lausdahl.ast.test2;

import generated.node.ATrueBoolean;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ABinopExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.ALazyAndBinop;
import org.overture.ast.expressions.AMinusBinop;
import org.overture.ast.expressions.APlusBinop;

import com.lausdahl.ast.values.BooleanValue;
import com.lausdahl.ast.values.IValue;
import com.lausdahl.ast.values.IntegerValue;
import com.lausdahl.ast.values.RealValue;
import com.lausdahl.runtime.Context;

public class EvaluationVisitor extends QuestionAnswerAdaptor<Context, IValue>
{
	@Override
	public IValue caseABinopExp(ABinopExp node, Context ctxt)
	{
		if (node.getBinop() instanceof APlusBinop)
		{
			double lv = node.getLeft().apply(this, ctxt).realValue(ctxt);
			double rv = node.getRight().apply(this, ctxt).realValue(ctxt);

			return new RealValue(lv + rv);
		} else if (node.getBinop() instanceof AMinusBinop)
		{
			double lv = node.getLeft().apply(this, ctxt).realValue(ctxt);
			double rv = node.getRight().apply(this, ctxt).realValue(ctxt);

			return new RealValue(lv + rv);
		} else if (node.getBinop() instanceof ALazyAndBinop)
		{
			IValue lv = node.getLeft().apply(this, ctxt);

			boolean lb = lv.boolValue(ctxt);

			if (!lb)
			{
				return lv; // Stop after LHS
			}

			IValue rv = node.getRight().apply(this, ctxt);

			if (lb)
			{
				return rv;
			}

			return new BooleanValue(false);
		} else if (node.getBinop() instanceof ALazyAndBinop)
		{
			IValue lv = node.getLeft().apply(this, ctxt);

			boolean lb = lv.boolValue(ctxt);

			if (lb)
			{
				return lv; // Stop after LHS
			}

			IValue rv = node.getRight().apply(this, ctxt);

			if (lb)
			{
				return new BooleanValue(true);
			} else
			{
				return rv;
			}
		}
		return null;
	}

	@Override
	public IValue caseABooleanConstExp(ABooleanConstExp node,
			Context question)
	{
		return new BooleanValue(node.getBoolean() instanceof ATrueBoolean);
	}

	@Override
	public IValue caseAIntConstExp(AIntConstExp node, Context question)
	{
		return new IntegerValue(Integer.valueOf(node.getNumbersLiteral().getText()));
	}
}
