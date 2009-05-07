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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.lex.LexLocation;
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
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;

public class IsOfBaseClassExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken baseclass;
	public final Expression exp;

	public IsOfBaseClassExpression(
		LexLocation start, LexNameToken classname, Expression exp)
	{
		super(start);

		this.baseclass = classname.getExplicit(false);
		this.exp = exp;
	}

	@Override
	public Value eval(Context ctxt)
	{
		try
		{
			Value v = exp.eval(ctxt).deref();

			if (!(v instanceof ObjectValue))
			{
				return new BooleanValue(false);
			}

			ObjectValue ov = v.objectValue(ctxt);
			return new BooleanValue(search(ov));
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	private boolean search(ObjectValue from)
	{
		if (from.type.name.name.equals(baseclass.name) &&
			from.superobjects.isEmpty())
		{
			return true;
		}

		for (ObjectValue svalue: from.superobjects)
		{
			if (search(svalue))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return exp.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return exp.getProofObligations(ctxt);
	}

	@Override
	public String kind()
	{
		return "isofbaseclass";
	}

	@Override
	public String toString()
	{
		return "isofbaseclass(" + baseclass + "," + exp + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		if (env.findType(baseclass) == null)
		{
			report(3114, "Undefined base class type: " + baseclass.name);
		}

		Type rt = exp.typeCheck(env, null, scope);

		if (!rt.isClass())
		{
			exp.report(3266, "Argument is not an object");
		}

		return new BooleanType(location);
	}
}
