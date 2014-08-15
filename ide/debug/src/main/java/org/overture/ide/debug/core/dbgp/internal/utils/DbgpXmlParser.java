/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.dbgp.internal.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
//import org.eclipse.dltk.compiler.util.Util;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpDebuggingEngineException;

public class DbgpXmlParser
{
	protected DbgpXmlParser()
	{
	}

	protected static int parseLine(String s)
	{
		final int index = s.indexOf(':');
		if (index < 0)
		{
			return -1;
		}
		return Integer.parseInt(s.substring(0, index));
	}

	protected static int parseColumn(String s)
	{
		final int index = s.indexOf(':');
		if (index < 0)
		{
			return -1;
		}
		return Integer.parseInt(s.substring(index + 1));
	}

	protected static boolean makeBoolean(String s)
	{
		return Integer.parseInt(s) == 0 ? false : true;
	}

	public static Document parseXml(byte[] xml) throws DbgpProtocolException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = factory.newDocumentBuilder();

			// builder.setErrorHandler(new ErrorHandler() {
			// public void error(SAXParseException exception)
			// throws SAXException {
			// }
			//
			// public void fatalError(SAXParseException exception)
			// throws SAXException {
			// }
			//
			// public void warning(SAXParseException exception)
			// throws SAXException {
			// }
			// });

			InputSource source = new InputSource(new ByteArrayInputStream(xml));
			return builder.parse(source);
		} catch (ParserConfigurationException e)
		{
			throw new DbgpProtocolException(e);
		} catch (SAXException e)
		{
			throw new DbgpProtocolException(e);
		} catch (IOException e)
		{
			throw new DbgpProtocolException(e);
		}
	}

	protected static String parseContent(Element element)
	{
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); ++i)
		{
			Node e = list.item(i);
			int type = e.getNodeType();

			if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE)
			{
				return e.getNodeValue();
			}
		}

		return Util.EMPTY_STRING;
	}

	public static String parseBase64Content(Element element)
	{
		return Base64Helper.decodeString(parseContent(element));
	}

	public static DbgpDebuggingEngineException checkError(Element element)
	{
		final String TAG_ERROR = "error"; //$NON-NLS-1$
		final String TAG_MESSAGE = "message"; //$NON-NLS-1$
		final String ATTR_CODE = "code"; //$NON-NLS-1$

		NodeList errors = element.getElementsByTagName(TAG_ERROR);
		if (errors.getLength() > 0)
		{
			Element error = (Element) errors.item(0);
			int errorCode = Integer.parseInt(error.getAttribute(ATTR_CODE));

			String errorText = "No message"; //$NON-NLS-1$
			NodeList messages = error.getElementsByTagName(TAG_MESSAGE);

			if (messages.getLength() > 0)
			{
				errorText = parseContent((Element) messages.item(0));
			}

			return new DbgpDebuggingEngineException(errorCode, errorText);
		}

		return null;
	}

	public static boolean parseSuccess(Element response)
	{
		final String ATTR_SUCCESS = "success"; //$NON-NLS-1$

		// Strange assumption but it's required for compatibility
		if (!response.hasAttribute(ATTR_SUCCESS))
		{
			return true;
		}

		return makeBoolean(response.getAttribute(ATTR_SUCCESS));
	}

	protected static String getStringAttribute(Element element, String name)
	{

		String value = ""; //$NON-NLS-1$
		if (element.hasAttribute(name))
		{
			value = element.getAttribute(name);
		}

		return value;
	}

	protected static int getIntAttribute(Element element, String name,
			int defaultValue)
	{

		int value = defaultValue;

		if (element.hasAttribute(name))
		{
			try
			{
				value = Integer.parseInt(element.getAttribute(name));
			} catch (NumberFormatException e)
			{
				// TODO: this should probably be logged
			}
		}

		return value;
	}

}
