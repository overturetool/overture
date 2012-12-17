/*******************************************************************************
 *
 *	Copyright (c) 2012 Fujitsu Services Ltd.
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
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class NarrowExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public Type basictype;
	public final LexNameToken typename;
	public final Expression test;

	private Definition typedef = null;
	private Type exptype = null;

	public NarrowExpression(LexLocation location, LexNameToken typename, Expression test)
	{
		super(location);
		this.basictype = null;
		this.typename = typename;
		this.test = test;
	}

	public NarrowExpression(LexLocation location, Type type, Expression test)
	{
		super(location);
		this.basictype = type;
		this.typename = null;
		this.test = test;
	}

	@Override
	public String toString()
	{
		return "narrow_(" + test + ", " + (typename == null ? basictype : typename) + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		exptype = test.typeCheck(env, null, scope);
		Type result = null;

		if (basictype != null)
		{
			basictype = basictype.typeResolve(env, null);
			result = basictype;
		}
		else
		{
			typedef = env.findType(typename, location.module);

			if (typedef == null)
			{
				report(3113, "Unknown type name '" + typename + "'");
			}
			
			result = typedef.getType();
		}
		
		if (!TypeComparator.compatible(result, exptype))
		{
			report(3317, "Expression can never match narrow type");
		}
		
		return result;
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
				if (typedef.isTypeDefinition())
				{
					// NB. we skip the DTC enabled check here
					v = v.convertValueTo(typedef.getType(), ctxt);
				}
    			else if (v.isType(RecordValue.class))
    			{
    				v = v.recordValue(ctxt);
    			}
    		}
    		else
    		{
    			// NB. we skip the DTC enabled check here
   				v = v.convertValueTo(basictype, ctxt);
    		}
		}
		catch (ValueException ex)
		{
			abort(ex);
		}
		
		return v;
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
		ProofObligationList obligations = new ProofObligationList();
		
		Type expected = (typedef == null ? basictype : typedef.getType());
		ctxt.noteType(test, expected);

		if (!TypeComparator.isSubType(exptype, expected))
		{
			obligations.add(new SubTypeObligation(test, expected, exptype, ctxt));
		}

		obligations.addAll(test.getProofObligations(ctxt));
		return obligations;
	}

	@Override
	public String kind()
	{
		return "narrow_";
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return test.getValues(ctxt);
	}

	@Override
	public LexNameList getOldNames()
	{
		return test.getOldNames();
	}
}
