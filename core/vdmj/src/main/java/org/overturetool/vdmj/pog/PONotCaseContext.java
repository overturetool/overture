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

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.types.Type;

public class PONotCaseContext extends POContext
{
	public final Pattern pattern;
	public final Type type;
	public final Expression exp;

	public PONotCaseContext(Pattern pattern, Type type, Expression exp)
	{
		this.pattern = pattern;
		this.type = type;
		this.exp = exp;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (pattern.isSimple())
		{
			sb.append("not ");
    		sb.append(pattern);
    		sb.append(" = ");
    		sb.append(exp);
		}
		else
		{
			Expression matching = pattern.getMatchingExpression();
			
    		sb.append("not exists ");
    		sb.append(matching);
    		sb.append(":");
    		sb.append(type);
    		sb.append(" & ");
    		sb.append(matching);
    		sb.append(" = ");
    		sb.append(exp);
		}

		sb.append(" =>");

		return sb.toString();
	}
}
