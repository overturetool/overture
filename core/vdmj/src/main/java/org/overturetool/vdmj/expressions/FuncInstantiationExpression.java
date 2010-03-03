/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.InheritedDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.ParameterType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.ParameterValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class FuncInstantiationExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression function;
	public TypeList actualTypes;
	public FunctionType type;

	private ExplicitFunctionDefinition expdef = null;
	private ImplicitFunctionDefinition impdef = null;

	public FuncInstantiationExpression(Expression function, TypeList types)
	{
		super(function);
		this.function = function;
		this.actualTypes = types;
	}

	@Override
	public String toString()
	{
		return "(" + function + ")[" + Utils.listToString(actualTypes) + "]";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		// If there are no type qualifiers passed because the poly function value
		// is being accessed alone (not applied). In this case, the null qualifier
		// will cause VariableExpression to search for anything that matches the
		// name alone. If there is precisely one, it is selected; if there are
		// several, this is an ambiguity error.
		//
		// Note that a poly function is hard to identify from the actual types
		// passed here because the number of parameters may not equal the number
		// of type parameters.

		Type ftype = function.typeCheck(env, qualifiers, scope);

		if (ftype.isUnknown())
		{
			return ftype;
		}

		if (ftype.isFunction())
		{
			FunctionType t = ftype.getFunction();
			TypeSet set = new TypeSet();

			if (t.definitions == null)
			{
				report(3098, "Function value is not polymorphic");
				set.add(new UnknownType(location));
			}
			else
			{
    			boolean serious = (t.definitions.size() == 1);

    			for (Definition def: t.definitions)		// Possibly a union of several
    			{
    				LexNameList typeParams = null;

    				while (def instanceof InheritedDefinition)
    				{
    					def = ((InheritedDefinition)def).superdef;
    				}

    				if (def instanceof ExplicitFunctionDefinition)
    				{
    					expdef = (ExplicitFunctionDefinition)def;
    					typeParams = expdef.typeParams;
    				}
    				else if (def instanceof ImplicitFunctionDefinition)
    				{
    					impdef = (ImplicitFunctionDefinition)def;
    					typeParams = impdef.typeParams;
    				}
    				else
    				{
    					report(3099, "Polymorphic function is not in scope");
    				}

    				if (typeParams == null)
    				{
    					concern(serious, 3100, "Function has no type parameters");
    					continue;
    				}

    				if (actualTypes.size() != typeParams.size())
    				{
    					concern(serious, 3101, "Expecting " + typeParams.size() + " type parameters");
    					continue;
    				}

    				TypeList fixed = new TypeList();

    				for (Type ptype: actualTypes)
    				{
    					if (ptype instanceof ParameterType)		// Recursive polymorphism
    					{
    						ParameterType pt = (ParameterType)ptype;
    						Definition d = env.findName(pt.name, scope);

    						if (d == null)
    						{
    							concern(serious, 3102, "Parameter name " + pt + " not defined");
    						}

    						ptype = d.getType();
    					}

    					fixed.add(ptype.typeResolve(env, null));
    				}

    				actualTypes = fixed;

    				type = expdef == null ?
    					impdef.getType(actualTypes) : expdef.getType(actualTypes);

    				set.add(type);
    			}
			}

			if (!set.isEmpty())
			{
				return set.getType(location);
			}
		}
		else
		{
			report(3103, "Function instantiation does not yield a function");
		}

		return new UnknownType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		FunctionValue fv = function.eval(ctxt).functionValue(ctxt);
    		TypeList fixed = new TypeList();

    		for (Type ptype: actualTypes)
    		{
    			if (ptype instanceof ParameterType)
    			{
    				ParameterType pname = (ParameterType)ptype;
    				Value t = ctxt.lookup(pname.name);

    				if (t == null)
    				{
    					abort(4008, "No such type parameter @" + pname + " in scope", ctxt);
    				}
    				else if (t instanceof ParameterValue)
    				{
    					ParameterValue tv = (ParameterValue)t;
    					fixed.add(tv.type);
    				}
    				else
    				{
    					abort(4009, "Type parameter/local variable name clash, @" + pname, ctxt);
    				}
    			}
    			else
    			{
    				fixed.add(ptype);
    			}
    		}

    		if (expdef == null)
			{
    			FunctionValue prefv = null;
    			FunctionValue postfv = null;

    			if (impdef.predef != null)
    			{
    				prefv = impdef.predef.getPolymorphicValue(fixed);
    			}
    			else
    			{
    				prefv = null;
    			}

    			if (impdef.postdef != null)
    			{
    				postfv = impdef.postdef.getPolymorphicValue(fixed);
    			}
    			else
    			{
    				postfv = null;
    			}

				FunctionValue rv = new FunctionValue(
					impdef, fixed, prefv, postfv, null);

				rv.setSelf(fv.self);
				return rv;
			}
			else
			{
    			FunctionValue prefv = null;
    			FunctionValue postfv = null;

    			if (expdef.predef != null)
    			{
    				prefv = expdef.predef.getPolymorphicValue(fixed);
    			}
    			else
    			{
    				prefv = null;
    			}

    			if (expdef.postdef != null)
    			{
    				postfv = expdef.postdef.getPolymorphicValue(fixed);
    			}
    			else
    			{
    				postfv = null;
    			}

				FunctionValue rv =  new FunctionValue(
					expdef, fixed, prefv, postfv, null);

				rv.setSelf(fv.self);
				return rv;
			}
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return function.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return function.getProofObligations(ctxt);
	}

	@Override
	public String getPreName()
	{
		return function.getPreName();
	}

	@Override
	public String kind()
	{
		return "instantiation";
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return function.getValues(ctxt);
	}
}
