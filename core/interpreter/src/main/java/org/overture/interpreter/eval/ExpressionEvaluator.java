package org.overture.interpreter.eval;

import java.util.Collections;
import java.util.Iterator;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ADefExp;
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
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.expressions.AIsOfClassExp;
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
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.AStateInitExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.AThreadIdExp;
import org.overture.ast.expressions.ATimeExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.definition.AExplicitFunctionDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AImplicitFunctionDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsOfBaseClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsOfClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.APostOpExpAssistant;
import org.overture.interpreter.assistant.pattern.ASetBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.assistant.type.ARecordInvariantTypeAssistantInterpreter;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.scheduler.SharedStateListner;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CompFunctionValue;
import org.overture.interpreter.values.FieldMap;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.IntegerValue;
import org.overture.interpreter.values.IterFunctionValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NaturalValue;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.ParameterValue;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.TokenValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.UndefinedValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueMap;
import org.overture.interpreter.values.ValueSet;
import org.overture.typechecker.assistant.pattern.PatternListTC;


public class ExpressionEvaluator extends BinaryExpressionEvaluator
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3877784873512750134L;

	
	@Override
	public Value caseAApplyExp(AApplyExp node, Context ctxt)
			throws AnalysisException
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
    			return VdmRuntimeError.abort(node.getLocation(),4003, "Value " + object + " cannot be applied", ctxt);
			}
    	}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
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
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		Value val = node.getExpression().apply(VdmRuntime.getExpressionEvaluator(),ctxt);

		for (ACaseAlternative c: node.getCases())
		{
			Value rv = eval(c,val, ctxt);
			if (rv != null) return rv;
		}

		if (node.getOthers() != null)
		{
			return node.getOthers().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
		}

		return VdmRuntimeError.abort(node.getLocation(),4004, "No cases apply for " + val, ctxt);
	}
	
	@Override
	public Value caseADefExp(ADefExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		Context evalContext = new Context(node.getLocation(), "def expression", ctxt);

		for (PDefinition d: node.getLocalDefs())
		{
			evalContext.putList(PDefinitionAssistantInterpreter.getNamedValues(d,evalContext));
		}

		return node.getExpression().apply(VdmRuntime.getExpressionEvaluator(),evalContext);
	}
	
	/**
	 * Utility method for ACaseAlternative
	 * @param node
	 * @param val
	 * @param ctxt
	 * @return
	 * @throws AnalysisException 
	 */
	public Value eval(ACaseAlternative node,Value val, Context ctxt) throws AnalysisException
	{
		Context evalContext = new Context(node.getLocation(), "case alternative", ctxt);

		try
		{
			evalContext.putList( PPatternAssistantInterpreter.getNamedValues(node.getPattern(),val, ctxt));
			return node.getResult().apply(VdmRuntime.getExpressionEvaluator(),evalContext);
		}
		catch (PatternMatchException e)
		{
			// Silently fail (CasesExpression will try the others)
		}

		return null;
	}
	
	@Override
	public Value caseAElseIfExp(AElseIfExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			return node.getElseIf().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt) ? node.getThen().apply(VdmRuntime.getExpressionEvaluator(),ctxt) : null;
		}
        catch (ValueException e)
        {
        	return VdmRuntimeError.abort(node.getLocation(),e);
        }
	}
	
	@Override
	public Value caseAExists1Exp(AExists1Exp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		ValueList allValues = null;
		boolean alreadyFound = false;
		
		try
		{
			allValues = PBindAssistantInterpreter.getBindValues(node.getBind(),ctxt);
		} 
		catch (ValueException e)
		{
			VdmRuntimeError.abort(node.getLocation(),e);
		}

		for (Value val: allValues)
		{
			try
			{
				Context evalContext = new Context(node.getLocation(), "exists1", ctxt);
				evalContext.putList( PPatternAssistantInterpreter.getNamedValues(node.getBind().getPattern(),val, ctxt));

				if (node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt))
				{
					if (alreadyFound)
					{
						return new BooleanValue(false);
					}

					alreadyFound = true;
				}
	        }
	        catch (ValueException e)
	        {
	        	VdmRuntimeError.abort(node.getLocation(),e);
	        }
			catch (PatternMatchException e)
			{
				// Ignore pattern mismatches
			}
		}

		return new BooleanValue(alreadyFound);
	}
	
	@Override
	public Value caseAExistsExp(AExistsExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb: node.getBindList())
			{
				ValueList bvals = PMultipleBindAssistantInterpreter.getBindValues(mb,ctxt);

				for (PPattern p: mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init();

			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(node.getLocation(), "exists", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches && node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt))
				{
					return new BooleanValue(true);
				}
			}
		}
	    catch (ValueException e)
	    {
	    	VdmRuntimeError.abort(node.getLocation(),e);
	    }

		return new BooleanValue(false);
	}
	
	@Override
	public Value caseAFieldExp(AFieldExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		node.getField().location.hit();

		try
		{
    		Value v = node.getObject().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
    		PType objtype = null;
    		Value r = null;

    		if (v.isType(ObjectValue.class))
    		{
    			ObjectValue ov = v.objectValue(ctxt);
    	   		objtype = ov.type;
    	   		r = ov.get(node.getMemberName(), node.getMemberName().explicit);
    		}
    		else
    		{
    			RecordValue rv = v.recordValue(ctxt);
    	   		objtype = rv.type;
    			FieldMap fields = rv.fieldmap;
         		r = fields.get(node.getField().name);
    		}

    		if (r == null)
    		{
    			VdmRuntimeError.abort(node.getLocation(),4006, "Type " + objtype + " has no field " + node.getField().name, ctxt);
    		}

    		return r;
        }
        catch (ValueException e)
        {
        	return VdmRuntimeError.abort(node.getLocation(),e);
        }
	}
	
	@Override
	public Value caseAFieldNumberExp(AFieldNumberExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		node.getField().location.hit();

		try
		{
    		ValueList fields = node.getTuple().apply(VdmRuntime.getExpressionEvaluator(),ctxt).tupleValue(ctxt);
    		Value r = fields.get((int)node.getField().value - 1);

    		if (r == null)
    		{
    			VdmRuntimeError.abort(node.getLocation(),4007, "No such field in tuple: #" + node.getField(), ctxt);
    		}

    		return r;
        }
        catch (ValueException e)
        {
        	return VdmRuntimeError.abort(node.getLocation(),e);
        }
	}
	
	@Override
	public Value caseAForAllExp(AForAllExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb: node.getBindList())
			{
				ValueList bvals = PMultipleBindAssistantInterpreter.getBindValues(mb, ctxt);

				for (PPattern p: mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init();

			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(node.getLocation(), "forall", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches && !node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt))
				{
					return new BooleanValue(false);
				}
			}
		}
	    catch (ValueException e)
	    {
	    	return VdmRuntimeError.abort(node.getLocation(),e);
	    }

		return new BooleanValue(true);
	}
	
	@Override
	public Value caseAFuncInstatiationExp(AFuncInstatiationExp node,
			Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
    		FunctionValue fv = node.getFunction().apply(VdmRuntime.getExpressionEvaluator(),ctxt).functionValue(ctxt);

    		if (!fv.uninstantiated)
    		{
    			VdmRuntimeError.abort(node.getLocation(),3034, "Function is already instantiated: " + fv.name, ctxt);
    		}

    		PTypeList fixed = new PTypeList();

    		for (PType ptype: node.getActualTypes())
    		{
    			if (ptype instanceof AParameterType)
    			{
    				AParameterType pname = (AParameterType)ptype;
    				Value t = ctxt.lookup(pname.getName());

    				if (t == null)
    				{
    					VdmRuntimeError.abort(node.getLocation(),4008, "No such type parameter @" + pname + " in scope", ctxt);
    				}
    				else if (t instanceof ParameterValue)
    				{
    					ParameterValue tv = (ParameterValue)t;
    					fixed.add(tv.type);
    				}
    				else
    				{
    					VdmRuntimeError.abort(node.getLocation(),4009, "Type parameter/local variable name clash, @" + pname, ctxt);
    				}
    			}
    			else
    			{
    				fixed.add(ptype);
    			}
    		}
    		
    		FunctionValue rv = null;
    		
    		if (node.getExpdef() == null)
			{
				rv = AImplicitFunctionDefinitionAssistantInterpreter.getPolymorphicValue(node.getImpdef(),fixed);
			}
			else
			{
				rv = AExplicitFunctionDefinitionAssistantInterpreter.getPolymorphicValue(node.getExpdef(),fixed);
			}

    		rv.setSelf(fv.self);
			rv.uninstantiated = false;
			return rv;
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseAHistoryExp(AHistoryExp node, Context ctxt)
			throws AnalysisException
	{
		try
		{
			// TODO Not very efficient to do this every time. But we can't
			// save the list because the same HistoryExpression is called from
			// different object instance contexts, and each instance has its
			// own operation history counters...

			ValueList operations = new ValueList();
			ObjectValue self = ((ObjectContext)ctxt).self;

			for (LexNameToken opname: node.getOpnames())
			{
				operations.addAll(self.getOverloads(opname));
			}

			int result = 0;

    		for (Value v: operations)
    		{
    			OperationValue ov = v.operationValue(ctxt);

    			switch (node.getHop().type)
    			{
    				case ACT:
    					result += ov.hashAct;
    					break;

    				case FIN:
       					result += ov.hashFin;
    					break;

    				case REQ:
       					result += ov.hashReq;
    					break;

    				case ACTIVE:
       					result += ov.hashAct - ov.hashFin;
    					break;

    				case WAITING:
       					result += ov.hashReq - ov.hashAct;
    					break;

    				default:
    					VdmRuntimeError.abort(node.getLocation(),4011, "Illegal history operator: " + node.getHop(), ctxt);

    			}
    		}

    		node.getLocation().hit();
    		return new NaturalValue(result);
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
		catch (Exception e)
		{
			return VdmRuntimeError.abort(node.getLocation(),4065, e.getMessage(), ctxt);
		}
	}
	
	@Override
	public Value caseAIsExp(AIsExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		Value v = node.getTest().apply(VdmRuntime.getExpressionEvaluator(),ctxt);

		try
		{
    		if (node.getTypeName() != null)
    		{
    			if (node.getTypedef() != null)
    			{
    				if (PDefinitionAssistantInterpreter.isTypeDefinition(node.getTypedef()))
    				{
    					// NB. we skip the DTC enabled check here
    					v.convertValueTo(PDefinitionAssistantInterpreter.getType(node.getTypedef()), ctxt);
    					return new BooleanValue(true);
    				}
    			}
    			else if (v.isType(RecordValue.class))
    			{
    				RecordValue rv = v.recordValue(ctxt);
    				return new BooleanValue(rv.type.getName().equals(node.getTypeName()));
    			}
    		}
    		else
    		{
    			// NB. we skip the DTC enabled check here
   				v.convertValueTo(node.getBasicType(), ctxt);
   				return new BooleanValue(true);
    		}
		}
		catch (ValueException ex)
		{
			// return false...
		}

		return new BooleanValue(false);
	}
	
	@Override
	public Value caseAIfExp(AIfExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
    		if (node.getTest().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt))
    		{
    			return node.getThen().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
    		}

    		for (AElseIfExp elseif: node.getElseList())
			{
				Value r = elseif.apply(VdmRuntime.getExpressionEvaluator(),ctxt);
				if (r != null) return r;
			}

			return node.getElse().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
        }
        catch (ValueException e)
        {
        	return VdmRuntimeError.abort(node.getLocation(),e);
        }
	}
	
	@Override
	public Value caseAIotaExp(AIotaExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		ValueList allValues = null;
		Value result = null;

		try
		{
			allValues = PBindAssistantInterpreter.getBindValues(node.getBind(),ctxt);
		}
		catch (ValueException e)
		{
			VdmRuntimeError.abort(node.getLocation(),e);
		}

		for (Value val: allValues)
		{
			try
			{
				Context evalContext = new Context(node.getLocation(), "iota", ctxt);
				evalContext.putList(PPatternAssistantInterpreter.getNamedValues(node.getBind().getPattern(),val, ctxt));

				if (node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt))
				{
					if (result != null && !result.equals(val))
					{
						VdmRuntimeError.abort(node.getLocation(),4013, "Iota selects more than one result", ctxt);
					}

					result = val;
				}
			}
	        catch (ValueException e)
	        {
	        	VdmRuntimeError.abort(node.getLocation(),e);
	        }
			catch (PatternMatchException e)
			{
				// Ignore pattern mismatches
			}
	    }

		if (result != null)
		{
			return result;
		}

		return VdmRuntimeError.abort(node.getLocation(),4014, "Iota does not select a result", ctxt);
	}
	
	@Override
	public Value caseALambdaExp(ALambdaExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		// Free variables are everything currently visible from this
		// context (but without the context chain).

		Context free = ctxt.getVisibleVariables();

		PatternListTC list = new PatternListTC();
		list.addAll(node.getParamPatterns());
		 
		return new FunctionValue(node.getLocation(), "lambda",(AFunctionType) node.getType(),
				list, node.getExpression(), free);
	}
	
	@Override
	public Value caseALetBeStExp(ALetBeStExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb: node.getDef().getBindings())
			{
				ValueList bvals =  PMultipleBindAssistantInterpreter.getBindValues(mb,ctxt);

				for (PPattern p: mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init();

			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(node.getLocation(), "let be st expression", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches &&
					(node.getSuchThat() == null || node.getSuchThat().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt)))
				{
					return node.getValue().apply(VdmRuntime.getExpressionEvaluator(),evalContext);
				}
			}
		}
        catch (ValueException e)
        {
        	VdmRuntimeError.abort(node.getLocation(),e);
        }

		return VdmRuntimeError.abort(node.getLocation(),4015, "Let be st found no applicable bindings", ctxt);
	}
	
	@Override
	public Value caseALetDefExp(ALetDefExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		Context evalContext = new Context(node.getLocation(), "let expression", ctxt);

		LexNameToken sname = new LexNameToken(node.getLocation().module, "self", node.getLocation());
		ObjectValue self = (ObjectValue)ctxt.check(sname);

		for (PDefinition d: node.getLocalDefs())
		{
			NameValuePairList values = PDefinitionAssistantInterpreter.getNamedValues(d,evalContext);

			if (self != null && d instanceof AExplicitFunctionDefinition)
			{
				for (NameValuePair nvp: values)
				{
					if (nvp.value instanceof FunctionValue)
					{
						FunctionValue fv = (FunctionValue)nvp.value;
						fv.setSelf(self);
					}
				}
			}

			evalContext.putList(values);
		}

		return node.getExpression().apply(VdmRuntime.getExpressionEvaluator(),evalContext);
	}
	
	/*
	 * Map
	 */
	
	@Override
	public Value caseAMapCompMapExp(AMapCompMapExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		ValueMap map = new ValueMap();

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb: node.getBindings())
			{
				ValueList bvals = PMultipleBindAssistantInterpreter.getBindValues(mb,ctxt);

				for (PPattern p: mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init();

			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(node.getLocation(), "map comprehension", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches &&
					(node.getPredicate() == null ||
					 node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt)))
				{
					Value dom = node.getFirst().getLeft().apply(VdmRuntime.getExpressionEvaluator(),evalContext);
					Value rng = node.getFirst().getRight().apply(VdmRuntime.getExpressionEvaluator(),evalContext);
					node.getFirst().getLocation().hit();

					Value old = map.put(dom, rng);

					if (old != null && !old.equals(rng))
					{
						VdmRuntimeError.abort(node.getLocation(),4016, "Duplicate map keys have different values: " + dom, ctxt);
					}
				}
			}
		}
	    catch (ValueException e)
	    {
	    	return VdmRuntimeError.abort(node.getLocation(),e);
	    }

		return new MapValue(map);
	}
	
	@Override
	public Value caseAMapEnumMapExp(AMapEnumMapExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueMap map = new ValueMap();

		for (AMapletExp e: node.getMembers())
		{
			Value l = e.getLeft().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
			Value r = e.getRight().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
			e.getLocation().hit();
			Value old = map.put(l, r);

			if (old != null && !old.equals(r))
			{
				VdmRuntimeError.abort(node.getLocation(),4017, "Duplicate map keys have different values: " + l, ctxt);
			}
		}

		return new MapValue(map);
	}
	
	/*
	 * Map end
	 */
	
	
	@Override
	public Value caseAMapletExp(AMapletExp node, Context ctxt)
			throws AnalysisException
	{
		//Not used
		return null;
	}
	
	@Override
	public Value caseAMkBasicExp(AMkBasicExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		Value v = node.getArg().apply(VdmRuntime.getExpressionEvaluator(),ctxt);

		if (node.getType() instanceof ATokenBasicType)
		{
			return new TokenValue(v);
		}
		else
		{
			try
			{
				v = v.convertTo(node.getType(), ctxt);
			}
			catch (ValueException e)
			{
				VdmRuntimeError.abort(node.getLocation(),4022, "mk_ type argument is not " + node.getType(), ctxt);
			}
		}

		return v;
	}
	
	@Override
	public Value caseAMkTypeExp(AMkTypeExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueList argvals = new ValueList();

		for (PExp e: node.getArgs())
		{
			argvals.add(e.apply(VdmRuntime.getExpressionEvaluator(),ctxt));
		}

		try
		{
    		return new RecordValue(node.getRecordType(), argvals, ctxt);
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseAMuExp(AMuExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
    		RecordValue r = node.getRecord().apply(VdmRuntime.getExpressionEvaluator(),ctxt).recordValue(ctxt);
    		FieldMap fields = new FieldMap(r.fieldmap);

    		for (ARecordModifier rm: node.getModifiers())
    		{
    			AFieldField f = ARecordInvariantTypeAssistantInterpreter.findField(r.type, rm.getTag().name);

    			if (f == null)
    			{
        			VdmRuntimeError.abort(node.getLocation(),4023, "Mu type conflict? No field tag " + rm.getTag().name, ctxt);
    			}
    			else
    			{
    				fields.add(rm.getTag().name, rm.getValue().apply(VdmRuntime.getExpressionEvaluator(),ctxt), !f.getEqualityAbstraction());
    			}
     		}

     		return new RecordValue(r.type, fields, ctxt);
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseANewExp(ANewExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		node.getClassName().location.hit();

		try
		{
    		ValueList argvals = new ValueList();

     		for (PExp arg: node.getArgs())
    		{
    			argvals.add(arg.apply(VdmRuntime.getExpressionEvaluator(),ctxt));
    		}

			ObjectValue objval =
				SClassDefinitionAssistantInterpreter.newInstance(node.getClassdef(),node.getCtorDefinition(), argvals, ctxt);

    		if (objval.invlistener != null)
    		{
    			// Check the initial values of the object's fields
    			objval.invlistener.doInvariantChecks = true;
    			objval.invlistener.changedValue(node.getLocation(), objval, ctxt);
    		}

    		return objval;
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseANilExp(ANilExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		return new NilValue();
	}
	
	@Override
	public Value caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		return VdmRuntimeError.abort(node.getLocation(),4024, "'not yet specified' expression reached", ctxt);
	}
	
	@Override
	public Value caseAPostOpExp(APostOpExp node, Context ctxt)
			throws AnalysisException
	{
		// No break check here, as we want to start in the expression

				// The postcondition function arguments are the function args, the
				// result, the old/new state (if any). These all exist in ctxt.
				// We find the Sigma record and expand its contents to give additional
				// values in ctxt for each field. Ditto with Sigma~.

				try
				{
		    		if (node.getState() != null)
		    		{
		    			RecordValue sigma = ctxt.lookup(node.getState().getName()).recordValue(ctxt);

		    			for (AFieldField field: node.getState().getFields())
		    			{
		    				ctxt.put(field.getTagname(), sigma.fieldmap.get(field.getTag()));
		    			}

		    			RecordValue oldsigma = ctxt.lookup(node.getState().getName().getOldName()).recordValue(ctxt);

		    			for (AFieldField field: node.getState().getFields())
		    			{
		    				ctxt.put(field.getTagname().getOldName(), oldsigma.fieldmap.get(field.getTag()));
		    			}
		    		}
		    		else if (ctxt instanceof ObjectContext)
		    		{
		    			ObjectContext octxt = (ObjectContext)ctxt;
		    			LexNameToken selfname = node.getOpname().getSelfName();
		    			LexNameToken oldselfname = selfname.getOldName();

		    			ObjectValue self = octxt.lookup(selfname).objectValue(ctxt);
		    			ValueMap oldvalues = octxt.lookup(oldselfname).mapValue(ctxt);

		    			// If the opname was defined in a superclass of "self", we have
		    			// to discover the subobject to populate its state variables.

		    			ObjectValue subself = APostOpExpAssistant.findObject(node,node.getOpname().module, self);

		    			if (subself == null)
		    			{
		    				VdmRuntimeError.abort(node.getLocation(),4026, "Cannot create post_op environment", ctxt);
		    			}

		    			// Create an object context using the "self" passed in, rather
		    			// than the self that we're being called from, assuming they
		    			// are different.

		    			if (subself != octxt.self)
		    			{
		        			ObjectContext selfctxt = new ObjectContext(
		        				ctxt.location, "postcondition's object", ctxt, subself);

		        			selfctxt.putAll(ctxt);	// To add "RESULT" and args.
		        			ctxt = selfctxt;
		    			}

		    			APostOpExpAssistant.populate(node,ctxt, subself.type.getName().name, oldvalues);		// To add old "~" values
		    		}


		    		// If there are errs clauses, and there is a precondition defined, then
		    		// we evaluate that as well as the postcondition.

		    		boolean result =
		    			(node.getErrors().isEmpty() || node.getPreexpression() == null || node.getPreexpression().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt)) &&
		    			node.getPostexpression().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt);

		    		node.setErrorLocation( node.getLocation());//FIXME not good 

		    		if (node.getErrors() != null)
		    		{
		    			for (AErrorCase err: node.getErrors())
		    			{
		    				boolean left  = err.getLeft().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt);
		    				boolean right = err.getRight().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt);

		    				if (left && !right)
		    				{
		    					node.setErrorLocation( err.getLeft().getLocation());//FIXME not good 
		    				}

		    				result = result || (left && right);
		    			}
		    		}

		    		return new BooleanValue(result);
				}
				catch (ValueException e)
				{
					return VdmRuntimeError.abort(node.getLocation(),e);
				}
	}
	
	@Override
	public Value caseAPreExp(APreExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		Value fv = node.getFunction().apply(VdmRuntime.getExpressionEvaluator(),ctxt);

		if (fv instanceof FunctionValue)
		{
			FunctionValue tfv = (FunctionValue)fv;

			while (true)
			{
    			if (tfv instanceof CompFunctionValue)
    			{
    				tfv = ((CompFunctionValue)tfv).ff1;
    				continue;
    			}

    			if (tfv instanceof IterFunctionValue)
    			{
    				tfv = ((IterFunctionValue)tfv).function;
    				continue;
    			}

    			break;
			}

			FunctionValue pref = tfv.precondition;

			if (pref == null)
			{
				return new BooleanValue(true);
			}

			if (pref.type.getParameters().size() <= node.getArgs().size())
			{
				try
				{
    				ValueList argvals = new ValueList();
    				Iterator<PExp> aiter = node.getArgs().iterator();

    				for (@SuppressWarnings("unused") PType t: pref.type.getParameters())
    				{
    					argvals.add(aiter.next().apply(VdmRuntime.getExpressionEvaluator(),ctxt));
    				}

					return pref.eval(node.getLocation(), argvals, ctxt);
				}
				catch (ValueException e)
				{
					VdmRuntimeError.abort(node.getLocation(),e);
				}
			}

			// else true, below.
		}

		return new BooleanValue(true);
	}
	
	@Override
	public Value caseAPreOpExp(APreOpExp node, Context ctxt)
			throws AnalysisException
	{
		try
    	{
    		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

    		// The precondition function arguments are the function args,
    		// plus the state (if any). These all exist in ctxt. We find the
    		// Sigma record and expand its contents to give additional
    		// values in ctxt for each field.

    		if (node.getState() != null)
    		{
    			try
    			{
    				RecordValue sigma = ctxt.lookup(node.getState().getName()).recordValue(ctxt);

    				for (AFieldField field: node.getState().getFields())
    				{
    					ctxt.put(field.getTagname(), sigma.fieldmap.get(field.getTag()));
    				}
    			}
    			catch (ValueException e)
    			{
    				VdmRuntimeError.abort(node.getLocation(),e);
    			}
    		}
    		else if (ctxt instanceof ObjectContext)
    		{
    			ObjectContext octxt = (ObjectContext)ctxt;
    			LexNameToken selfname = node.getOpname().getSelfName();
    			ObjectValue self = octxt.lookup(selfname).objectValue(ctxt);

    			// Create an object context using the "self" passed in, rather
    			// than the self that we're being called from.

    			ObjectContext selfctxt = new ObjectContext(
    				ctxt.location, "precondition's object", ctxt, self);

    			selfctxt.putAll(ctxt);	// To add "RESULT" and args.
    			ctxt = selfctxt;
    		}

    		boolean result = node.getExpression().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt);

    		if (node.getErrors() != null)
    		{
    			for (AErrorCase err: node.getErrors())
    			{
    				result = result || err.getLeft().apply(VdmRuntime.getExpressionEvaluator(),ctxt).boolValue(ctxt);
    			}
    		}

    		return new BooleanValue(result);
    	}
    	catch (ValueException e)
    	{
    		return VdmRuntimeError.abort(node.getLocation(),e);
    	}
	}
	
	@Override
	public Value caseASameBaseClassExp(ASameBaseClassExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			Value l = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
			Value r = node.getRight().apply(VdmRuntime.getExpressionEvaluator(),ctxt);

			if (!l.isType(ObjectValue.class) ||
				!r.isType(ObjectValue.class))
			{
				return new BooleanValue(false);
			}

			ObjectValue lv = l.objectValue(ctxt);
			ObjectValue rv = r.objectValue(ctxt);

			PTypeList lbases = lv.getBaseTypes();
			PTypeList rbases = rv.getBaseTypes();

			for (PType ltype: lbases)
			{
				if (rbases.contains(ltype))
				{
					return new BooleanValue(true);
				}
			}

			return new BooleanValue(false);
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseASameClassExp(ASameClassExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			Value l = node.getLeft().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
			Value r = node.getRight().apply(VdmRuntime.getExpressionEvaluator(),ctxt);

			if (!l.isType(ObjectValue.class) ||
				!r.isType(ObjectValue.class))
			{
				return new BooleanValue(false);
			}

			ObjectValue lv = l.objectValue(ctxt);
			ObjectValue rv = r.objectValue(ctxt);

			return new BooleanValue(lv.type.equals(rv.type));
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	/*
	 * Seq
	 */
	
	@Override
	public Value caseASeqCompSeqExp(ASeqCompSeqExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueList allValues = ASetBindAssistantInterpreter.getBindValues(node.getSetBind(),ctxt);

		ValueSet seq = new ValueSet();	// Bind variable values
		ValueMap map = new ValueMap();	// Map bind values to output values

		for (Value val: allValues)
		{
			try
			{
				Context evalContext = new Context(node.getLocation(), "seq comprehension", ctxt);
				NameValuePairList nvpl = PPatternAssistantInterpreter.getNamedValues(node.getSetBind().getPattern(),val, ctxt);
				Value sortOn = nvpl.get(0).value;

				if (map.get(sortOn) == null)
				{
    				if (nvpl.size() != 1 || !sortOn.isNumeric())
    				{
    					VdmRuntimeError.abort(node.getLocation(),4029, "Sequence comprehension bindings must be one numeric value", ctxt);
    				}

    				evalContext.putList(nvpl);

    				if (node.getPredicate() == null || node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt))
    				{
    					Value out = node.getFirst().apply(VdmRuntime.getExpressionEvaluator(),evalContext);
   						seq.add(sortOn);
   						map.put(sortOn, out);
    				}
				}
			}
			catch (ValueException e)
			{
				VdmRuntimeError.abort(node.getLocation(),e);
			}
			catch (PatternMatchException e)
			{
				// Ignore mismatches
			}
		}

		Collections.sort(seq);	// Using compareTo
		ValueList sorted = new ValueList();

		for (Value bv: seq)
		{
			sorted.add(map.get(bv));
		}

		return new SeqValue(sorted);
	}
	
	@Override
	public Value caseASeqEnumSeqExp(ASeqEnumSeqExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueList values = new ValueList();

		for (PExp e: node.getMembers())
		{
			values.add(e.apply(VdmRuntime.getExpressionEvaluator(),ctxt));
		}

		return new SeqValue(values);
	}
	/*
	 * seq end
	 */
	
	/*
	 * (non-Javadoc) Set start
	 * @see org.overture.ast.analysis.QuestionAnswerAdaptor#caseAStateInitExp(org.overture.ast.expressions.AStateInitExp, java.lang.Object)
	 */
	@Override
	public Value caseASetEnumSetExp(ASetEnumSetExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueSet values = new ValueSet();

		for (PExp e: node.getMembers())
		{
			values.add(e.apply(VdmRuntime.getExpressionEvaluator(),ctxt));
		}

		return new SetValue(values);
	}
	
	@Override
	public Value caseASetCompSetExp(ASetCompSetExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		ValueSet set = new ValueSet();

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb: node.getBindings())
			{
				ValueList bvals = PMultipleBindAssistantInterpreter.getBindValues(mb,ctxt);

				for (PPattern p: mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init();

			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(node.getLocation(), "set comprehension", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches &&
					(node.getPredicate() == null ||
							node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(),evalContext).boolValue(ctxt)))
				{
					set.add(node.getFirst().apply(VdmRuntime.getExpressionEvaluator(),evalContext));
				}
			}
		}
		catch (ValueException e)
		{
			return  VdmRuntimeError.abort(node.getLocation(),e);
		}

		return new SetValue(set);
	}
	
	@Override
	public Value caseASetRangeSetExp(ASetRangeSetExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
    		long from = node.getFirst().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
    		long to =  node.getLast().apply(VdmRuntime.getExpressionEvaluator(), ctxt).intValue(ctxt);
    		ValueSet set = new ValueSet();

    		for (long i=from; i<= to; i++)
    		{
    			set.add(new IntegerValue(i));
    		}

    		return new SetValue(set);
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	
	@Override
	public Value caseAStateInitExp(AStateInitExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
			FunctionValue invariant = VdmRuntime.getNodeState(node.getState()).invfunc;

			// Note, the function just checks whether the argument passed would
			// violate the state invariant (if any). It doesn't initialize the
			// state itself. This is done in State.initialize().

			if (invariant != null)
			{
				AIdentifierPattern argp = (AIdentifierPattern)node.getState().getInitPattern();
				RecordValue rv = (RecordValue)ctxt.lookup(argp.getName());
				return invariant.eval(node.getLocation(), rv, ctxt);
			}

			return new BooleanValue(true);
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		return VdmRuntimeError.abort(node.getLocation(),4032, "'is subclass responsibility' expression reached", ctxt);
	}
	
	@Override
	public Value caseASubseqExp(ASubseqExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		try
		{
    		ValueList list = node.getSeq().apply(VdmRuntime.getExpressionEvaluator(),ctxt).seqValue(ctxt);
    		double fr = node.getFrom().apply(VdmRuntime.getExpressionEvaluator(),ctxt).realValue(ctxt);
    		double tr = node.getTo().apply(VdmRuntime.getExpressionEvaluator(),ctxt).realValue(ctxt);
    		int fi = (int)Math.ceil(fr);
    		int ti = (int)Math.floor(tr);

    		if (fi < 1)
    		{
    			fi = 1;
    		}

    		if (ti > list.size())
    		{
    			ti = list.size();
    		}

    		ValueList result = new ValueList();

    		if (fi <= ti)
    		{
        		result.addAll(list.subList(fi-1, ti));
    		}

    		return new SeqValue(result);
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseAThreadIdExp(AThreadIdExp node, Context ctxt)
			throws AnalysisException
	{
		try
		{
			node.getLocation().hit();
			return new NaturalValue(ctxt.threadState.threadId);
		}
		catch (Exception e)
		{
			return VdmRuntimeError.abort(node.getLocation(),4065, e.getMessage(), ctxt);
		}
	}
	
	@Override
	public Value caseATimeExp(ATimeExp node, Context ctxt) throws AnalysisException
	{
		node.getLocation().hit();

		try
        {
	        return new NaturalValue(SystemClock.getWallTime());
        }
        catch (Exception e)
        {
        	return VdmRuntimeError.abort(node.getLocation(),4145, "Time: " + e.getMessage(), ctxt);
        }
	}
	
	@Override
	public Value caseATupleExp(ATupleExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		ValueList argvals = new ValueList();

 		for (PExp arg: node.getArgs())
		{
			argvals.add(arg.apply(VdmRuntime.getExpressionEvaluator(),ctxt));
		}

		return new TupleValue(argvals);
	}
	
	@Override
	public Value caseAUndefinedExp(AUndefinedExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		return new UndefinedValue();
	}
	
	@Override
	public Value caseAVariableExp(AVariableExp node, Context ctxt)
			throws AnalysisException
	{
		//Experimental hood added for DESTECS
		if (Settings.dialect == Dialect.VDM_RT)
		{
			SharedStateListner.beforeVariableReadDuration(node);
		}
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		return ctxt.lookup(node.getName());
	}
	
	@Override
	public Value caseASelfExp(ASelfExp node, Context ctxt)
			throws AnalysisException
	{
		node.getLocation().hit();
		return ctxt.lookup(node.getName());
	}
	
	@Override
	public Value caseAIsOfBaseClassExp(AIsOfBaseClassExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		node.getBaseClass().location.hit();

		try
		{
			Value v = node.getExp().apply(VdmRuntime.getExpressionEvaluator(), ctxt).deref();

			if (!(v instanceof ObjectValue))
			{
				return new BooleanValue(false);
			}

			ObjectValue ov = v.objectValue(ctxt);
			return new BooleanValue(AIsOfBaseClassExpAssistantInterpreter.search(node,ov));
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
	
	@Override
	public Value caseAIsOfClassExp(AIsOfClassExp node, Context ctxt)
			throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);
		node.getClassName().location.hit();

		try
		{
			Value v = node.getExp().apply(VdmRuntime.getExpressionEvaluator(),ctxt).deref();

			if (!(v instanceof ObjectValue))
			{
				return new BooleanValue(false);
			}

			ObjectValue ov = v.objectValue(ctxt);
			return new BooleanValue(AIsOfClassExpAssistantInterpreter.isOfClass(ov, node.getClassName().name));
		}
		catch (ValueException e)
		{
			return VdmRuntimeError.abort(node.getLocation(),e);
		}
	}
}
