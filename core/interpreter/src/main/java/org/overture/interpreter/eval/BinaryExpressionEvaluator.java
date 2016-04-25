package org.overture.interpreter.eval;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CompFunctionValue;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.IterFunctionValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.UndefinedValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueMap;
import org.overture.interpreter.values.ValueSet;

public class BinaryExpressionEvaluator extends UnaryExpressionEvaluator
{

	/*
	 * Boolean
	 */

	@Override
	public Value caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			Context ctxt) throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, Context ctxt)
			throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			Context ctxt) throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node, Context ctxt)
			throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	/*
	 * end boolean
	 */

	@Override
	public Value caseACompBinaryExp(ACompBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).deref();
		Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).deref();

		if (lv instanceof MapValue)
		{
			ValueMap lm = null;
			ValueMap rm = null;

			try
			{
				lm = lv.mapValue(ctxt);
				rm = rv.mapValue(ctxt);
			} catch (ValueException e)
			{
				return VdmRuntimeError.abort(node.getLocation(), e);
			}

			ValueMap result = new ValueMap();

			for (Value v : rm.keySet())
			{
				Value rng = lm.get(rm.get(v));

				if (rng == null)
				{
					VdmRuntimeError.abort(node.getLocation(), 4162, "The RHS range is not a subset of the LHS domain", ctxt);
				}

				Value old = result.put(v, rng);

				if (old != null && !old.equals(rng))
				{
					VdmRuntimeError.abort(node.getLocation(), 4005, "Duplicate map keys have different values", ctxt);
				}
			}

			return new MapValue(result);
		}

		try
		{
			FunctionValue f1 = lv.functionValue(ctxt);
			FunctionValue f2 = rv.functionValue(ctxt);

			return new CompFunctionValue(f1, f2);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			ValueSet set = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			ValueMap map = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
			ValueMap modified = new ValueMap(map);

			for (Value k : map.keySet())
			{
				if (set.contains(k))
				{
					modified.remove(k);
				}
			}

			return new MapValue(modified);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			ValueSet set = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			ValueMap map = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
			ValueMap modified = new ValueMap(map);

			for (Value k : map.keySet())
			{
				if (!set.contains(k))
				{
					modified.remove(k);
				}
			}

			return new MapValue(modified);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAEqualsBinaryExp(AEqualsBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		if (lv.isUndefined())
		{
			return lv;
		}

		Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		if (rv.isUndefined())
		{
			return rv;
		}

		return new BooleanValue(lv.equals(rv));
	}

	@Override
	public Value caseAInSetBinaryExp(AInSetBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value elem = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		Value set = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		try
		{
			return new BooleanValue(set.setValue(ctxt).contains(elem));
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAMapUnionBinaryExp(AMapUnionBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		ValueMap lm = null;
		ValueMap rm = null;

		try
		{
			lm = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
			rm = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}

		ValueMap result = new ValueMap();
		result.putAll(lm);

		for (Value k : rm.keySet())
		{
			Value rng = rm.get(k);
			Value old = result.put(k, rng);

			if (old != null && !old.equals(rng))
			{
				VdmRuntimeError.abort(node.getLocation(), 4021, "Duplicate map keys have different values: "
						+ k, ctxt);
			}
		}

		return new MapValue(result);
	}

	@Override
	public Value caseANotEqualBinaryExp(ANotEqualBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		return new BooleanValue(!lv.equals(rv));
	}

	@Override
	public Value caseANotInSetBinaryExp(ANotInSetBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		Value elem = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		Value set = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

		try
		{
			return new BooleanValue(!set.setValue(ctxt).contains(elem));
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	/*
	 * Numeric
	 */

	@Override
	public Value caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);

			if (rv == 0)
			{
				throw new ValueException(4134, "Infinite or NaN trouble", ctxt);
			}

			return NumericValue.valueOf(div(lv, rv), ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			Context ctxt) throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, Context ctxt)
			throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			Context ctxt) throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, Context ctxt)
			throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			Context ctxt) throws AnalysisException
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
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAModNumericBinaryExp(AModNumericBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			/*
			 * Remainder x rem y and modulus x mod y are the same if the signs of x and y are the same, otherwise they
			 * differ and rem takes the sign of x and mod takes the sign of y. The formulas for remainder and modulus
			 * are: x rem y = x - y * (x div y) x mod y = x - y * floor(x/y) Hence, -14 rem 3 equals -2 and -14 mod 3
			 * equals 1. One can view these results by walking the real axis, starting at -14 and making jumps of 3. The
			 * remainder will be the last negative number one visits, because the first argument corresponding to x is
			 * negative, while the modulus will be the first positive number one visit, because the second argument
			 * corresponding to y is positive.
			 */

			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);

			if (rv == 0)
			{
				throw new ValueException(4134, "Infinite or NaN trouble", ctxt);
			}

			return NumericValue.valueOf(lv - rv * (long) Math.floor(lv / rv), ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
    		Value l = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
    		Value r = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (NumericValue.areIntegers(l, r))
			{
				long lv = l.intValue(ctxt);
				long rv = r.intValue(ctxt);
				long sum = addExact(lv, rv, ctxt);
				return NumericValue.valueOf(sum, ctxt);
			}
			else
			{
				double lv = l.realValue(ctxt);
				double rv = r.realValue(ctxt);
	    		return NumericValue.valueOf(lv + rv, ctxt);
			}

		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			/*
			 * Remainder x rem y and modulus x mod y are the same if the signs of x and y are the same, otherwise they
			 * differ and rem takes the sign of x and mod takes the sign of y. The formulas for remainder and modulus
			 * are: x rem y = x - y * (x div y) x mod y = x - y * floor(x/y) Hence, -14 rem 3 equals -2 and -14 mod 3
			 * equals 1. One can view these results by walking the real axis, starting at -14 and making jumps of 3. The
			 * remainder will be the last negative number one visits, because the first argument corresponding to x is
			 * negative, while the modulus will be the first positive number one visit, because the second argument
			 * corresponding to y is positive.
			 */

			double lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
			double rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);

			if (rv == 0)
			{
				throw new ValueException(4134, "Infinite or NaN trouble", ctxt);
			}

			return NumericValue.valueOf(lv - rv * div(lv, rv), ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
    		Value l = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
    		Value r = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (NumericValue.areIntegers(l, r))
			{
				long lv = l.intValue(ctxt);
				long rv = r.intValue(ctxt);
				long diff = subtractExact(lv, rv, ctxt);
				return NumericValue.valueOf(diff, ctxt);
			}
			else
			{
				double lv = l.realValue(ctxt);
				double rv = r.realValue(ctxt);
	    		return NumericValue.valueOf(lv - rv, ctxt);
			}
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
    		Value l = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
    		Value r = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (NumericValue.areIntegers(l, r))
			{
				long lv = l.intValue(ctxt);
				long rv = r.intValue(ctxt);
				long mult = multiplyExact(lv, rv, ctxt);
				return NumericValue.valueOf(mult, ctxt);
			}
			else
			{
				double lv = l.realValue(ctxt);
				double rv = r.realValue(ctxt);
	    		return NumericValue.valueOf(lv * rv, ctxt);
			}
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	/*
	 * end numeric
	 */

	@Override
	public Value caseAPlusPlusBinaryExp(APlusPlusBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).deref();
			Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (lv instanceof MapValue)
			{
				ValueMap lm = new ValueMap(lv.mapValue(ctxt));
				ValueMap rm = rv.mapValue(ctxt);

				for (Value k : rm.keySet())
				{
					lm.put(k, rm.get(k));
				}

				return new MapValue(lm);
			} else
			{
				ValueList seq = lv.seqValue(ctxt);
				ValueMap map = rv.mapValue(ctxt);
				ValueList result = new ValueList(seq);

				for (Value k : map.keySet())
				{
					int iv = (int) k.intValue(ctxt);

					if (iv < 1 || iv > seq.size())
					{
						VdmRuntimeError.abort(node.getLocation(), 4025, "Map key not within sequence index range: "
								+ k, ctxt);
					}

					result.set(iv - 1, map.get(k));
				}

				return new SeqValue(result);
			}
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			ValueSet set1 = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			ValueSet set2 = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);

			return new BooleanValue(set1.size() < set2.size()
					&& set2.containsAll(set1));
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		ValueSet set = null;
		ValueMap map = null;

		try
		{
			set = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			map = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}

		ValueMap modified = new ValueMap(map);

		for (Value k : map.keySet())
		{
			if (set.contains(map.get(k)))
			{
				modified.remove(k);
			}
		}

		return new MapValue(modified);
	}

	@Override
	public Value caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		ValueSet set = null;
		ValueMap map = null;

		try
		{
			set = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			map = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}

		ValueMap modified = new ValueMap(map);

		for (Value k : map.keySet())
		{
			if (!set.contains(map.get(k)))
			{
				modified.remove(k);
			}
		}

		return new MapValue(modified);
	}

	@Override
	public Value caseASeqConcatBinaryExp(ASeqConcatBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
			Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			ValueList result = new ValueList();
			result.addAll(lv.seqValue(ctxt));
			result.addAll(rv.seqValue(ctxt));

			return new SeqValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		ValueSet result = new ValueSet();
		ValueSet togo = null;

		try
		{
			togo = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			result.addAll(node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt));
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}

		for (Value r : togo)
		{
			result.remove(r);
		}

		return new SetValue(result);
	}

	@Override
	public Value caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			Context ctxt) throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			ValueSet result = new ValueSet();
			result.addAll(node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt));
			result.retainAll(node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt));
			return new SetValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseASetUnionBinaryExp(ASetUnionBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			ValueSet result = new ValueSet();
			result.addAll(node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt));
			result.addAll(node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt));
			return new SetValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAStarStarBinaryExp(AStarStarBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			Value lv = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).deref();
			Value rv = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt);

			if (lv instanceof MapValue)
			{
				ValueMap map = lv.mapValue(ctxt);
				long n = rv.intValue(ctxt);
				ValueMap result = new ValueMap();

				for (Value k : map.keySet())
				{
					Value r = k;

					for (int i = 0; i < n; i++)
					{
						r = map.get(r);
					}

					if (r == null)
					{
						VdmRuntimeError.abort(node.getLocation(), 4133, "Map range is not a subset of its domain: "
								+ k, ctxt);
					}

					Value old = result.put(k, r);

					if (old != null && !old.equals(r))
					{
						VdmRuntimeError.abort(node.getLocation(), 4030, "Duplicate map keys have different values: "
								+ k, ctxt);
					}
				}

				return new MapValue(result);
			} else if (lv instanceof FunctionValue)
			{
				return new IterFunctionValue(lv.functionValue(ctxt), rv.intValue(ctxt));
			} else if (lv instanceof NumericValue)
			{
				double ld = lv.realValue(ctxt);
				double rd = rv.realValue(ctxt);

				return NumericValue.valueOf(Math.pow(ld, rd), ctxt);
			}

			return VdmRuntimeError.abort(node.getLocation(), 4031, "First arg of '**' must be a map, function or number", ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseASubsetBinaryExp(ASubsetBinaryExp node, Context ctxt)
			throws AnalysisException
	{
		// breakpoint.check(location, ctxt);
		node.getLocation().hit(); // Mark as covered

		try
		{
			ValueSet set1 = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			ValueSet set2 = node.getRight().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);

			return new BooleanValue(set2.containsAll(set1));
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	/*
	 * Utility methods
	 */

	static public long div(double lv, double rv)
	{
		/*
		 * There is often confusion on how integer division, remainder and modulus work on negative numbers. In fact,
		 * there are two valid answers to -14 div 3: either (the intuitive) -4 as in the Toolbox, or -5 as in e.g.
		 * Standard ML [Paulson91]. It is therefore appropriate to explain these operations in some detail. Integer
		 * division is defined using floor and real number division: x/y < 0: x div y = -floor(abs(-x/y)) x/y >= 0: x
		 * div y = floor(abs(x/y)) Note that the order of floor and abs on the right-hand side makes a difference, the
		 * above example would yield -5 if we changed the order. This is because floor always yields a smaller (or
		 * equal) integer, e.g. floor (14/3) is 4 while floor (-14/3) is -5.
		 */

		if (lv / rv < 0)
		{
			return (long) -Math.floor(Math.abs(lv / rv));
		} else
		{
			return (long) Math.floor(Math.abs(-lv / rv));
		}
	}
	
	// These are included in Java 8 Math.java
	
	private long addExact(long x, long y, Context ctxt) throws ValueException
	{
		long r = x + y;
		// HD 2-12 Overflow iff both arguments have the opposite sign of the result
		
		if (((x ^ r) & (y ^ r)) < 0)
		{
			throw new ValueException(4169, "Arithmetic overflow", ctxt);
		}
		
		return r;
	}

	private long subtractExact(long x, long y, Context ctxt) throws ValueException
	{
		long r = x - y;
		// HD 2-12 Overflow iff the arguments have different signs and
		// the sign of the result is different than the sign of x

		if (((x ^ y) & (x ^ r)) < 0)
		{
			throw new ValueException(4169, "Arithmetic overflow", ctxt);
		}

		return r;
	}

    private long multiplyExact(long x, long y, Context ctxt) throws ValueException
    {
    	long r = x * y;
    	long ax = Math.abs(x);
    	long ay = Math.abs(y);

    	if (((ax | ay) >>> 31 != 0))
    	{
    		if (((y != 0) && (r / y != x)) || (x == Long.MIN_VALUE && y == -1))
    		{
    			throw new ValueException(4169, "Arithmetic overflow", ctxt);
    		}
    	}

    	return r;
    }
}
