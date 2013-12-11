package org.overture.interpreter.tests;

import java.io.File;
import java.io.PrintWriter;

import junit.framework.Assert;

import org.overture.test.framework.TestResourcesResultTestCase4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class InterpreterStringBaseTestCase extends
		TestResourcesResultTestCase4<String>
{

	public InterpreterStringBaseTestCase(File file, String suiteName,
			File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	public void encondeResult(String result, Document doc, Element resultElement)
	{
		Element message = doc.createElement("output");

		message.setAttribute("object", result);
		message.setAttribute("resource", file.getName());
		message.setAttribute("value", result + "");

		resultElement.appendChild(message);
	}

	public String decodeResult(Node node)
	{

		String result = null;
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			Node cn = node.getChildNodes().item(i);
			if (cn.getNodeType() == Node.ELEMENT_NODE
					&& cn.getNodeName().equals("output"))
			{
				String nodeType = cn.getAttributes().getNamedItem("object").getNodeValue();
				if (nodeType != null && !nodeType.isEmpty())
				{
					try
					{
						result = nodeType;
					} catch (Exception e)
					{
						Assert.fail("Not able to decode object stored result");
					}
				}
			}
		}
		return result;
	}

	@Override
	protected boolean assertEqualResults(String expected, String actual,
			PrintWriter out)
	{
		// FIXME: check is not sufficient
		if (expected == null)
		{
			assert false : "No result file";
		}
		// return expected.size() == actual.size();
		if (!expected.equals(actual))
		{
			out.println("Expected result does not match actual:\n\tExpected:\n\t"
					+ expected + "\n\tActual:\n\t" + actual);
			return false;
		}
		return true;
	}

}
