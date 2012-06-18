package org.overture.interpreter.eval;

import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.Value;

public class BinaryExpressionEvaluator extends UnaryExpressionEvaluator
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3877784873512750134L;

	
	@Override
	public Value caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);

			return NumericValue.valueOf(lv + rv, ctxt);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(),e);
		}
	}
}
