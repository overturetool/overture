package org.overture.interpreter.eval;

import java.util.Collections;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ADistConcatUnaryExp;
import org.overture.ast.expressions.ADistIntersectUnaryExp;
import org.overture.ast.expressions.ADistMergeUnaryExp;
import org.overture.ast.expressions.ADistUnionUnaryExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AFloorUnaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.AReverseUnaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NaturalOneValue;
import org.overture.interpreter.values.NaturalValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueMap;
import org.overture.interpreter.values.ValueSet;

public class UnaryExpressionEvaluator extends LiteralEvaluator
{

	@Override
	public Value caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			return NumericValue.valueOf(Math.abs(node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt)), ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			return new NaturalValue(node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt).size());
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		} catch (ContextException e)
		{
			throw e; // To avoid case below
		} catch (Exception e)
		{
			return VdmRuntimeError.abort(node.getLocation(), 4065, e.getMessage(), ctxt);
		}
	}

	@Override
	public Value caseADistConcatUnaryExp(ADistConcatUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueList seqseq = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).seqValue(ctxt);
			ValueList result = new ValueList();

			for (Value v : seqseq)
			{
				result.addAll(v.seqValue(ctxt));
			}

			return new SeqValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueSet setset = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);

			if (setset.isEmpty())
			{
				return VdmRuntimeError.abort(node.getLocation(), 4151, "Cannot take dinter of empty set", ctxt);
			}

			ValueSet result = null;

			for (Value v : setset)
			{
				if (result == null)
				{
					result = new ValueSet(v.setValue(ctxt));
				} else
				{
					result.retainAll(v.setValue(ctxt));
				}
			}

			return new SetValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseADistMergeUnaryExp(ADistMergeUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueSet setmap = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			ValueMap result = new ValueMap();

			for (Value v : setmap)
			{
				ValueMap m = v.mapValue(ctxt);

				for (Value k : m.keySet())
				{
					Value rng = m.get(k);
					Value old = result.put(k, rng);

					if (old != null && !old.equals(rng))
					{
						VdmRuntimeError.abort(node.getLocation(), 4021, "Duplicate map keys have different values: "
								+ k, ctxt);
					}
				}
			}

			return new MapValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseADistUnionUnaryExp(ADistUnionUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueSet setset = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			ValueSet result = new ValueSet();

			for (Value v : setset)
			{
				result.addAll(v.setValue(ctxt));
			}

			return new SetValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAElementsUnaryExp(AElementsUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueList seq = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).seqValue(ctxt);
			ValueSet set = new ValueSet();
			set.addAll(seq);
			return new SetValue(set);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAFloorUnaryExp(AFloorUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			return NumericValue.valueOf(Math.floor(node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt)), ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAHeadUnaryExp(AHeadUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueList seq = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).seqValue(ctxt);

			if (seq.isEmpty())
			{
				return VdmRuntimeError.abort(node.getLocation(), 4010, "Cannot take head of empty sequence", ctxt);
			}

			return seq.get(0);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAIndicesUnaryExp(AIndicesUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueList seq = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).seqValue(ctxt);
			ValueSet result = new ValueSet();

			for (int i = 1; i <= seq.size(); i++)
			{
				result.addNoCheck(new NaturalOneValue(i));
			}

			return new SetValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		} catch (Exception e)
		{
			return VdmRuntimeError.abort(node.getLocation(), 4065, e.getMessage(), ctxt);
		}
	}

	@Override
	public Value caseALenUnaryExp(ALenUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			return new NaturalValue(node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).seqValue(ctxt).size());
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		} catch (ContextException e)
		{
			throw e; // To avoid case below
		} catch (Exception e)
		{
			return VdmRuntimeError.abort(node.getLocation(), 4065, e.getMessage(), ctxt);
		}
	}

	@Override
	public Value caseAMapDomainUnaryExp(AMapDomainUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueMap map = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
			ValueSet result = new ValueSet();
			result.addAll(map.keySet());
			return new SetValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAMapInverseUnaryExp(AMapInverseUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueMap map = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);

			if (!map.isInjective())
			{
				VdmRuntimeError.abort(node.getLocation(), 4012, "Cannot invert non-injective map", ctxt);
			}

			ValueMap result = new ValueMap();

			for (Value k : map.keySet())
			{
				result.put(map.get(k), k);
			}

			return new MapValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAMapRangeUnaryExp(AMapRangeUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueMap map = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).mapValue(ctxt);
			ValueSet result = new ValueSet();
			result.addAll(map.values());
			return new SetValue(result);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseANotUnaryExp(ANotUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			Value v = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
			return v.isUndefined() ? v : new BooleanValue(!v.boolValue(ctxt));
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAPowerSetUnaryExp(APowerSetUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			ValueSet values = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			List<ValueSet> psets = values.powerSet();
			ValueSet rs = new ValueSet(psets.size());

			for (ValueSet v : psets)
			{
				rs.addNoCheck(new SetValue(v));
			}

			return new SetValue(rs);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAReverseUnaryExp(AReverseUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueList seq;

		try
		{
			seq = new ValueList(node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).seqValue(ctxt));
			Collections.reverse(seq);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}

		return new SeqValue(seq);
	}

	@Override
	public Value caseATailUnaryExp(ATailUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueList seq;

		try
		{
			seq = new ValueList(node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).seqValue(ctxt));
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}

		if (seq.isEmpty())
		{
			VdmRuntimeError.abort(node.getLocation(), 4033, "Tail sequence is empty", ctxt);
		}

		seq.remove(0);
		return new SeqValue(seq);
	}

	@Override
	public Value caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			double v = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).realValue(ctxt);
			return NumericValue.valueOf(-v, ctxt);
		} catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(), e);
		}
	}

	@Override
	public Value caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		return node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
	}

}
