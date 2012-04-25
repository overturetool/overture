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

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.NaturalValue;
import org.overturetool.vdmj.values.Value;

public class CardinalityExpression extends UnaryExpression
{
	private static final long serialVersionUID = 1L;

	public CardinalityExpression(LexLocation location, Expression exp)
	{
		super(location, exp);
	}

	@Override
	public String toString()
	{
		return "(card " + exp + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		if (!exp.typeCheck(env, null, scope).isSet())
		{
			exp.report(3067, "Argument of 'card' is not a set");
		}

		return new NaturalType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			return new NaturalValue(exp.eval(ctxt).setValue(ctxt).size());
		}
		catch (ValueException e)
		{
			return abort(e);
		}
        catch (ContextException e)
        {
        	throw e;	// To avoid case below
        }
		catch (Exception e)
		{
			return abort(4065, e.getMessage(), ctxt);
		}
	}

	@Override
	public String kind()
	{
		return "card";
	}
}
