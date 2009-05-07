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

import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.Value;

public class ObjectIdentifierDesignator extends ObjectDesignator
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken name;
	public final VariableExpression expression;

	public ObjectIdentifierDesignator(LexNameToken name)
	{
		super(name.location);
		this.name = name;
		this.expression = new VariableExpression(name.getExplicit(true));
	}

	@Override
	public String toString()
	{
		return name.toString();
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers)
	{
		return expression.typeCheck(env, qualifiers, NameScope.NAMESANDSTATE);
	}

	@Override
	public Value eval(Context ctxt)
	{
		return expression.eval(ctxt);
	}
}
