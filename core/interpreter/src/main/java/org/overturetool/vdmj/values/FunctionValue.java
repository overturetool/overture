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

package org.overturetool.vdmj.values;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.overture.interpreter.ast.node.ExternalNodeInterpreter;

import org.overture.interpreter.ast.definitions.AClassClassDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AExplicitFunctionDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AImplicitFunctionDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.SClassDefinitionInterpreter;
import org.overture.interpreter.ast.expressions.PExpInterpreter;
import org.overture.interpreter.ast.patterns.AExpressionPatternInterpreter;
import org.overture.interpreter.ast.patterns.APatternListTypePairInterpreter;
import org.overture.interpreter.ast.patterns.PPatternInterpreter;
import org.overture.interpreter.ast.types.AFunctionTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overture.interpreter.definitions.assistant.AExplicitFunctionDefinitionAssistant;
import org.overture.interpreter.definitions.assistant.AImplicitFunctionDefinitionAssistant;
import org.overture.interpreter.types.assistant.PTypeAssistant;
import org.overture.interpreter.types.assistant.PTypeInterpreterList;
import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.runtime.ClassContext;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.util.Utils;


public class FunctionValue extends Value implements ExternalNodeInterpreter
{
	private static final long serialVersionUID = 1L;
	public final LexLocation location;
	public final String name;
	public NameValuePairList typeValues;
	public AFunctionTypeInterpreter type;
	public final List<List<PPatternInterpreter>> paramPatternList;  //TODO list of list?
	public final PExpInterpreter body; 
	public final FunctionValue precondition;
	public final FunctionValue postcondition;
	public final Context freeVariables;

	// Causes parameter assignments to check their invariants (if any).
	// This is set to false for inv_() functions, which cannot check them.
	private final boolean checkInvariants;

	// Measure function value, if any
	private LexNameToken measureName = null;
	private FunctionValue measure = null;
	private Map<Long, Stack<Value>> measureValues = null;
	private Set<Long> measuringThreads = null;
	private Set<Long> callingThreads = null;
	private boolean isMeasure = false;

	public ObjectValue self = null;
	public boolean isStatic = false;
	public boolean uninstantiated = false;
	private SClassDefinitionInterpreter classdef = null;

