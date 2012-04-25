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

import java.util.Iterator;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;

public class POOperationDefinitionContext extends POContext
{
	public final LexNameToken name;
	public final OperationType deftype;
	public final PatternList paramPatternList;
	public final boolean addPrecond;
	public final Expression precondition;
	public final Definition stateDefinition;

	public POOperationDefinitionContext(ImplicitOperationDefinition definition,
		boolean precond, Definition stateDefinition)
	{
		this.name = definition.name;
		this.deftype = definition.type;
		this.addPrecond = precond;
		this.paramPatternList = definition.getParamPatternList();
		this.precondition = definition.precondition;
		this.stateDefinition = stateDefinition;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (!deftype.parameters.isEmpty())
		{
    		sb.append("forall ");
    		String sep = "";
			Iterator<Type> types = deftype.parameters.iterator();

			for (Pattern p: paramPatternList)
			{
				if (!(p instanceof IgnorePattern))
				{
					sb.append(sep);
					sb.append(p.toString());
					sb.append(":");
					sb.append(types.next());
					sep = ", ";
				}
			}

			if (stateDefinition != null)
			{
				appendStatePatterns(sb);
			}

    		sb.append(" &");

    		if (addPrecond && precondition != null)
    		{
    			sb.append(" ");
    			sb.append(precondition);
    			sb.append(" =>");
    		}
		}

		return sb.toString();
	}

	private void appendStatePatterns(StringBuilder sb)
	{
		if (stateDefinition == null)
		{
			return;
		}
		else if (stateDefinition instanceof StateDefinition)
		{
			StateDefinition def = (StateDefinition)stateDefinition;
			sb.append(", oldstate:");
			sb.append(def.name.name);
		}
		else
		{
			ClassDefinition def = (ClassDefinition)stateDefinition;
			sb.append(", oldself:");
			sb.append(def.name.name);
		}
	}
}
