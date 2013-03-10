package org.overture.ct.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class XmlFileWriter
{

	FileWriter outputFileReader;
	PrintWriter outputStream;
	int level = 0;
	Boolean inElement = false;
	String rootName;

	public void StartDocument(File name, String root) throws IOException
	{

		outputFileReader = new FileWriter(name);
		outputStream = new PrintWriter(outputFileReader);
		rootName = root;
		StartElement(rootName);

	}

	public void StopDocument() throws IOException
	{

		StopElement(rootName);
		outputStream.close();
		outputFileReader.close();

	}

	public static String GetIndentation(int level)
	{
		String indentation = "";
		for (int i = 0; i < level; i++)
		{
			indentation += "	";
		}
		return indentation;
	}

	public void StartElement(String name)
	{

		level++;
		outputStream.println(GetIndentation(level) + "<" + name + ">");
		inElement = true;

	}

	public static String NormalizeValue(String value)
	{
		return value.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("'", "&apos;");

		// &amp; (& or "ampersand")
		// &lt; (< or "less than")
		// &gt; (> or "greater than")
		// &apos; (' or "apostrophe")
		// &quot; (" or "quotation mark")
	}

	public static String DeNormalizeValue(String value)
	{
		return value.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&apos;", "'");

		// &amp; (& or "ampersand")
		// &lt; (< or "less than")
		// &gt; (> or "greater than")
		// &apos; (' or "apostrophe")
		// &quot; (" or "quotation mark")
	}

	public void StartElement(String name, String attribyteName,
			String attributeValue)
	{

		outputStream.println(GetIndentation(level) + "<" + name + " "
				+ attribyteName + "=\"" + NormalizeValue(attributeValue)
				+ "\">");
		inElement = true;
		level++;
	}

	public void StartElement(String name, String... attribute)
	{

		String element = GetIndentation(level) + "<" + name + " ";

		for (int i = 0; i < attribute.length; i++)
		{

			if (i % 2 == 0)
				element += attribute[i] + "=";
			else
				element += "\"" + NormalizeValue(attribute[i]) + "\" ";
		}
		element += ">";
		outputStream.println(element);

		// outputStream.println(GetIndentation(level) + "<" + name + " "
		// + attribyteName1 + "=\"" +NormalizeValue( attributeValue1)
		// + "\" "+ attribyteName2 + "=\"" + NormalizeValue(attributeValue2)
		// + "\">");
		inElement = true;
		level++;
	}

	public void StopElement(String name)
	{
		level--;
		outputStream.println(GetIndentation(level) + "</" + name + ">");
		inElement = false;

	}

	public void WriteValue(String value)
	{

		if (inElement)
			outputStream.println(GetIndentation(level) + NormalizeValue(value));
		else
			System.err.println("Errir priting value skipped since it was out side an element");

	}
}
