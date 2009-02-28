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
import java.util.List;

import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.Type;

public class POFunctionDefinitionContext extends POContext
{
	public final LexNameToken name;
	public final FunctionType deftype;
	public final List<PatternList> paramPatternList;
	public final boolean addPrecond;
	public final Expression precondition;

	public POFunctionDefinitionContext(
		ExplicitFunctionDefinition definition, boolean precond)
	{
		this.name = definition.name;
		this.deftype = definition.type;
		this.paramPatternList = definition.paramPatternList;
		this.addPrecond = precond;
		this.precondition = definition.precondition;
	}

	public POFunctionDefinitionContext(
		ImplicitFunctionDefinition definition, boolean precond)
	{
		this.name = definition.name;
		this.deftype = definition.type;
		this.addPrecond = precond;
		this.paramPatternList = definition.getParamPatternList();
		this.precondition = definition.precondition;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (!deftype.parameters.isEmpty())
		{
    		sb.append("forall ");
    		String sep = "";
    		FunctionType ftype = deftype;

    		for (PatternList pl: paramPatternList)
    		{
    			Iterator<Type> types = ftype.parameters.iterator();

    			for (Pattern p: pl)
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

    			if (ftype.result instanceof FunctionType)
    			{
    				ftype = (FunctionType)ftype.result;
    			}
    			else
    			{
    				break;
    			}
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
}
