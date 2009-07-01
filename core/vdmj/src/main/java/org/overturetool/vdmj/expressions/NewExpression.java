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
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.syntax.SystemDefinition;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class NewExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final LexIdentifierToken classname;
	public final ExpressionList args;

	private ClassDefinition classdef;
	private Definition ctorDefinition = null;

	public NewExpression(LexLocation location,
		LexIdentifierToken classname, ExpressionList args)
	{
		super(location);
		this.classname = classname;
		this.args = args;
	}

	@Override
	public String toString()
	{
		return "new " + classname + "("+ Utils.listToString(args) + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Definition cdef = env.findType(classname.getClassName());

		if (cdef == null || !(cdef instanceof ClassDefinition))
		{
			report(3133, "Class name " + classname + " not in scope");
			return new UnknownType(location);
		}

		classdef = (ClassDefinition)cdef;

		if (classdef instanceof SystemDefinition)
		{
			report(3279, "Cannot instantiate system class " + classdef.name);
		}

		TypeList argtypes = new TypeList();

		for (Expression a: args)
		{
			argtypes.add(a.typeCheck(env, null, scope));
		}

		Definition opdef = classdef.findConstructor(argtypes);

		if (opdef == null)
		{
			if (!args.isEmpty())	// Not having a default ctor is OK
    		{
    			report(3134, "Class has no constructor with these parameter types");
    			detail("Called", classdef.getCtorName(argtypes));
    		}
		}
		else
		{
			if (!opdef.isCallableOperation())
    		{
    			report(3135, "Class has no constructor with these parameter types");
    			detail("Called", classdef.getCtorName(argtypes));
    		}
			else if (opdef.accessSpecifier.access == Token.PRIVATE) //!ClassDefinition.isAccessible(env, opdef, false))
			{
    			report(3292, "Constructor is not accessible");
    			detail("Called", classdef.getCtorName(argtypes));
			}
			else
			{
				ctorDefinition = opdef;
			}
		}

		return classdef.getType();
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		ValueList argvals = new ValueList();

     		for (Expression arg: args)
    		{
    			argvals.add(arg.eval(ctxt));
    		}

    		if (classdef.invlistener != null)
    		{
    			// Suppress during construction
    			classdef.invlistener.doInvariantChecks = false;
    		}

			ObjectValue objval =
				classdef.newInstance(ctorDefinition, argvals, ctxt);

    		if (classdef.invlistener != null)
    		{
    			// Check the initial values of the object's fields
    			classdef.invlistener.doInvariantChecks = true;
    			classdef.invlistener.changedValue(location, objval, ctxt);
    		}

    		return objval;
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

		return args.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return args.getProofObligations(ctxt);
	}

	@Override
	public String kind()
	{
		return "new";
	}
}
