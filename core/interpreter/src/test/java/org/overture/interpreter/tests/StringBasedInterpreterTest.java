package org.overture.interpreter.tests;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Assert;
import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.Properties;
import org.overture.test.framework.TestResourcesResultTestCase4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class StringBasedInterpreterTest extends
		TestResourcesResultTestCase4<String>
{

	private static final String TESTS_TC_PROPERTY_PREFIX = "tests.interpreter.override.";

	public StringBasedInterpreterTest(File file, String suiteName,
			File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	public void encodeResult(String result, Document doc, Element resultElement)
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

	protected void configureResultGeneration()
	{
		LexLocation.absoluteToStringLocation = false;
		if (System.getProperty(TESTS_TC_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
				&& System.getProperty(TESTS_TC_PROPERTY_PREFIX
						+ getPropertyId()) != null)
		{
			Properties.recordTestResults = true;
		}

	}

	protected void unconfigureResultGeneration()
	{
		Properties.recordTestResults = false;
	}

	protected abstract String getPropertyId();

}
