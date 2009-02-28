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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class CallStatement extends Statement
{
	public final LexNameToken name;
	public final ExpressionList args;

	public CallStatement(LexNameToken name, ExpressionList args)
	{
		super(name.location);
		this.name = name;
		this.args = args;
	}

	@Override
	public String toString()
	{
		return "call " + name + "(" + Utils.listToString(args) + ")";
	}

	@Override
	public String kind()
	{
		return "call";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		TypeList atypes = getArgTypes(env, scope);

		if (env.isVDMPP())
		{
			name.setTypeQualifier(atypes);
		}

		Definition opdef = env.findName(name, scope);

		if (opdef == null)
		{
			report(3213, "Operation " + name + " is not in scope");
			env.listAlternatives(name);
			return new UnknownType(location);
		}

		if (!opdef.isStatic() && env.isStatic())
		{
			report(3214, "Cannot call " + name + " from static context");
			return new UnknownType(location);
		}

		Type type = opdef.getType();

		if (type.isOperation())
		{
    		OperationType optype = type.getOperation();
    		optype.typeResolve(env, null);

    		// Reset the name's qualifier with the actual operation type so
    		// that runtime search has a simple TypeComparator call.

    		if (env.isVDMPP())
    		{
    			name.setTypeQualifier(optype.parameters);
    		}

    		checkArgTypes(optype.parameters, atypes);
    		return optype.result;
		}
		else if (type.isFunction())
		{
			// This is the case where a function is called as an operation without
			// a "return" statement.

    		FunctionType ftype = type.getFunction();
    		ftype.typeResolve(env, null);

    		// Reset the name's qualifier with the actual function type so
    		// that runtime search has a simple TypeComparator call.

    		if (env.isVDMPP())
    		{
    			name.setTypeQualifier(ftype.parameters);
    		}

    		checkArgTypes(ftype.parameters, atypes);
    		return ftype.result;
		}
		else
		{
			report(3210, "Name is neither a function nor an operation");
			return new UnknownType(location);
		}
	}

	@Override
	public TypeSet exitCheck()
	{
		// TODO We don't know what an operation call will raise
		return new TypeSet(new UnknownType(location));
	}

	private TypeList getArgTypes(Environment env, NameScope scope)
	{
		TypeList types = new TypeList();

		for (Expression a: args)
		{
			types.add(a.typeCheck(env, null, scope));
		}

		return types;
	}

	private void checkArgTypes(TypeList ptypes, TypeList atypes)
	{
		if (ptypes.size() != atypes.size())
		{
			report(3216, "Expecting " + ptypes.size() + " arguments");
		}
		else
		{
			int i=0;

			for (Type atype: atypes)
			{
				Type ptype = ptypes.get(i++);

				if (!TypeComparator.compatible(ptype, atype))
				{
					atype.report(3217, "Unexpected type for argument " + i);
					detail2("Expected", ptype, "Actual", atype);
				}
			}
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			Value v = ctxt.lookup(name).deref();

			if (v instanceof OperationValue)
			{
    			OperationValue op = v.operationValue(ctxt);
    			ValueList argValues = new ValueList();

    			for (Expression arg: args)
    			{
    				argValues.add(arg.eval(ctxt));
    			}

    			return op.eval(argValues, ctxt);
			}
			else
			{
    			FunctionValue fn = v.functionValue(ctxt);
    			ValueList argValues = new ValueList();

    			for (Expression arg: args)
    			{
    				argValues.add(arg.eval(ctxt));
    			}

    			return fn.eval(location, argValues, ctxt);
			}
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (Expression exp: args)
		{
			obligations.addAll(exp.getProofObligations(ctxt));
		}

		return obligations;
	}
}
