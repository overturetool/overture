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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.Value;

public class VariableExpression extends Expression
{
	public LexNameToken name;

	private Definition vardef = null;
	private String original = null;

	public VariableExpression(LexNameToken name)
	{
		super(name.location);
		this.name = name;
		this.original = name.getName();
	}

	public VariableExpression(LexNameToken name, boolean explicit)
	{
		this(name);
		setExplicit(explicit);
	}

	@Override
	public String toString()
	{
		return original;
	}

	public void setExplicit(boolean explicit)
	{
		name = name.getExplicit(explicit);
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		if (env.isVDMPP())
		{
			name.setTypeQualifier(qualifiers);
    		vardef = env.findName(name, scope);

    		if (vardef != null)
    		{
    			if (vardef.classDefinition != null)
    			{
        			if (!ClassDefinition.isAccessible(env, vardef, true))
        			{
        				report(3180, "Inaccessible member " + name + " of class " +
        					vardef.classDefinition.name.name);
        				return new UnknownType(location);
        			}
    			}
    		}
    		else if (qualifiers != null)
    		{
    			// It may be an apply of a map or sequence, which would not
    			// have the type qualifier of its arguments in the name. Or
    			// it might be an apply of a function via a function variable
    			// which would not be qualified.

    			name.setTypeQualifier(null);
    			vardef = env.findName(name, scope);

    			if (vardef == null)
    			{
    				name.setTypeQualifier(qualifiers);	// Just for error text!
    			}
    		}
    		else
    		{
    			// We may be looking for a bare function/op "x", when in fact
    			// there is one with a qualified name "x(args)". So we check
    			// the possible matches - if there is precisely one, we pick it,
    			// else we raise an ambiguity error.

				for (Definition possible: env.findMatches(name))
				{
					if (possible.isFunctionOrOperation())
					{
						if (vardef != null)
						{
							report(3269, "Ambiguous function/operation name: " + name.name);
							env.listAlternatives(name);
							break;
						}

						vardef = possible;

						// Set the qualifier so that it will find it at runtime.

						Type pt = possible.getType();

						if (pt instanceof FunctionType)
						{
							FunctionType ft = (FunctionType)pt;
							name.setTypeQualifier(ft.parameters);
						}
						else
						{
							OperationType ot = (OperationType)pt;
							name.setTypeQualifier(ot.parameters);
						}
					}
				}
    		}

    		if (vardef != null &&
    			vardef.classDefinition != null &&		// It's a member of a class
    			!vardef.isStatic() && env.isStatic())
    		{
    			report(3181, "Cannot access " + name + " from a static context");
    			return new UnknownType(location);
    		}
    	}
    	else
    	{
    		vardef = env.findName(name, scope);
    	}

		if (vardef == null)
		{
			report(3182, "Name '" + name + "' is not in scope");
			env.listAlternatives(name);
			return new UnknownType(location);
		}
		else
		{
			// Note that we perform an extra typeResolve here. This is
			// how forward referenced types are resolved, and is the reason
			// we don't need to retry at the top level (assuming all names
			// are in the environment).

			return vardef.getType().typeResolve(env, null);
		}
	}

	@Override
	public String getPreName()
	{
		if (vardef instanceof ExplicitFunctionDefinition)
		{
			ExplicitFunctionDefinition ex = (ExplicitFunctionDefinition)vardef;

			if (ex.predef == null)
			{
				return "";		// A function without a precondition
			}

			return ex.predef.name.name;
		}
		else if (vardef instanceof ImplicitFunctionDefinition)
		{
			ImplicitFunctionDefinition im = (ImplicitFunctionDefinition)vardef;

			if (im.predef == null)
			{
				return "";		// A function without a precondition
			}

			return im.predef.name.name;
		}

		return null;	// Not a function.
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		return ctxt.lookup(name);
	}

	@Override
	public String kind()
	{
		return "variable";
	}
}
