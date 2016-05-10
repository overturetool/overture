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

package org.overture.ast.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;

public class Utils
{
	private Utils() {
	}

	public static <T> String listToString(List<T> list)
	{
		return listToString("", list, ", ", "");
	}

	public static <T> String listToString(List<T> list, String separator)
	{
		return listToString("", list, separator, "");
	}

	public static <T> String listToString(String before, List<T> list,
			String separator, String after)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(before);

		if (!list.isEmpty())
		{
			sb.append(list.get(0).toString());

			for (int i = 1; i < list.size(); i++)
			{
				sb.append(separator);
				sb.append(list.get(i).toString());
			}
		}

		sb.append(after);
		return sb.toString();
	}

	public static <T> String listToString(String separator, List<T> list)
	{
		return listToString("", separator, list, "");
	}

	public static <T> String listToString(String before, String separator,
			List<T> list, String after)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(before);

		if (!list.isEmpty())
		{
			sb.append(separator);
			sb.append(list.get(0).toString());

			for (int i = 1; i < list.size(); i++)
			{
				sb.append(separator);
				sb.append(list.get(i).toString());
			}
		}

		sb.append(after);
		return sb.toString();
	}

	public static <T> String setToString(Collection<T> set, String separator)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");

		if (!set.isEmpty())
		{
			Iterator<T> i = set.iterator();
			sb.append(i.next().toString());

			while (i.hasNext())
			{
				sb.append(separator);
				sb.append(i.next().toString());
			}
		}

		sb.append(")");
		return sb.toString();
	}

	public static String ifToString(PExp ifExp, PExp thenExp,
			List<AElseIfExp> elseList, PExp elseExp)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(if " + ifExp + "\nthen " + thenExp);

		for (AElseIfExp s : elseList)
		{
			sb.append("\n");
			sb.append(s.toString());
		}

		if (elseExp != null)
		{
			sb.append("\nelse ");
			sb.append(elseExp.toString());
		}

		sb.append(")");

		return sb.toString();
	}
	
	public static String recNameToString(ILexNameToken name)
	{
		String fullRecName;
		
		// Flat specifications have blank module names
		if(name.getModule().length() == 0)
		{
			fullRecName = "DEFAULT";
		}
		else
		{
			fullRecName = name.getModule();
		}
		
		fullRecName += "`";
		fullRecName += name.getName();
		
		return fullRecName;
	}
	
	
}
