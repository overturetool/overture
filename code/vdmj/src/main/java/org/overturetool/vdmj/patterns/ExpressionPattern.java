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

package org.overturetool.vdmj.patterns;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;

public class ExpressionPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final Expression exp;

	public ExpressionPattern(Expression exp)
	{
		super(exp.location);
		this.exp = exp;
	}

	@Override
	public String toString()
	{
		return "(" + exp.toString() + ")";
	}

	@Override
	public void typeResolve(Environment env)
	{
		if (resolved) return; else { resolved = true; }

		try
		{
			exp.typeCheck(env, null, NameScope.NAMESANDSTATE);
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		return new DefinitionList();	// Exp has no bindings
	}

	@Override
	public NameValuePairList getNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		NameValuePairList result = new NameValuePairList();

		if (!expval.equals(exp.eval(ctxt)))
		{
			patternFail(4110, "Expression pattern match failed");
		}

		return result;		// NB no values for a match, as there's no definition
	}

	@Override
	public Type getPossibleType()
	{
		return new UnknownType(location);
	}

	@Override
	public Expression getMatchingExpression()
	{
		return exp;
	}
}
