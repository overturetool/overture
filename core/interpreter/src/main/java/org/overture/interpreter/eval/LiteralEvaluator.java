package org.overture.interpreter.eval;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.node.INode;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CharacterValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.QuoteValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.Value;

public class LiteralEvaluator extends QuestionAnswerAdaptor<Context, Value>
{
	
	@Override
	public Value caseAIntLiteralExp(AIntLiteralExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			return NumericValue.valueOf(node.getValue().getValue(), ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseACharLiteralExp(ACharLiteralExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		return new CharacterValue(node.getValue().getValue());
	}

	@Override
	public Value caseABooleanConstExp(ABooleanConstExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		return new BooleanValue(node.getValue().getValue());
	}

	@Override
	public Value caseAQuoteLiteralExp(AQuoteLiteralExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		return new QuoteValue(node.getValue().getValue());
	}

	@Override
	public Value caseARealLiteralExp(ARealLiteralExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			return NumericValue.valueOf(node.getValue().getValue(), ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAStringLiteralExp(AStringLiteralExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		return new SeqValue(node.getValue().getValue());
	}

	@Override
	public Value createNewReturnValue(INode node, Context question)
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public Value createNewReturnValue(Object node, Context question)
	{
		assert false : "Should not happen";
		return null;
	}
}
