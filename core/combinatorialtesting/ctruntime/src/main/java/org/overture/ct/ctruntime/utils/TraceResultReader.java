/*
 * #%~
 * Combinatorial Testing Runtime
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
package org.overture.ct.ctruntime.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.overture.ct.utils.TraceXmlWrapper;
import org.overture.interpreter.traces.Verdict;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TraceResultReader
{
	public List<TraceResult> read(File file) throws SAXException, IOException,
			ParserConfigurationException, XPathExpressionException
	{
		List<TraceResult> results = new Vector<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		FileInputStream is = new FileInputStream(file);
		Document doc = builder.parse(is);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		XPathExpression expr = xpath.compile("//" + TraceXmlWrapper.TRACE_TAG);

		final NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		for (Node n : new NodeIterator(list))
		{
			final String traceName = n.getAttributes().getNamedItem(TraceXmlWrapper.NAME_TAG).getNodeValue();

			TraceResult tr = new TraceResult();
			results.add(tr);
			tr.traceName = traceName;

			expr = xpath.compile(TraceXmlWrapper.TEST_CASE_TAG);
			final NodeList tests = (NodeList) expr.evaluate(n, XPathConstants.NODESET);

			for (Node testNode : new NodeIterator(tests))
			{
				Integer testNo = Integer.parseInt(testNode.getAttributes().getNamedItem(TraceXmlWrapper.NUMBER_TAG).getNodeValue());
				String test = testNode.getTextContent().trim();

				expr = xpath.compile(TraceXmlWrapper.RESULT_TAG + "[@"
						+ TraceXmlWrapper.NUMBER_TAG + "='" + testNo + "']");

				final NodeList resultNodeList = (NodeList) expr.evaluate(n, XPathConstants.NODESET);

				final Node resultNode = resultNodeList.item(0);
				final String result = resultNode.getTextContent().trim();
				final String verdict = resultNode.getAttributes().getNamedItem(TraceXmlWrapper.VERDICT_TAG).getNodeValue();

				TraceTest tt = new TraceTest(testNo, test, result, Verdict.valueOf(verdict));
				tr.tests.add(tt);
			}
		}

		is.close();

		return results;
	}
}
