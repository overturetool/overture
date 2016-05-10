/*
 * #%~
 * Test Framework for Overture
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
package org.overture.test.util;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.overture.test.framework.Properties;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlResultReaderWriter<R>
{
	public interface IResultStore<T>
	{
		void encodeResult(T result, Document doc, Element resultElement);

		T decodeResult(Node node);
	}

	private File file;
	private Result<R> result = null;
	private String type;
	private IResultStore<R> resultStore;

	public XmlResultReaderWriter()
	{
	}

	public XmlResultReaderWriter(File file, IResultStore<R> store)
	{
		this.file = file;
		this.resultStore = store;
	}

	// public XmlResultReaderWriter(String path)
	// {
	// this(new File(path));
	// }

	public void setResult(String type, Result<R> result, IResultStore<R> store)
	{
		this.type = type;
		this.result = result;
		this.resultStore = store;
	}

	public void saveInXml() throws ParserConfigurationException,
			TransformerException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("testResult");
		doc.appendChild(rootElement);
		rootElement.setAttribute("type", type);

		createWarningsAndErrors(doc, rootElement);

		String oldLineSeparator = System.getProperty("line.separator");
		if (Properties.forceUnixLineEndings)
		{
			// Output using LF line endings (UNIX-style),
			// but only for this file.
			oldLineSeparator = System.getProperty("line.separator");
			System.setProperty("line.separator", "\n");
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

		if (Properties.forceUnixLineEndings)
		{
			// Restore line endings
			System.setProperty("line.separator", oldLineSeparator);
		}
	}

	private void createWarningsAndErrors(Document doc, Element rootElement)
	{

		List<IMessage> warnings = getResult().warnings;
		List<IMessage> errors = getResult().errors;

		if (warnings != null)
		{
			for (IMessage warning : warnings)
			{
				Element message = doc.createElement("message");
				message.setAttribute("messageType", "warning");
				message.setAttribute("resource", warning.getResource());
				message.setAttribute("number", new Integer(warning.getNumber()).toString());
				message.setAttribute("message", warning.getMessage());
				message.setAttribute("column", new Integer(warning.getCol()).toString());
				message.setAttribute("line", new Integer(warning.getLine()).toString());
				rootElement.appendChild(message);
			}
		}

		if (errors != null)
		{
			for (IMessage warning : errors)
			{
				Element message = doc.createElement("message");
				message.setAttribute("messageType", "error");
				message.setAttribute("resource", warning.getResource());
				message.setAttribute("number", new Integer(warning.getNumber()).toString());
				message.setAttribute("message", warning.getMessage());
				message.setAttribute("column", new Integer(warning.getCol()).toString());
				message.setAttribute("line", new Integer(warning.getLine()).toString());
				rootElement.appendChild(message);
			}
		}

		Element resultElement = doc.createElement("result");
		resultStore.encodeResult(result.result, doc, resultElement);
		rootElement.appendChild(resultElement);

	}

	public boolean loadFromXml()
	{
		// File resultFile = new File(file.getAbsoluteFile()+ ".result");
		List<IMessage> warnings = new ArrayList<IMessage>();
		List<IMessage> errors = new ArrayList<IMessage>();
		R readResult = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try
		{
			db = dbf.newDocumentBuilder();

			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("message");
			for (int i = 0; i < nodeLst.getLength(); i++)
			{
				Node node = nodeLst.item(i);
				String nodeType = node.getAttributes().getNamedItem("messageType").getNodeValue();
				if (nodeType.equals("error"))
				{
					convertNodeToMessage(errors, node);
				} else if (nodeType.equals("warning"))
				{
					convertNodeToMessage(warnings, node);
				}
			}

			for (int i = 0; i < doc.getDocumentElement().getChildNodes().getLength(); i++)
			{
				Node node = doc.getDocumentElement().getChildNodes().item(i);
				if (node.getNodeName() != null
						&& node.getNodeName().equals("result"))
				{
					node.normalize();
					readResult = resultStore.decodeResult(node);
				}
				// System.out.println(node);
			}
			// doc.getDocumentElement().getChildNodes().getElementsByTagName("result")

			String type = doc.getDocumentElement().getAttributes().getNamedItem("type").getNodeValue();
			setResult(type, new Result<R>(readResult, warnings, errors));
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}

	private void convertNodeToMessage(List<IMessage> set, Node node)
	{

		NamedNodeMap nnm = node.getAttributes();
		String resource = nnm.getNamedItem("resource").getNodeValue();
		IMessage m = new Message(resource, Integer.parseInt(nnm.getNamedItem("number").getNodeValue()), Integer.parseInt(nnm.getNamedItem("line").getNodeValue()), Integer.parseInt(nnm.getNamedItem("column").getNodeValue()), nnm.getNamedItem("message").getNodeValue());
		set.add(m);
	}

	public Result<R> getResult()
	{
		return result;
	}

	public void setResult(String type, Result<R> result)
	{
		this.result = result;
		this.type = type;
	}

	public List<IMessage> getWarnings()
	{
		if (result != null)
		{
			return result.warnings;
		}

		return null;
	}

	public List<IMessage> getErrors()
	{
		if (result != null)
		{
			return result.errors;
		}

		return null;
	}

}
