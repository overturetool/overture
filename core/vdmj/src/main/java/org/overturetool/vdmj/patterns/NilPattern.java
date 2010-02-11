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
import org.overturetool.vdmj.expressions.NilExpression;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NilValue;
import org.overturetool.vdmj.values.Value;

public class NilPattern extends Pattern
{
	private static final long serialVersionUID = 1L;

	public NilPattern(LexKeywordToken token)
	{
		super(token.location);
	}

	@Override
	public String toString()
	{
		return "nil";
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		return new DefinitionList();
	}

	@Override
	public NameValuePairList getNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		NameValuePairList result = new NameValuePairList();

		if (!(expval.deref() instanceof NilValue))
		{
			patternFail(4106, "Nil pattern match failed");
		}

		return result;
	}

	@Override
	public Type getPossibleType()
	{
		return new OptionalType(location, new UnknownType(location));
	}

	@Override
	public Expression getMatchingExpression()
	{
		return new NilExpression(location);
	}
}
