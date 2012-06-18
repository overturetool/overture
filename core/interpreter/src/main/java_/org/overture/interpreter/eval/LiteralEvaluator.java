package org.overture.interpreter.eval;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.Value;

public class LiteralEvaluator extends QuestionAnswerAdaptor<Context, Value>
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -9092856001136845731L;

	@Override
	public Value caseAIntLiteralExp(AIntLiteralExp node, Context ctxt)
			throws Throwable
	{
	BreakpointManager.getBreakpoint(	node).check(node.getLocation(), ctxt);

		try
		{
			return NumericValue.valueOf(node.getValue().value, ctxt);
		}
        catch (ValueException e)
        {
        	return RuntimeError.abort(node.getLocation(),e);
        }
	}
}
