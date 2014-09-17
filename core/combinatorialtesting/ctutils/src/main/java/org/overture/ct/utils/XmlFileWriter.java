/*
 * #%~
 * Combinatorial Testing Utilities
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ct.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class XmlFileWriter
{
	enum NodeState
	{
		End, Start
	}

	public static String deNormalizeValue(String value)
	{
		return value.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&apos;", "'");

		// &amp; (& or "ampersand")
		// &lt; (< or "less than")
		// &gt; (> or "greater than")
		// &apos; (' or "apostrophe")
		// &quot; (" or "quotation mark")
	}

	public static String getIndentation(int level)
	{
		String indentation = "";
		for (int i = 0; i < level; i++)
		{
			indentation += "	";
		}
		return indentation;
	}

	public static String normalizeValue(String value)
	{
		return value.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("'", "&apos;");

		// &amp; (& or "ampersand")
		// &lt; (< or "less than")
		// &gt; (> or "greater than")
		// &apos; (' or "apostrophe")
		// &quot; (" or "quotation mark")
	}

	int flushindex = 0;;
	Boolean inElement = false;

	int level = 0;

	Writer outputStream;

	NodeState previousNode = NodeState.End;

	String rootName;

	public void startDocument(File name, String root) throws IOException
	{
		outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "UTF-8"));

		rootName = root;
		startElement(rootName);
	}

	void prepareElement() throws IOException
	{
		if (previousNode == NodeState.Start)
		{
			outputStream.append("\n");
		}
		previousNode = NodeState.Start;
	}

	public void startElement(String name) throws IOException
	{
		prepareElement();
		outputStream.append(getIndentation(level) + "<" + name + ">");
		level++;
		inElement = true;
	}

	public void startElement(String name, String... attribute)
			throws IOException
	{
		prepareElement();
		String element = getIndentation(level) + "<" + name + " ";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < attribute.length; i++)
		{

			if (i % 2 == 0)
			{
				sb.append(attribute[i] + "=");
			} else
			{
				sb.append("\"" + normalizeValue(attribute[i]) + "\" ");
			}
		}
		element = element + sb.toString().trim() + ">";
		outputStream.append(element);

		// outputStream.println(GetIndentation(level) + "<" + name + " "
		// + attribyteName1 + "=\"" +NormalizeValue( attributeValue1)
		// + "\" "+ attribyteName2 + "=\"" + NormalizeValue(attributeValue2)
		// + "\">");
		inElement = true;
		level++;
	}

	public void startElement(String name, String attribyteName,
			String attributeValue) throws IOException
	{
		prepareElement();
		outputStream.append(getIndentation(level) + "<" + name + " "
				+ attribyteName + "=\"" + normalizeValue(attributeValue)
				+ "\">");
		inElement = true;
		level++;
	}

	public void stopDocument() throws IOException
	{

		stopElement(rootName);
		outputStream.close();
		// outputFileReader.close();

	}

	public void stopElement(String name) throws IOException
	{
		if (previousNode == NodeState.End)
		{
			outputStream.append(getIndentation(level - 1));
		}
		previousNode = NodeState.End;
		level--;
		outputStream.append("</" + name + ">\n");
		inElement = false;
		flushindex++;
		if (flushindex > 100)
		{
			outputStream.flush();
			flushindex = 0;
		}

	}

	public void writeValue(String value) throws IOException
	{

		if (inElement)
		{
			outputStream.append(normalizeValue(value));
		} else
		{
			System.err.println("Error priting value skipped since it was out side an element");
		}

	}
}
