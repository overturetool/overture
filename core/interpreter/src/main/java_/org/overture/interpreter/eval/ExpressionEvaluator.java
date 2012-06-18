package org.overture.interpreter.eval;

import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AHistoryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.AStateInitExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.AThreadIdExp;
import org.overture.ast.expressions.ATimeExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;


public class ExpressionEvaluator extends BinaryExpressionEvaluator
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3877784873512750134L;

	
	@Override
	public Value caseAApplyExp(AApplyExp node, Context ctxt)
			throws Throwable
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		node.getLocation().hits--;	// This is counted below when root is evaluated

    	try
    	{
    		Value object = node.getRoot().apply(VdmRuntime.getExpressionEvaluator(),ctxt).deref();

			if (object instanceof FunctionValue)
    		{
        		ValueList argvals = new ValueList();

         		for (PExp arg: node.getArgs())
        		{
        			argvals.add(arg.apply(VdmRuntime.getExpressionEvaluator(),ctxt));
        		}

           		FunctionValue fv = object.functionValue(ctxt);
           		return fv.eval(node.getLocation(), argvals, ctxt);
    		}
			else if (object instanceof OperationValue)
    		{
        		ValueList argvals = new ValueList();

         		for (PExp arg: node.getArgs())
        		{
        			argvals.add(arg.apply(VdmRuntime.getExpressionEvaluator(),ctxt));
        		}

         		OperationValue ov = object.operationValue(ctxt);
           		return ov.eval(node.getLocation(), argvals, ctxt);
    		}
			else if (object instanceof SeqValue)
    		{
    			Value arg = node.getArgs().get(0).apply(VdmRuntime.getExpressionEvaluator(),ctxt);
    			SeqValue sv = (SeqValue)object;
    			return sv.get(arg, ctxt);
    		}
			else if (object instanceof MapValue)
    		{
    			Value arg = node.getArgs().get(0).apply(VdmRuntime.getExpressionEvaluator(),ctxt);
    			MapValue mv = (MapValue)object;
    			return mv.lookup(arg, ctxt);
    		}
			else
			{
    			return RuntimeError.abort(node.getLocation(),4003, "Value " + object + " cannot be applied", ctxt);
			}
    	}
		catch (ValueException e)
		{
			return RuntimeError.abort(node.getLocation(),e);
		}
	}
	
	/*
	 * Unary expressions are in the base class
	 */
	
	/*
	 * Binary expressions are in the base class
	 */
	@Override
	public Value caseACasesExp(ACasesExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseACasesExp(node, ctxt);
	}
	
	@Override
	public Value caseAElseIfExp(AElseIfExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAElseIfExp(node, ctxt);
	}
	
	@Override
	public Value caseAExists1Exp(AExists1Exp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAExists1Exp(node, ctxt);
	}
	
	@Override
	public Value caseAExistsExp(AExistsExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAExistsExp(node, ctxt);
	}
	
	@Override
	public Value caseAFieldExp(AFieldExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAFieldExp(node, ctxt);
	}
	
	@Override
	public Value caseAFieldNumberExp(AFieldNumberExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAFieldNumberExp(node, ctxt);
	}
	
	@Override
	public Value caseAForAllExp(AForAllExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAForAllExp(node, ctxt);
	}
	
	@Override
	public Value caseAFuncInstatiationExp(AFuncInstatiationExp node,
			Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAFuncInstatiationExp(node, ctxt);
	}
	
	@Override
	public Value caseAHistoryExp(AHistoryExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAHistoryExp(node, ctxt);
	}
	
	@Override
	public Value caseAIfExp(AIfExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAIfExp(node, ctxt);
	}
	
	@Override
	public Value caseAIotaExp(AIotaExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAIotaExp(node, ctxt);
	}
	
	@Override
	public Value caseALambdaExp(ALambdaExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseALambdaExp(node, ctxt);
	}
	
	@Override
	public Value caseALetBeStExp(ALetBeStExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseALetBeStExp(node, ctxt);
	}
	
	@Override
	public Value caseALetDefExp(ALetDefExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseALetDefExp(node, ctxt);
	}
	
	/*
	 * Map
	 */
	
	@Override
	public Value caseAMapCompMapExp(AMapCompMapExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAMapCompMapExp(node, ctxt);
	}
	
	@Override
	public Value caseAMapEnumMapExp(AMapEnumMapExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAMapEnumMapExp(node, ctxt);
	}
	
	/*
	 * Map end
	 */
	
	
	@Override
	public Value caseAMapletExp(AMapletExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAMapletExp(node, ctxt);
	}
	
	@Override
	public Value caseAMkBasicExp(AMkBasicExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAMkBasicExp(node, ctxt);
	}
	
	@Override
	public Value caseAMkTypeExp(AMkTypeExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAMkTypeExp(node, ctxt);
	}
	
	@Override
	public Value caseAMuExp(AMuExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAMuExp(node, ctxt);
	}
	
	@Override
	public Value caseANewExp(ANewExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseANewExp(node, ctxt);
	}
	
	@Override
	public Value caseANilExp(ANilExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseANilExp(node, ctxt);
	}
	
	@Override
	public Value caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseANotYetSpecifiedExp(node, ctxt);
	}
	
	@Override
	public Value caseAPostOpExp(APostOpExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAPostOpExp(node, ctxt);
	}
	
	@Override
	public Value caseAPreExp(APreExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAPreExp(node, ctxt);
	}
	
	@Override
	public Value caseAPreOpExp(APreOpExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAPreOpExp(node, ctxt);
	}
	
	@Override
	public Value caseASameBaseClassExp(ASameBaseClassExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseASameBaseClassExp(node, ctxt);
	}
	
	@Override
	public Value caseASameClassExp(ASameClassExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseASameClassExp(node, ctxt);
	}
	
	/*
	 * Seq
	 */
	
	@Override
	public Value caseASeqCompSeqExp(ASeqCompSeqExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseASeqCompSeqExp(node, ctxt);
	}
	
	@Override
	public Value caseASeqEnumSeqExp(ASeqEnumSeqExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseASeqEnumSeqExp(node, ctxt);
	}
	/*
	 * seq end
	 */
	
	@Override
	public Value caseAStateInitExp(AStateInitExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAStateInitExp(node, ctxt);
	}
	
	@Override
	public Value caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseASubclassResponsibilityExp(node, ctxt);
	}
	
	@Override
	public Value caseASubseqExp(ASubseqExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseASubseqExp(node, ctxt);
	}
	
	@Override
	public Value caseAThreadIdExp(AThreadIdExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAThreadIdExp(node, ctxt);
	}
	
	@Override
	public Value caseATimeExp(ATimeExp node, Context ctxt) throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseATimeExp(node, ctxt);
	}
	
	@Override
	public Value caseATupleExp(ATupleExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseATupleExp(node, ctxt);
	}
	
	@Override
	public Value caseAUndefinedExp(AUndefinedExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAUndefinedExp(node, ctxt);
	}
	
	@Override
	public Value caseAVariableExp(AVariableExp node, Context ctxt)
			throws Throwable
	{
		// TODO Auto-generated method stub
		return super.caseAVariableExp(node, ctxt);
	}
}
