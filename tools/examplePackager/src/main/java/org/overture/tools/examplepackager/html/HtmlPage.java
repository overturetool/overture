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

public class HtmlPage
{
	public final static String overtureExamplesPreLink = "http://overture.sourceforge.net/examples/";

	public static String makePage(String body)
	{
		return "<html>"
				+ "\n<head>"
				+ "\n<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">"
				+ "</head>" + "\n<body>\n" + body + "\n</body>\n</html>";
	}

	public static String makeP(String body)
	{
		return "<p>" + body + "\n</p>";
	}

	public static String makeLink(String text, String href)
	{
		return "<a href=\"" + href + "\">" + text + "</a>";
	}

	public static String makeLink(String text, String href, String preHref)
	{
		return "<a href=\"" + preHref + href + "\">" + text + "</a>";
	}

	public static String makeH1(String text)
	{
		return makeH(1, text);// return "\n<h1>" + text+ "</h1>\n";
	}

	public static String makeStyleCss()
	{
		return styleCss;
	}

	public static String makeOvertureStyleCss()
	{
		return drupalStyle;
	}

	public static String makeBr()
	{
		return "<br/>\n";
	}

	final static String styleCss = "table.mytable" + "\n{" + "\n	width: 100%;"
			+ "\npadding: 0px;" + "\nborder: none;"
			+ "\nborder: 1px solid #000000;" + "\n}" + "\ntable.mytable td"
			+ "\n{" + "\nfont-size: 11px;" + "\nborder: none;"
			+ "\nbackground-color: #EBEBEB;" + "\nvertical-align: middle;"
			+ "\npadding: 7px;" + "\nfont-weight: bold;"
			+ "\nfont-family: 'Courier New' , Courier, monospace;" + "\n}"
			+ "\n" + "\ntable.mytable td.faild" + "\n{"
			+ "\nbackground-color: #F87676;" + "\n}" + "\ntable.mytable td.ok"
			+ "\n{" + "\nbackground-color: #80F479;" + "\n}" + "\n"
			+ "\ntable.mytable th" + "\n{"
			+ "\nborder-style: none none double none;"
			+ "\nborder-width: medium;" + "\nborder-color: inherit;"
			+ "\nfont-size: 12px;" + "\nbackground-color: #EBEBEB;"
			+ "\nvertical-align: middle;" + "\npadding: 7px;"
			+ "\nfont-weight: bold;" + "\n}" + "\ntable.mytable tr.special td"
			+ "\n{" + "\nborder-bottom: 1px solid #ff0000;"
			+ "\nborder-bottom-color: #0033CC;" + "\n}" + "\n"
			+ "\ntable.mytable a" + "\n{" + "\ncolor: #000000;" + "\n}" + "\n"
			+ "\nbody" + "\n{" + "\nfont-family: Arial, Helvetica, sans-serif;"
			+ "\n}" + "\ntable.mytable tr.total td" + "\n{"
			+ "\n			border-style: double none none none;"
			+ "\n			border-width: medium;" + "\n			border-color: inherit;"
			+ "\n}" + "\ntable.mytable tr.total td.ok" + "\n{"
			+ "\n			background-color: #80F479;" + "\n}"
			+ "\ntable.mytable tr.total td.faild" + "\n{"
			+ "\n			background-color: #F87676;" + "\n}";

	private final static String drupalStyle = "<style type=\"text/css\">\n"
			+ ".examples h3\n" + "{\n" + "color: white;\n"
			+ "background-color: #340044;\n" + "width: 40%;\n"
			+ "margin: 0px;\n" + "padding: 8px 5px 5px 10px;\n"
			+ "font-weight: bold;\n" + "}\n" + "\n" + "table\n" + "{\n"
			+ "border-style:none;\n" + "background-color: #340044;\n" + "}\n"
			+ "\n" + "table.mytable\n" + "{\n" + "width: 100%;\n"
			+ "padding: 0px;\n" + "border: none;\n"
			+ "border: 3px solid #340044;\n"
			+ "font-family: Arial, Helvetica, sans-serif;\n"
			+ "font-size: 14px;\n" + "margin: 0px;\n"
			+ "margin-bottom: 18px;\n" + "border-collapse: separate;\n" + "}\n"
			+ "\n" + "table.mytable .first\n" + "{\n" + "font-weight: bold;\n"
			+ "width: 130px;\n" + "}\n" + "\n" + "table.mytable td\n" + "{\n"
			+ "border: none;\n" + "background-color: white;\n"
			+ "vertical-align: middle;\n" + "padding: 7px;\n" + "}\n" + "\n"
			+ "table.mytable td.faild\n" + "{\n"
			+ "background-color: #F87676;\n" + "}\n" + "\n"
			+ "table.mytable td.ok\n" + "{\n" + "background-color: #80F479;\n"
			+ "}\n" + "\n" + "table.mytable th\n" + "{\n"
			+ "border-style: none none double none;\n"
			+ "border-width: medium;\n" + "border-color: inherit;\n"
			+ "font-size: 12px;\n" + "background-color: #EBEBEB;\n"
			+ "vertical-align: middle;\n" + "padding: 7px;\n"
			+ "font-weight: bold;\n" + "}\n" + "\n"
			+ "table.mytable tr.special td\n" + "{\n"
			+ "border-bottom: 1px solid #340044;\n"
			+ "border-bottom-color: #0033CC;\n" + "}\n" + "\n"
			+ "table.mytable a\n" + "{\n" + "color: #000000;\n" + "}\n" + "\n"
			+ "body\n" + "{\n" + "font-family: Arial, Helvetica, sans-serif;\n"
			+ "}\n" + "table.mytable tr.total td\n" + "{\n"
			+ "			border-style: double none none none;\n"
			+ "			border-width: medium;\n" + "			border-color: inherit;\n"
			+ "}\n" + "table.mytable tr.total td.ok\n" + "{\n"
			+ "background-color: #80F479;\n" + "}\n"
			+ "table.mytable tr.total td.faild\n" + "{\n"
			+ "			background-color: #F87676;\n" + "}\n" + "</style>";

	public static String makeH(Integer level, String text)
	{
		return "\n<h" + level + ">" + text + "</h" + level + ">\n";
	}

	public static String makeDiv(String content, String className)
	{
		return "\n<div class=\"" + className + "\">" + content + "</div>\n";
	}
}
