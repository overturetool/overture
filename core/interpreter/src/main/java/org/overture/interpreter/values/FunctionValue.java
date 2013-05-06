/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.values;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.ClassContext;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.pattern.PatternListTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;



public class FunctionValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final ILexLocation location;
	public final String name;
	public NameValuePairList typeValues;
	public AFunctionType type;
	public final List<List<PPattern>> paramPatternList;
	public final PExp body;
	public final FunctionValue precondition;
	public final FunctionValue postcondition;
	public final Context freeVariables;

	// Causes parameter assignments to check their invariants (if any).
	// This is set to false for inv_() functions, which cannot check them.
	private final boolean checkInvariants;

	// Measure function value, if any
	private ILexNameToken measureName = null;
	private FunctionValue measure = null;
	private Map<Long, Stack<Value>> measureValues = null;
	private Set<Long> measuringThreads = null;
	private Set<Long> callingThreads = null;
	private ValueList curriedArgs = null;
	private boolean isMeasure = false;

	public ObjectValue self = null;
	public boolean isStatic = false;
	public boolean uninstantiated = false;
	private SClassDefinition classdef = null;

	private FunctionValue(ILexLocation location, String name, AFunctionType type,
			List<List<PPattern>> paramPatternList, PExp body,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables, boolean checkInvariants, ValueList curriedArgs,
		ILexNameToken measureName, Map<Long, Stack<Value>> measureValues)
	{
		this.location = location;
		this.name = name;
		this.typeValues = null;
		this.type = type;
		this.paramPatternList = paramPatternList;
		this.body = body;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.freeVariables = freeVariables;
		this.checkInvariants = checkInvariants;
		this.curriedArgs = curriedArgs;
		
		if (Settings.measureChecks && measureName != null)
		{
			this.measureName = measureName;
			this.measureValues = measureValues;	// NB. a copy of the base FunctionValue's
		}
	}

	public FunctionValue(ILexLocation location, String name, AFunctionType type,
		PatternListTC paramPatterns, PExp body, Context freeVariables)
	{
		this.location = location;
		this.name = name;
		this.typeValues = null;
		this.type = type;
		this.paramPatternList = new Vector<List<PPattern>>();
		this.body = body;
		this.precondition = null;
		this.postcondition = null;
		this.freeVariables = freeVariables;
		this.checkInvariants = true;

		paramPatternList.add(paramPatterns);
	}

	public FunctionValue(AExplicitFunctionDefinition def,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables)
	{
		this.location = def.getLocation();
		this.name = def.getName().getName();
		this.typeValues = null;
		this.type = def.getType();
		this.paramPatternList = def.getParamPatternList();
		this.body = def.getBody();
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.freeVariables = freeVariables;
		this.checkInvariants = !def.getIsTypeInvariant();
		this.classdef = def.getClassDefinition();

		if (Settings.measureChecks && def.getMeasureDef() != null)
		{
			measureName = def.getMeasureDef().getName();
			measureValues = Collections.synchronizedMap(new HashMap<Long, Stack<Value>>());
		}
	}

	public FunctionValue(AImplicitFunctionDefinition def,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables)
	{
		this.location = def.getLocation();
		this.name = def.getName().getName();
		this.typeValues = null;
		this.type = def.getType();

		this.paramPatternList = new Vector<List<PPattern>>();
		PatternListTC plist = new PatternListTC();

		for (APatternListTypePair ptp: def.getParamPatterns())
		{
			plist.addAll(ptp.getPatterns());
		}

		this.paramPatternList.add(plist);

		this.body = def.getBody();
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.freeVariables = freeVariables;
		this.checkInvariants = true;
		this.classdef = def.getClassDefinition();

		if (Settings.measureChecks && def.getMeasureDef() != null)
		{
			measureName = def.getMeasureDef().getName();
			measureValues = Collections.synchronizedMap(new HashMap<Long, Stack<Value>>());
		}
	}

	public FunctionValue(AImplicitFunctionDefinition fdef,
		PTypeList actualTypes, FunctionValue precondition,
		FunctionValue postcondition, Context freeVariables)
	{
		this(fdef, precondition, postcondition, freeVariables);
		this.typeValues = new NameValuePairList();
		this.type =  AImplicitFunctionDefinitionAssistantTC.getType(fdef, actualTypes);

		Iterator<PType> ti = actualTypes.iterator();

		for (ILexNameToken pname: fdef.getTypeParams())
		{
			PType ptype = ti.next();
			typeValues.add(new NameValuePair(pname, new ParameterValue(ptype)));
		}
	}

	public FunctionValue(AExplicitFunctionDefinition fdef,
		PTypeList actualTypes, FunctionValue precondition,
		FunctionValue postcondition, Context freeVariables)
	{
		this(fdef, precondition, postcondition, freeVariables);
		this.typeValues = new NameValuePairList();
		this.type = AExplicitFunctionDefinitionAssistantTC.getType(fdef,actualTypes);

		Iterator<PType> ti = actualTypes.iterator();

		for (ILexNameToken pname: fdef.getTypeParams())
		{
			PType ptype = ti.next();
			typeValues.add(new NameValuePair(pname, new ParameterValue(ptype)));
		}
	}

	// This constructor is used by IterFunctionValue and CompFunctionValue
	// The methods which matter are overridden in those classes.

	public FunctionValue(ILexLocation location, AFunctionType type, String name)
	{
		this.location = location;
		this.name = name;
		this.typeValues = null;
		this.type = type;
		this.paramPatternList = null;
		this.body = null;
		this.precondition = null;
		this.postcondition = null;
		this.freeVariables = null;
		this.checkInvariants = true;
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	public Value eval(
		ILexLocation from, Value arg, Context ctxt) throws ValueException
	{
		ValueList args = new ValueList(arg);
		return eval(from, args, ctxt, null);
	}

	public Value eval(
		ILexLocation from, ValueList argValues, Context ctxt) throws ValueException
	{
		return eval(from, argValues, ctxt, null);
	}

	public void setSelf(ObjectValue self)
	{
		if (!isStatic)
		{
			this.self = self;

			if (measure != null)
			{
				measure.setSelf(self);
			}
		}
	}

	public void setClass(SClassDefinition classdef)
	{
		this.classdef = classdef;
	}

	public Value eval(
		ILexLocation from, ValueList argValues, Context ctxt, Context sctxt) throws ValueException
	{
		if (body == null)
		{
			abort(4051, "Cannot apply implicit function: " + name, ctxt);
		}

		if (uninstantiated)
		{
			abort(3033, "Polymorphic function has not been instantiated: " + name, ctxt);
		}

		List<PPattern> paramPatterns = paramPatternList.get(0);
		RootContext evalContext = newContext(from, toTitle(), ctxt, sctxt);

		if (typeValues != null)
		{
			// Add any @T type values, for recursive polymorphic functions
			evalContext.putList(typeValues);
		}

		if (argValues.size() != paramPatterns.size())
		{
			VdmRuntimeError.abort(type.getLocation(),4052, "Wrong number of arguments passed to " + name, ctxt);
		}

		Iterator<Value> valIter = argValues.iterator();
		Iterator<PType> typeIter = type.getParameters().iterator();
		NameValuePairMap args = new NameValuePairMap();

		for (PPattern p: paramPatterns)
		{
			Value pv = valIter.next();

			if (checkInvariants)	// Don't even convert invariant arg values
			{
				pv = pv.convertTo(typeIter.next(), ctxt);
			}

			try
			{
				for (NameValuePair nvp: PPatternAssistantInterpreter.getNamedValues(p,pv, ctxt))
				{
					Value v = args.get(nvp.name);

					if (v == null)
					{
						args.put(nvp);
					}
					else	// Names match, so values must also
					{
						if (!v.equals(nvp.value))
						{
							abort(4053, "Parameter patterns do not match arguments", ctxt);
						}
					}
				}
			}
			catch (PatternMatchException e)
			{
				abort(e.number, e, ctxt);
			}
		}

		if (self != null)
		{
			evalContext.put(
				new LexNameToken(location.getModule(), "self", location), self);
		}

		evalContext.putAll(args);

		if (paramPatternList.size() == 1)
		{
			if (precondition != null && Settings.prechecks)
			{
				// Evaluate pre/post in evalContext as it includes the type
				// variables, if any. We disable the swapping and time (RT)
				// as precondition checks should be "free".

				evalContext.threadState.setAtomic(true);
				evalContext.setPrepost(4055, "Precondition failure: ");
				precondition.eval(from, argValues, evalContext);
				evalContext.setPrepost(0, null);
				evalContext.threadState.setAtomic(false);
			}

			Long tid = Thread.currentThread().getId();

			if (isMeasure)
			{
				if (measuringThreads.contains(tid))		// We are measuring on this thread
				{
    				if (!callingThreads.add(tid))		// And we've been here already
    				{
    					abort(4148, "Measure function is called recursively: " + name, evalContext);
    				}
				}
			}

			if (measureName != null)
			{
				if (measure == null)
				{
					measure = evalContext.lookup(measureName).functionValue(ctxt);

					if (typeValues != null)		// Function is polymorphic, so measure copies type args
					{
						measure = (FunctionValue)measure.clone();
						measure.uninstantiated = false;
						measure.typeValues = typeValues;
					}

					measure.measuringThreads = Collections.synchronizedSet(new HashSet<Long>());
					measure.callingThreads = Collections.synchronizedSet(new HashSet<Long>());
					measure.isMeasure = true;
				}

				// If this is a curried function, then the measure is called with all of the
				// previously applied argument values, in addition to the argValues.
				
				ValueList measureArgs = null;
				
				if (curriedArgs == null)
				{
					measureArgs = argValues;
				}
				else
				{
					measureArgs = new ValueList();
					measureArgs.addAll(curriedArgs);	// Previous args
					measureArgs.addAll(argValues);		// Final args
				}
				
				// We disable the swapping and time (RT) as measure checks should be "free".

				measure.measuringThreads.add(tid);
				evalContext.threadState.setAtomic(true);
				Value mv = measure.eval(measure.location, measureArgs, evalContext);
				evalContext.threadState.setAtomic(false);
				measure.measuringThreads.remove(tid);

				Stack<Value> stack = measureValues.get(tid);

				if (stack == null)
				{
					stack = new Stack<Value>();
					measureValues.put(tid, stack);
				}

				if (!stack.isEmpty())
				{
					Value old = stack.peek();		// Previous value

    				if (old != null && mv.compareTo(old) >= 0)		// Not decreasing order
    				{
    					abort(4146, "Measure failure: " +
    						name + Utils.listToString("(", argValues, ", ", ")") + ", measure " +
    						measure.name + ", current " + mv + ", previous " + old, evalContext);
    				}
				}

				stack.push(mv);
			}	
			
			Value rv = null;
			try
			{
				rv = body.apply(VdmRuntime.getExpressionEvaluator(), evalContext).convertTo(type.getResult(), evalContext);
			} catch (AnalysisException e)
			{
				if(e instanceof ValueException)
				{
					throw (ValueException) e;
				}
				e.printStackTrace();
			}
    		
    		if (ctxt.prepost > 0)	// Note, caller's context is checked
    		{
    			if (!rv.boolValue(ctxt))
    			{
    				// Note that this calls getLocation to find out where the body
    				// wants to report its location for this error - this may be an
    				// errs clause in some circumstances.

    				throw new ContextException(ctxt.prepost,
    					ctxt.prepostMsg + name, body.getLocation(), evalContext);
    			}
    		}

			if (postcondition != null && Settings.postchecks)
			{
				ValueList postArgs = new ValueList(argValues);
				postArgs.add(rv);

				// Evaluate pre/post in evalContext as it includes the type
				// variables, if any. We disable the swapping and time (RT)
				// as postcondition checks should be "free".

				evalContext.threadState.setAtomic(true);
				evalContext.setPrepost(4056, "Postcondition failure: ");
				postcondition.eval(from, postArgs, evalContext);
				evalContext.setPrepost(0, null);
				evalContext.threadState.setAtomic(false);
			}

			if (measure != null)
			{
				measureValues.get(tid).pop();
			}

			if (isMeasure)
			{
				callingThreads.remove(tid);
			}

			return rv;
		}
		else	// This is a curried function
		{
			if (type.getResult() instanceof AFunctionType)
			{
				// If a curried function has a pre/postcondition, then the
				// result of a partial application has a pre/post condition
				// with its free variables taken from the environment (so
				// that parameters passed are fixed in subsequent applies).

				FunctionValue newpre = null;

				if (precondition != null)
				{
					newpre = precondition.curry(evalContext);
				}

				FunctionValue newpost = null;

				if (postcondition != null)
				{
					newpost = postcondition.curry(evalContext);
				}

				if (freeVariables != null)
				{
					evalContext.putAll(freeVariables);
				}
				
				
				// Curried arguments are collected so that we can invoke any measure functions
				// once we reach the final apply that does not return a function.
				
				ValueList argList = new ValueList();
				
				if (curriedArgs != null)
				{
					argList.addAll(curriedArgs);
				}
				
				argList.addAll(argValues);

    			FunctionValue rv = new FunctionValue(location, "curried",
    				(AFunctionType)type.getResult(),
    				paramPatternList.subList(1, paramPatternList.size()),
    				body, newpre, newpost, evalContext, false, argList,
    				measureName, measureValues);

    			rv.setSelf(self);
    			rv.typeValues = typeValues;
    			
        		return rv;
			}

			VdmRuntimeError.abort(type.getLocation(),4057, "Curried function return type is not a function", ctxt);
			return null;
		}
	}

	private RootContext newContext(ILexLocation from, String title, Context ctxt, Context sctxt)
	{
		RootContext evalContext;

		if (self != null)
		{
			evalContext = new ObjectContext(
				from, title, freeVariables, ctxt, self);
		}
		else if (classdef != null)
		{
			evalContext = new ClassContext(
				from, title, freeVariables, ctxt, classdef);
		}
		else
		{
			evalContext = new StateContext(
				from, title, freeVariables, ctxt, sctxt);
		}

		return evalContext;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

			if (val instanceof CompFunctionValue || val instanceof IterFunctionValue)
			{
				return false;	// Play safe - we can't really tell
			}
			else if (val instanceof FunctionValue)
    		{
    			FunctionValue ov = (FunctionValue)val;
    			return ov.type.equals(type) &&		// Param and result types same
    				   ov.body.equals(body);		// Not ideal - a string comparison in fact
    		}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return type.hashCode() + body.hashCode();
	}

	@Override
	public String kind()
	{
		return "function";
	}

	@Override
	public FunctionValue functionValue(Context ctxt)
	{
		return this;
	}

	@Override
	public Value convertValueTo(PType to, Context ctxt) throws ValueException
	{
		if (PTypeAssistantTC.isType(to,AFunctionType.class))
		{
			return this;
		}
		else
		{
			return super.convertValueTo(to, ctxt);
		}
	}

	private FunctionValue curry(Context newFreeVariables)
	{
		// Remove first set of parameters, and set the free variables instead.
		// And adjust the return type to be the result type (a function).

		return new FunctionValue(location, name, (AFunctionType)type.getResult(),
			paramPatternList.subList(1, paramPatternList.size()),
			body, precondition, postcondition, newFreeVariables, false, null, null, null);
	}

	@Override
	public Object clone()
	{
		FunctionValue copy = new FunctionValue(location, name, type,
			paramPatternList, body, precondition, postcondition,
			freeVariables, checkInvariants, curriedArgs,
			measureName, measureValues);

		copy.typeValues = typeValues;
		return copy;
	}

	public String toTitle()
	{
		List<PPattern> paramPatterns = paramPatternList.get(0);
		return name + Utils.listToString("(", paramPatterns, ", ", ")");
	}
}
