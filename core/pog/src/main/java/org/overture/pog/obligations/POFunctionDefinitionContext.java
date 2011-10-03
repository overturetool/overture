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

package org.overture.pog.obligations;

import java.util.Iterator;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameToken;

public class POFunctionDefinitionContext extends POContext
{
	public final LexNameToken name;
	public final AFunctionType deftype;
	public final List<List<PPattern>> paramPatternList;
	public final boolean addPrecond;
	public final PExp precondition;

	public POFunctionDefinitionContext(
		AExplicitFunctionDefinition definition, boolean precond)
	{
		this.name = definition.getName();
		this.deftype = definition.getType();
		this.paramPatternList = definition.getParamPatternList();
		this.addPrecond = precond;
		this.precondition = definition.getPrecondition();
	}

	public POFunctionDefinitionContext(
		AImplicitFunctionDefinition definition, boolean precond)
	{
		this.name = definition.getName();
		this.deftype = definition.getType();
		this.addPrecond = precond;
		this.paramPatternList = definition.getParamPatternList();
		this.precondition = definition.getPrecondition();
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (!deftype.getParameters().isEmpty())
		{
    		sb.append("forall ");
    		String sep = "";
    		AFunctionType ftype = deftype;

    		for (List<PPattern> pl: paramPatternList)
    		{
    			Iterator<PType> types = ftype.getParameters().iterator();

    			for (PPattern p: pl)
    			{
    				if (!(p instanceof AIgnorePattern))
    				{
    					sb.append(sep);
    					sb.append(p.toString());
    					sb.append(":");
    					sb.append(types.next());
    					sep = ", ";
    				}
    			}

    			if (ftype.getResult() instanceof AFunctionType)
    			{
    				ftype = (AFunctionType)ftype.getResult();
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
