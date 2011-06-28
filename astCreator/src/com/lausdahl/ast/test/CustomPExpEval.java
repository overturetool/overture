//package com.lausdahl.ast.test;
//
//
//import generated.node.ABinopExp;
//import generated.node.ABooleanConstExp;
//import generated.node.AIntConstExp;
//import generated.node.ALazyAndBinop;
//import generated.node.AMinusBinop;
//import generated.node.APlusBinop;
//import generated.node.ATrueBoolean;
//import generated.node.PExpEval;
//
//import com.lausdahl.ast.values.BooleanValue;
//import com.lausdahl.ast.values.IValue;
//import com.lausdahl.ast.values.IntegerValue;
//import com.lausdahl.ast.values.RealValue;
//import com.lausdahl.runtime.Context;
//
//public class CustomPExpEval extends PExpEval
//{
//	@Override
//	public IValue caseABinopExp(ABinopExp source, Context ctxt)
//	{
//		if (source.getBinop() instanceof APlusBinop)
//		{
//			double lv = source.getLeft().eval(this.parent(), ctxt).realValue(ctxt);
//			double rv = source.getRight().eval(this.parent(), ctxt).realValue(ctxt);
//
//			return new RealValue(lv + rv);
//		} else if (source.getBinop() instanceof AMinusBinop)
//		{
//			double lv = source.getLeft().eval(this.parent(), ctxt).realValue(ctxt);
//			double rv = source.getRight().eval(this.parent(), ctxt).realValue(ctxt);
//
//			return new RealValue(lv + rv);
//		} else if (source.getBinop() instanceof ALazyAndBinop)
//		{
//			IValue lv = source.getLeft().eval(this.parent(), ctxt);
//
//			boolean lb = lv.boolValue(ctxt);
//
//			if (!lb)
//			{
//				return lv; // Stop after LHS
//			}
//
//			IValue rv = source.getRight().eval(this.parent(), ctxt);
//
//			if (lb)
//			{
//				return rv;
//			}
//
//			return new BooleanValue(false);
//		} else if (source.getBinop() instanceof ALazyAndBinop)
//		{
//			IValue lv = source.getLeft().eval(this.parent(), ctxt);
//
//			boolean lb = lv.boolValue(ctxt);
//
//			if (lb)
//			{
//				return lv; // Stop after LHS
//			}
//
//			IValue rv = source.getRight().eval(this.parent(), ctxt);
//
//			if (lb)
//			{
//				return new BooleanValue(true);
//			} else
//			{
//				return rv;
//			}
//		}
//		return null;
//
//	}
//
//	@Override
//	public IValue caseABooleanConstExp(ABooleanConstExp source, Context ctxt)
//	{
//		return new BooleanValue(source.getBoolean() instanceof ATrueBoolean);
//	}
//
//	@Override
//	public IValue caseAIntConstExp(AIntConstExp source, Context ctxt)
//	{
//		return new IntegerValue(Integer.valueOf(source.getNumbersLiteral().getText()));
//	}
//}