	public FunctionValue(LexLocation location, String name, AFunctionTypeInterpreter type,
		List<List<PPatternInterpreter>> paramPatternList, PExpInterpreter body,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables, boolean checkInvariants)
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
	}

	public FunctionValue(LexLocation location, String name, AFunctionTypeInterpreter type,
			List<PPatternInterpreter> paramPatterns, PExpInterpreter body, Context freeVariables)
	{
		this.location = location;
		this.name = name;
		this.typeValues = null;
		this.type = type;
		this.paramPatternList = new Vector<List<PPatternInterpreter>>();
		this.body = body;
		this.precondition = null;
		this.postcondition = null;
		this.freeVariables = freeVariables;
		this.checkInvariants = true;

		paramPatternList.add(paramPatterns);
	}

	public FunctionValue(AExplicitFunctionDefinitionInterpreter def,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables)
	{  
		this.location = def.getLocation();
		this.name = def.getName().name;
		this.typeValues = null;
		this.type = (AFunctionTypeInterpreter)def.getType();
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

	public FunctionValue(AImplicitFunctionDefinitionInterpreter def,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables)
	{
		this.location = def.getLocation();
		this.name = def.getName().name;
		this.typeValues = null;
		this.type = (AFunctionTypeInterpreter)def.getType();

		this.paramPatternList = new Vector<List<PPatternInterpreter>>();
		List<PPatternInterpreter> plist = new LinkedList<PPatternInterpreter>();

		for (APatternListTypePairInterpreter ptp: def.getParamPatterns())
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

	public FunctionValue(AImplicitFunctionDefinitionInterpreter fdef,
		PTypeInterpreterList actualTypes, FunctionValue precondition,
		FunctionValue postcondition, Context freeVariables)
	{
		this(fdef, precondition, postcondition, freeVariables);
		this.typeValues = new NameValuePairList();
		this.type =  AImplicitFunctionDefinitionAssistant.getType(fdef, actualTypes);

		Iterator<PTypeInterpreter> ti = actualTypes.iterator();

		for (LexNameToken pname: fdef.getTypeParams())
		{
			PTypeInterpreter ptype = ti.next();
			typeValues.add(new NameValuePair(pname, new ParameterValue(ptype)));
		}
	}

	public FunctionValue(AExplicitFunctionDefinitionInterpreter fdef,
		PTypeInterpreterList actualTypes, FunctionValue precondition,
		FunctionValue postcondition, Context freeVariables)
	{
		this(fdef, precondition, postcondition, freeVariables);
		this.typeValues = new NameValuePairList();
		this.type = AExplicitFunctionDefinitionAssistant.getType(fdef, actualTypes);

		Iterator<PTypeInterpreter> ti = actualTypes.iterator();

		for (LexNameToken pname: fdef.getTypeParams())
		{
			PTypeInterpreter ptype = ti.next();
			typeValues.add(new NameValuePair(pname, new ParameterValue(ptype)));
		}
	}

	// This constructor is used by IterFunctionValue and CompFunctionValue
	// The methods which matter are overridden in those classes.

	public FunctionValue(LexLocation location, AFunctionTypeInterpreter type, String name)
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
		LexLocation from, Value arg, Context ctxt) throws ValueException
	{
		ValueList args = new ValueList(arg);
		return eval(from, args, ctxt, null);
	}

	public Value eval(
		LexLocation from, ValueList argValues, Context ctxt) throws ValueException
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

	public void setClass(SClassDefinitionInterpreter classdef)
	{
		this.classdef = classdef;
	}

	public Value eval(
		LexLocation from, ValueList argValues, Context ctxt, Context sctxt) throws ValueException
	{
		if (body == null)
		{
			abort(4051, "Cannot apply implicit function: " + name, ctxt);
		}

		if (uninstantiated)
		{
			abort(3033, "Polymorphic function has not been instantiated: " + name, ctxt);
		}

		List<PPatternInterpreter> paramPatterns = paramPatternList.get(0);
		RootContext evalContext = newContext(from, toTitle(), ctxt, sctxt);

		if (typeValues != null)
		{
			// Add any @T type values, for recursive polymorphic functions
			evalContext.putList(typeValues);
		}

		if (argValues.size() != paramPatterns.size())
		{
			type.abort(4052, "Wrong number of arguments passed to " + name, ctxt);
		}

		Iterator<Value> valIter = argValues.iterator();
		Iterator<PTypeInterpreter> typeIter = type.getParameters().iterator();
		NameValuePairMap args = new NameValuePairMap();

		for (PPatternInterpreter p: paramPatterns)
		{
			Value pv = valIter.next();

			if (checkInvariants)	// Don't even convert invariant arg values
			{
				pv = pv.convertValueTo(typeIter.next(), ctxt);
			}

			try
			{
				for (NameValuePair nvp: p.getNamedValues(pv, ctxt))
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
				new LexNameToken(location.module, "self", location), self);
		}

		evalContext.putAll(args);

		if (paramPatternList.size() == 1)
		{
			if (precondition != null && Settings.prechecks)
			{
				// Evaluate pre/post in evalContext as it includes the type
				// variables, if any.

				evalContext.setPrepost(4055, "Precondition failure: ");
				precondition.eval(from, argValues, evalContext);
				evalContext.setPrepost(0, null);
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
						measure.type = new AFunctionTypeInterpreter(measure.location, false, measure.type.getPartial(), 
									type.getParameters(), measure.type.getResult());
						
						measure.typeValues = typeValues;
					}

					measure.measuringThreads = Collections.synchronizedSet(new HashSet<Long>());
					measure.callingThreads = Collections.synchronizedSet(new HashSet<Long>());
					measure.isMeasure = true;
				}

				measure.measuringThreads.add(tid);
				Value mv = measure.eval(measure.location, argValues, evalContext);
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

    		Value rv = body.eval(evalContext).convertValueTo(type.getResult(), evalContext);

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
				// variables, if any.

				evalContext.setPrepost(4056, "Postcondition failure: ");
				postcondition.eval(from, postArgs, evalContext);
				evalContext.setPrepost(0, null);
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
			if (type.getResult() instanceof AFunctionTypeInterpreter)
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

    			FunctionValue rv = new FunctionValue(location, "curried",
    				(AFunctionTypeInterpreter)type.getResult(),
    				paramPatternList.subList(1, paramPatternList.size()),
    				body, newpre, newpost, evalContext, false);

    			rv.setSelf(self);

        		return rv;
			}

			type.abort(4057, "Curried function return type is not a function", ctxt);
			return null;
		}
	}

	private RootContext newContext(LexLocation from, String title, Context ctxt, Context sctxt)
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
	public Value convertValueTo(PTypeInterpreter to, Context ctxt) throws ValueException
	{
		if (PTypeAssistant.isType(to, AFunctionTypeInterpreter.class))
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

		return new FunctionValue(location, name, (AFunctionTypeInterpreter)type.getResult(),
			paramPatternList.subList(1, paramPatternList.size()),
			body, precondition, postcondition, newFreeVariables, false);
	}

	@Override
	public Object clone()
	{
		FunctionValue copy = new FunctionValue(location, name, type,
			paramPatternList, body, precondition, postcondition,
			freeVariables, checkInvariants);

		copy.typeValues = typeValues;
		return copy;
	}

	public String toTitle()
	{
		List<PPatternInterpreter> paramPatterns = paramPatternList.get(0);
		return name + Utils.listToString("(", paramPatterns, ", ", ")");
	}
}
