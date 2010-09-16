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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.runtime.ClassContext;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.PatternListTypePair;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.util.Utils;


public class FunctionValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final LexLocation location;
	public final String name;
	public NameValuePairList typeValues;
	public FunctionType type;
	public final List<PatternList> paramPatternList;
	public final Expression body;
	public final FunctionValue precondition;
	public final FunctionValue postcondition;
	public final Context freeVariables;

	// Causes parameter assignments to check their invariants (if any).
	// This is set to false for inv_() functions, which cannot check them.
	private final boolean checkInvariants;

	// Measure function value, if any
	private FunctionValue measure = null;
	private Map<Long, Stack<Value>> measureValues = null;

	public ObjectValue self = null;
	public boolean isStatic = false;
	private ClassDefinition classdef = null;

	public FunctionValue(LexLocation location, String name, FunctionType type,
		List<PatternList> paramPatternList, Expression body,
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

	public FunctionValue(LexLocation location, String name, FunctionType type,
		PatternList paramPatterns, Expression body, Context freeVariables)
	{
		this.location = location;
		this.name = name;
		this.typeValues = null;
		this.type = type;
		this.paramPatternList = new Vector<PatternList>();
		this.body = body;
		this.precondition = null;
		this.postcondition = null;
		this.freeVariables = freeVariables;
		this.checkInvariants = true;

		paramPatternList.add(paramPatterns);
	}

	public FunctionValue(ExplicitFunctionDefinition def,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables)
	{
		this.location = def.location;
		this.name = def.name.name;
		this.typeValues = null;
		this.type = (FunctionType)def.getType();
		this.paramPatternList = def.paramPatternList;
		this.body = def.body;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.freeVariables = freeVariables;
		this.checkInvariants = !def.isTypeInvariant;
		this.classdef = def.classDefinition;

		if (Settings.measureChecks && def.measuredef != null)
		{
			measureValues = new HashMap<Long, Stack<Value>>();

			measure = new FunctionValue(
				(ExplicitFunctionDefinition)def.measuredef, null, null, freeVariables);
		}
	}

	public FunctionValue(ImplicitFunctionDefinition def,
		FunctionValue precondition, FunctionValue postcondition,
		Context freeVariables)
	{
		this.location = def.location;
		this.name = def.name.name;
		this.typeValues = null;
		this.type = (FunctionType)def.getType();

		this.paramPatternList = new Vector<PatternList>();
		PatternList plist = new PatternList();

		for (PatternListTypePair ptp: def.parameterPatterns)
		{
			plist.addAll(ptp.patterns);
		}

		this.paramPatternList.add(plist);

		this.body = def.body;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.freeVariables = freeVariables;
		this.checkInvariants = true;
		this.classdef = def.classDefinition;

		if (Settings.measureChecks && def.measuredef != null)
		{
			measureValues = new HashMap<Long, Stack<Value>>();

			measure = new FunctionValue(
				(ExplicitFunctionDefinition)def.measuredef, null, null, freeVariables);
		}
	}

	public FunctionValue(ImplicitFunctionDefinition fdef,
		TypeList actualTypes, FunctionValue precondition,
		FunctionValue postcondition, Context freeVariables)
	{
		this(fdef, precondition, postcondition, freeVariables);
		this.typeValues = new NameValuePairList();
		this.type = fdef.getType(actualTypes);

		Iterator<Type> ti = actualTypes.iterator();

		for (LexNameToken pname: fdef.typeParams)
		{
			Type ptype = ti.next();
			typeValues.add(new NameValuePair(pname, new ParameterValue(ptype)));
		}
	}

	public FunctionValue(ExplicitFunctionDefinition fdef,
		TypeList actualTypes, FunctionValue precondition,
		FunctionValue postcondition, Context freeVariables)
	{
		this(fdef, precondition, postcondition, freeVariables);
		this.typeValues = new NameValuePairList();
		this.type = fdef.getType(actualTypes);

		Iterator<Type> ti = actualTypes.iterator();

		for (LexNameToken pname: fdef.typeParams)
		{
			Type ptype = ti.next();
			typeValues.add(new NameValuePair(pname, new ParameterValue(ptype)));
		}
	}

	// This constructor is used by IterFunctionValue and CompFunctionValue
	// The methods which matter are overridden in those classes.

	public FunctionValue(LexLocation location, String name)
	{
		this.location = location;
		this.name = name;
		this.typeValues = null;
		this.type = null;
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
		}
	}

	public void setClass(ClassDefinition classdef)
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

		PatternList paramPatterns = paramPatternList.get(0);
		RootContext evalContext = null;

		if (self != null)
		{
			evalContext = new ObjectContext(
				from, toTitle(), freeVariables, ctxt, self);
		}
		else if (classdef != null)
		{
			evalContext = new ClassContext(
				from, toTitle(), freeVariables, ctxt, classdef);
		}
		else
		{
			evalContext = new StateContext(
				from, toTitle(), freeVariables, ctxt, sctxt);
		}

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
		Iterator<Type> typeIter = type.parameters.iterator();
		NameValuePairMap args = new NameValuePairMap();

		for (Pattern p: paramPatterns)
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

				if (!precondition.eval(precondition.location, argValues, evalContext).boolValue(ctxt))
				{
					abort(4055,
						"Precondition failure: " + precondition.name, evalContext);
				}
			}

			if (measure != null)
			{
				Value mv = measure.eval(measure.location, argValues, evalContext);
				Stack<Value> stack = measureValues.get(Thread.currentThread().getId());

				if (stack == null)
				{
					stack = new Stack<Value>();
					measureValues.put(Thread.currentThread().getId(), stack);
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

    		Value rv = body.eval(evalContext).convertValueTo(type.result, evalContext);

			if (postcondition != null && Settings.postchecks)
			{
				ValueList postArgs = new ValueList(argValues);
				postArgs.add(rv);

				// Evaluate pre/post in evalContext as it includes the type
				// variables, if any.

				if (!postcondition.eval(postcondition.location, postArgs, evalContext).boolValue(ctxt))
    			{
					abort(4056,
						"Postcondition failure: " + postcondition.name, evalContext);
    			}
			}

			if (measure != null)
			{
				measureValues.get(Thread.currentThread().getId()).pop();
			}

			return rv;
		}
		else	// This is a curried function
		{
			if (type.result instanceof FunctionType)
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

    			Value rv = new FunctionValue(location, "curried",
    				(FunctionType)type.result,
    				paramPatternList.subList(1, paramPatternList.size()),
    				body, newpre, newpost, evalContext, false);

        		return rv;
			}

			type.abort(4057, "Curried function return type is not a function", ctxt);
			return null;
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof FunctionValue)
    		{
    			FunctionValue ov = (FunctionValue)val;
    			return ov.type.equals(type);
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
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to.isType(FunctionType.class))
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

		return new FunctionValue(location, name, (FunctionType)type.result,
			paramPatternList.subList(1, paramPatternList.size()),
			body, precondition, postcondition, newFreeVariables, false);
	}

	@Override
	public Object clone()
	{
		return new FunctionValue(location, name, type,
			paramPatternList, body, precondition, postcondition,
			freeVariables, checkInvariants);
	}

	public String toTitle()
	{
		PatternList paramPatterns = paramPatternList.get(0);
		return name + Utils.listToString("(", paramPatterns, ", ", ")");
	}
}
