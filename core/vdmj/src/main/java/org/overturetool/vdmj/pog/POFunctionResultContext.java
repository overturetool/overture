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

package org.overturetool.vdmj.pog;

import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.PatternTypePair;

public class POFunctionResultContext extends POContext
{
	public final LexNameToken name;
	public final FunctionType deftype;
	public final Expression precondition;
	public final Expression body;
	public final PatternTypePair result;
	public final boolean implicit;

	public POFunctionResultContext(
		ExplicitFunctionDefinition definition)
	{
		this.name = definition.name;
		this.deftype = definition.type;
		this.precondition = definition.precondition;
		this.body = definition.body;
		this.implicit = false;

		this.result = new PatternTypePair(
			new IdentifierPattern(
				new LexNameToken(
					definition.name.module, "RESULT", definition.location)),
					definition.type.result);
	}

	public POFunctionResultContext(
		ImplicitFunctionDefinition definition)
	{
		this.name = definition.name;
		this.deftype = definition.type;
		this.precondition = definition.precondition;
		this.body = definition.body;
		this.implicit = true;
		this.result = definition.result;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (precondition != null)
		{
			sb.append(precondition);
			sb.append(" => ");
		}

		if (implicit)
		{
			sb.append("forall ");
			sb.append(result);
			sb.append(" & ");
		}
		else
		{
			sb.append("let ");
			sb.append(result);
			sb.append(" = ");
			sb.append(body);
			sb.append(" in ");
		}

		return sb.toString();
	}
}
