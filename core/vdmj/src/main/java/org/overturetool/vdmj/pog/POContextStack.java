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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.types.Type;

@SuppressWarnings("serial")
public class POContextStack extends Stack<POContext>
{
	private Map<Expression, Type> knownTypes = new HashMap<Expression, Type>();

	public String getName()
	{
		StringBuilder result = new StringBuilder();
		String prefix = "";

		for (POContext ctxt: this)
		{
			String name = ctxt.getName();

			if (name.length() > 0)
			{
				result.append(prefix);
				result.append(name);
				prefix = ", ";
			}
		}

		return result.toString();
	}

	public String getObligation(String root)
	{
		StringBuilder result = new StringBuilder();
		String spacing = "  ";
		String indent = "";
		String tail = "";

		for (POContext ctxt: this)
		{
			String po = ctxt.getContext();

			if (po.length() > 0)
			{
				result.append(indent);
				result.append("(");
				result.append(indentNewLines(po, indent));
				result.append("\n");
				indent = indent + spacing;
				tail = tail + ")";
			}
		}

		result.append(indent);
		result.append(indentNewLines(root, indent));
		result.append(tail);
		result.append("\n");

		return result.toString();
	}

	private String indentNewLines(String line, String indent)
	{
		StringBuilder sb = new StringBuilder();
		String[] parts = line.split("\n");
		String prefix = "";

		for (int i=0; i<parts.length; i++)
		{
			sb.append(prefix);
			sb.append(parts[i]);
			prefix = "\n" + indent;
		}

		return sb.toString();
	}

	public void noteType(Expression exp, Type type)
	{
		knownTypes.put(exp, type);
	}

	public Type checkType(Expression exp, Type expected)
	{
		Type known = knownTypes.get(exp);
		return known == null ? expected : known;
	}
}
