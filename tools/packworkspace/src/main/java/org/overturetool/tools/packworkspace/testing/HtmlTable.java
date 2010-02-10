package org.overturetool.tools.packworkspace.testing;

public class HtmlTable
{
	public static final String STYLE_CLASS_FAILD="faild";
	public static final String STYLE_CLASS_OK="ok";
	
	
	public static String makeTable(String data)
	{
		return "\n<table class=\"mytable\">" + data + "\n</table>";
	}

	public static String makeRow(String data)
	{
		return "\n\t<tr>" + data + "\n\t</tr>";
	}

	public static String makeCell(String data)
	{
		return "\n<td>" + data + "</td>";
	}
	
	public static String makeCells(String[] data)
	{
		StringBuilder sb = new StringBuilder();
		for (String string : data)
		{
			sb.append(makeCell(string));
		}
		return sb.toString();
	}
	
	public static String makeCell(String data,String styleClass)
	{
		return "\n<td class=\""+styleClass+"\">" + data + "</td>";
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
