package org.overturetool.traces.utility;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class XmlFileWriter {

	FileWriter outputFileReader;
	PrintWriter outputStream;
	int level = -1;
	Boolean inElement = false;
	String rootName;

	public void StartDocument(String name, String root) throws IOException {

		outputFileReader = new FileWriter(name);
		outputStream = new PrintWriter(outputFileReader);
		rootName = root;
		StartElement(rootName);

	}

	public void StopDocument() throws IOException {

		StopElement(rootName);
		outputStream.close();
		outputFileReader.close();

	}

	public static String GetIndentation(int level) {
		String indentation = "";
		for (int i = 0; i < level; i++) {
			indentation += "	";
		}
		return indentation;
	}

	public void StartElement(String name) {

		level++;
		outputStream.println(GetIndentation(level) + "<" + name + ">");
		inElement = true;

	}
	
	public static String NormalizeValue(String value)
	{
	return	value.replace("\"", "&quot;").replace("<", "").replace(">", "");
	}

	public void StartElement(String name, String attribyteName,
			String attributeValue) {

		
		outputStream.println(GetIndentation(level) + "<" + name + " "
				+ attribyteName + "=\"" +NormalizeValue( attributeValue)
				+ "\">");
		inElement = true;
		level++;
	}
	
	public void StartElement(String name, String attribyteName1,
			String attributeValue1, String attribyteName2,
			String attributeValue2) {

		
		outputStream.println(GetIndentation(level) + "<" + name + " "
				+ attribyteName1 + "=\"" +NormalizeValue( attributeValue1)
				+ "\" "+ attribyteName2 + "=\"" + NormalizeValue(attributeValue2)
				+ "\">");
		inElement = true;
		level++;
	}

	public void StopElement(String name) {
		level--;
		outputStream.println(GetIndentation(level) +"</" + name + ">");
		inElement = false;
		

	}

	public void WriteValue(String value) {

		
		if (inElement)
			outputStream.println(GetIndentation(level) +value.replace("\"", "&quot;"));
		else
			System.err
					.println("Errir priting value skipped since it was out side an element");

	}
}
