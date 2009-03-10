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

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExternalDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.Value;

public class IdentifierDesignator extends StateDesignator
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken name;

	public IdentifierDesignator(LexNameToken name)
	{
		super(name.location);
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name.getName();
	}

	@Override
	public Type typeCheck(Environment env)
	{
		if (env.isVDMPP())
		{
			LexNameToken exname = name.getExplicit(true);
			Expression expression = new VariableExpression(exname);
			return expression.typeCheck(env, null, NameScope.NAMESANDSTATE);
		}
		else
		{
			Definition def = env.findName(name, NameScope.STATE);

			if (def == null)
			{
				report(3247, "Unknown state variable " + name + " in assignment");
				return new UnknownType(name.location);
			}
			else if (def instanceof ExternalDefinition)
			{
				ExternalDefinition d = (ExternalDefinition)def;

				if (d.readOnly)
				{
					report(3248, "Cannot assign to 'ext rd' state " + name);
				}
			}
			// else just state access in (say) an explicit operation

			return def.getType();
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		// We lookup the name in a context comprising only state...
		return ctxt.getUpdateable().lookup(name.getExplicit(true));
	}
}
