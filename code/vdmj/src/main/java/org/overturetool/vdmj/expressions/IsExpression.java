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
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;

public class IsExpression extends Expression
{
	public Type basictype;
	public final LexNameToken typename;
	public final Expression test;

	private Definition typedef = null;

	public IsExpression(LexNameToken typename, Expression test)
	{
		super(typename.location);
		this.basictype = null;
		this.typename = typename;
		this.test = test;
	}

	public IsExpression(Type type, Expression test)
	{
		super(type.location);
		this.basictype = type;
		this.typename = null;
		this.test = test;
	}

	@Override
	public String toString()
	{
		return "is_" + (typename == null ? basictype : typename) + "(" + test + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		if (test != null)
		{
			test.typeCheck(env, null, scope);
		}

		if (basictype != null)
		{
			basictype = basictype.typeResolve(env, null);
		}

		if (typename != null)
		{
			typedef = env.findType(typename);

			if (typedef == null)
			{
				report(3113, "Unknown type name '" + typename + "'");
			}
		}

		return new BooleanType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value v = test.eval(ctxt);

		try
		{
    		if (typename != null)
    		{
    			if (typedef != null)
    			{
    				if (typedef.isTypeDefinition())
    				{
    					// NB. we skip the DTC enabled check here
    					v.convertValueTo(typedef.getType(), ctxt);
    					return new BooleanValue(true);
    				}
    			}
    			else if (v.isType(RecordValue.class))
    			{
    				RecordValue rv = v.recordValue(ctxt);
    				return new BooleanValue(rv.type.name.equals(typename));
    			}
    		}
    		else
    		{
    			// NB. we skip the DTC enabled check here
   				v.convertValueTo(basictype, ctxt);
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
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return test.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		if (test != null)
		{
			return test.getProofObligations(ctxt);
		}
		else
		{
			return new ProofObligationList();
		}
	}

	@Override
	public String kind()
	{
		return "is_";
	}
}
