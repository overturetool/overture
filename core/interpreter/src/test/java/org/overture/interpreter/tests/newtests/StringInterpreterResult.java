package org.overture.interpreter.tests.newtests;

import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.util.List;

import org.junit.Assert;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class StringInterpreterResult
{

	String result;
	public final List<IMessage> warnings;
	public final List<IMessage> errors;

	public static StringInterpreterResult convert(Result<String> r)
	{
		return new StringInterpreterResult(r.getStringResult(), r.warnings, r.errors);
	}

	public StringInterpreterResult(String result, List<IMessage> warnings,
			List<IMessage> errors)
	{
		super();
		this.result = result;
		this.warnings = warnings;
		this.errors = errors;
	}

	protected boolean assertEqualResults(String actual)
	{
		if (result == null)
		{
			assert false : "No result file";
		}
		// return expected.size() == actual.size();
		if (!result.equals(actual))
		{
			fail("Expected result does not match actual:\n\tExpected:\n\t"
					+ result + "\n\tActual:\n\t" + actual);
			return false;
		}
		return true;
	}

	public void encodeResult(String filename, String result, Document doc,
			Element resultElement)
	{
		Element message = doc.createElement("output");

		message.setAttribute("object", result);
		message.setAttribute("resource", filename);
		message.setAttribute("value", result + "");

		resultElement.appendChild(message);
	}

	public static String decodeResult(Node node)
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

	public String getResult()
	{
		return result;
	}

}
