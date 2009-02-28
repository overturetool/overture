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

package org.overturetool.vdmj.definitions;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;

/**
 * A VDM class invariant definition.
 */

public class ClassInvariantDefinition extends Definition
{
	public final Expression expression;

	public ClassInvariantDefinition(LexNameToken name, Expression expression)
	{
		super(Pass.DEFS, name.location, name, NameScope.GLOBAL);
		this.expression = expression;
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		return null;		// We can never find inv_C().
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList();
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList();
	}

	@Override
	public Type getType()
	{
		return new BooleanType(location);
	}

	@Override
	public String toString()
	{
		return "inv " + expression;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		Type type = expression.typeCheck(base, null, NameScope.NAMESANDSTATE);

		if (!type.isType(BooleanType.class))
		{
			report(3013, "Class invariant is not a boolean expression");
		}
	}

	@Override
	public String kind()
	{
		return "invariant";
	}
}
