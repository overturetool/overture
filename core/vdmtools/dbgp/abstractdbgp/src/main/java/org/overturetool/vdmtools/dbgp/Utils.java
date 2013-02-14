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

package org.overturetool.vdmtools.dbgp;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Utils
{
	public static <T> String listToString(List<T> list)
	{
		return listToString("", list, ", ", "");
	}

	public static <T> String listToString(List<T> list, String separator)
	{
		return listToString("", list, separator, "");
	}

	public static <T> String listToString(String before, List<T> list, String separator, String after)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(before);

		if (!list.isEmpty())
		{
			sb.append(list.get(0).toString());

			for (int i=1; i<list.size(); i++)
			{
				sb.append(separator);
				sb.append(list.get(i).toString());
			}
		}

		sb.append(after);
		return sb.toString();
	}

	public static <T> String setToString(Set<T> set, String separator)
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
}
