/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.tools.examplepackager.html;

import java.util.Arrays;
import java.util.List;

public class HtmlTable
{
	public static final String STYLE_CLASS_FAILD = "faild";
	public static final String STYLE_CLASS_OK = "ok";

	public static String makeTable(String data)
	{
		return "\n<table class=\"mytable\">" + data + "\n</table>";
	}

	public static String makeRow(String data)
	{
		return "\n\t<tr>" + data + "\n\t</tr>";
	}

	public static String makeRowTotal(String data)
	{
		return "\n\t<tr class=\"total\">" + data + "\n\t</tr>";
	}

	public static String makeCell(String data)
	{
		return "\n<td>" + data + "</td>";
	}

	public static String makeCells(String[] data)
	{
		return makeCells(Arrays.asList(data));
	}

	public static String makeCells(List<String> data)
	{
		StringBuilder sb = new StringBuilder();
		for (String string : data)
		{
			sb.append(makeCell(string));
		}
		return sb.toString();
	}

	public static String makeCell(String data, String styleClass)
	{
		return "\n<td class=\"" + styleClass + "\">" + data + "</td>";
	}

	public static String makeCellHeaderss(String[] data)
	{
		StringBuilder sb = new StringBuilder();
		for (String string : data)
		{
			sb.append("\n<th>" + string + "</th>");
		}
		return sb.toString();
	}
}
