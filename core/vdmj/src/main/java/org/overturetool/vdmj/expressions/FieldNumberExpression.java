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

import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.TupleSelectObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class FieldNumberExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression tuple;
	public final LexIntegerToken field;
	private Type type = null;

	public FieldNumberExpression(Expression tuple, LexIntegerToken field)
	{
		super(tuple);
		this.tuple = tuple;
		this.field = field;
		this.field.location.executable(true);
	}

	@Override
	public String toString()
	{
		return "(" + tuple + "." + field + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		type = tuple.typeCheck(env, null, scope);

		if (!type.isProduct())
		{
			tuple.report(3094, "Field '#" + field + "' applied to non-tuple type");
			return new UnknownType(location);
		}

		ProductType product = type.getProduct();
		long fn = field.value;

		if (fn > product.types.size() || fn < 1)
		{
			field.report(3095, "Field number does not match tuple size");
			return new UnknownType(location);
		}

		return product.types.get((int)fn - 1);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		this.field.location.hit();

		try
		{
    		ValueList fields = tuple.eval(ctxt).tupleValue(ctxt);
    		Value r = fields.get((int)field.value - 1);

    		if (r == null)
    		{
    			field.abort(4007, "No such field in tuple: #" + field, ctxt);
    		}

    		return r;
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

		return tuple.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = tuple.getProofObligations(ctxt);

		if (type instanceof UnionType)
		{
			UnionType union = (UnionType)type;

			for (Type t: union.types)
			{
				if (t.isProduct())
				{
					ProductType pt = t.getProduct();

					if (pt.types.size() < field.value)
					{
						list.add(new TupleSelectObligation(this, pt, ctxt));
					}
				}
			}
		}

		return list;
	}

	@Override
	public String kind()
	{
		return "field #";
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return tuple.getValues(ctxt);
	}
}
