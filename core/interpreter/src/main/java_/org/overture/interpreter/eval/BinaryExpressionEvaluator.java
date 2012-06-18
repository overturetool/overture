package org.overture.interpreter.eval;

import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASubstractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.UndefinedValue;
import org.overture.interpreter.values.Value;

public class BinaryExpressionEvaluator extends UnaryExpressionEvaluator
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3877784873512750134L;

	/*
	 * Boolean
	 */

	@Override
	public Value caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (lv.isUndefined())
			{
				return lv;
			}

			boolean lb = lv.boolValue(ctxt);

			if (!lb)
			{
				return lv; // Stop after LHS
			}

			Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (lb)
			{
				return rv;
			}

			return new BooleanValue(false);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
			Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (lv.isUndefined() || rv.isUndefined())
			{
				return new UndefinedValue();
			}

			return new BooleanValue(lv.boolValue(ctxt) == rv.boolValue(ctxt));
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (lv.isUndefined())
			{
				return lv;
			}

			boolean lb = lv.boolValue(ctxt);

			if (lb)
			{
				return node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
			}

			return new BooleanValue(true);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node, Context ctxt)
			throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (lv.isUndefined())
			{
				return lv;
			} else
			{
				boolean lb = lv.boolValue(ctxt);

				if (lb)
				{
					return lv; // Stop after LHS
				}

				Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

				if (lb)
				{
					return new BooleanValue(true);
				} else
				{
					return rv;
				}
			}
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	/*
	 * Numeric
	 */

	@Override
	public Value caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);

			return NumericValue.valueOf(div(lv, rv), ctxt);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);

			return NumericValue.valueOf(lv / rv, ctxt);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		try
		{
			return new BooleanValue(lv.realValue(ctxt) >= rv.realValue(ctxt));
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		try
		{
			return new BooleanValue(lv.realValue(ctxt) > rv.realValue(ctxt));
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		try
		{
			return new BooleanValue(lv.realValue(ctxt) <= rv.realValue(ctxt));
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		try
		{
			return new BooleanValue(lv.realValue(ctxt) < rv.realValue(ctxt));
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAModNumericBinaryExp(AModNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			/*
			 * Remainder x rem y and modulus x mod y are the same if the signs of x
			 * and y are the same, otherwise they differ and rem takes the sign of x and
			 * mod takes the sign of y. The formulas for remainder and modulus are:
			 * x rem y = x - y * (x div y)
			 * x mod y = x - y * floor(x/y)
			 * Hence, -14 rem 3 equals -2 and -14 mod 3 equals 1. One can view these
			 * results by walking the real axis, starting at -14 and making jumps of 3.
			 * The remainder will be the last negative number one visits, because the first
			 * argument corresponding to x is negative, while the modulus will be the first
			 * positive number one visit, because the second argument corresponding to y
			 * is positive.
			 */

			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);

			return NumericValue.valueOf(lv - rv * (long) Math.floor(lv / rv), ctxt);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

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
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			/*
			 * Remainder x rem y and modulus x mod y are the same if the signs of x
			 * and y are the same, otherwise they differ and rem takes the sign of x and
			 * mod takes the sign of y. The formulas for remainder and modulus are:
			 * x rem y = x - y * (x div y)
			 * x mod y = x - y * floor(x/y)
			 * Hence, -14 rem 3 equals -2 and -14 mod 3 equals 1. One can view these
			 * results by walking the real axis, starting at -14 and making jumps of 3.
			 * The remainder will be the last negative number one visits, because the first
			 * argument corresponding to x is negative, while the modulus will be the first
			 * positive number one visit, because the second argument corresponding to y
			 * is positive.
			 */

			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);

			return NumericValue.valueOf(lv - rv * div(lv, rv), ctxt);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseASubstractNumericBinaryExp(
			ASubstractNumericBinaryExp node, Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);

			return NumericValue.valueOf(lv - rv, ctxt);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			Context ctxt) throws Throwable
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);

			return NumericValue.valueOf(lv * rv, ctxt);
		} catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(), e);
		}
	}

	/*
	 * Utility methods
	 */

	static public long div(double lv, double rv)
	{
		/*
		 * There is often confusion on how integer division, remainder and modulus
		 * work on negative numbers. In fact, there are two valid answers to -14 div
		 * 3: either (the intuitive) -4 as in the Toolbox, or -5 as in e.g. Standard
		 * ML [Paulson91]. It is therefore appropriate to explain these operations in
		 * some detail.
		 * Integer division is defined using floor and real number division:
		 * x/y < 0: x div y = -floor(abs(-x/y))
		 * x/y >= 0: x div y = floor(abs(x/y))
		 * Note that the order of floor and abs on the right-hand side makes a difference,
		 * the above example would yield -5 if we changed the order. This is
		 * because floor always yields a smaller (or equal) integer, e.g. floor (14/3) is
		 * 4 while floor (-14/3) is -5.
		 */

		if (lv / rv < 0)
		{
			return (long) -Math.floor(Math.abs(lv / rv));
		} else
		{
			return (long) Math.floor(Math.abs(-lv / rv));
		}
	}
}
