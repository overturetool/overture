package org.overture.codegen.execution.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import org.junit.Assert;
import org.overture.ast.lex.LexLocation;
import org.overture.interpreter.values.Value;
import org.overture.test.framework.Properties;
import org.overture.test.framework.TestResourcesResultTestCase4;
import org.overture.util.Base64;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class JavaCodeGenTestCase extends
		TestResourcesResultTestCase4<Object>
{
	private static final String TESTS_PROPERTY_PREFIX = "tests.javagen.override.";

	public JavaCodeGenTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	/**
	 * Read the object from Base64 string.
	 * 
	 * @throws Exception
	 */
	private static Object fromString(String s) throws Exception
	{
		byte[] data = Base64.decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	/** Write the object to a Base64 string. */
	private static String toString(Serializable o) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return new String(Base64.encode(baos.toByteArray()));
	}

	public void encodeResult(Object result, Document doc, Element resultElement)
	{
		Element message = doc.createElement("output");

		if (result instanceof Serializable)
			try
			{
				if(result instanceof Value)
				{
					result = ((Value) result).deref();
				}
				message.setAttribute("object", toString((Serializable) result));
			} catch (DOMException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		message.setAttribute("resource", file.getName());
		message.setAttribute("value", result + "");

		resultElement.appendChild(message);
	}

	public Object decodeResult(Node node)
	{

		String result = null;
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			Node cn = node.getChildNodes().item(i);
			if (cn.getNodeType() == Node.ELEMENT_NODE
					&& cn.getNodeName().equals("output"))
			{
				try
				{

					Node attribute = cn.getAttributes().getNamedItem("object");
					
					if(attribute==null)
					{
						return null;
					}
					
					Object nodeType = fromString(attribute.getNodeValue());
					return nodeType;
				} catch (Exception e)
				{
					e.printStackTrace();
					Assert.fail("Not able to decode object stored result");
				}
				// String nodeType = cn.getAttributes().getNamedItem("object").getNodeValue();
				// if (nodeType != null && !nodeType.isEmpty())
				// {
				// try
				// {
				// result = nodeType;
				// } catch (Exception e)
				// {
				// Assert.fail("Not able to decode object stored result");
				// }
				// }
			}
		}
		return result;
	}

	@Override
	protected boolean assertEqualResults(Object expected, Object actual,
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
		if (System.getProperty(TESTS_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
				&& System.getProperty(TESTS_PROPERTY_PREFIX + getPropertyId()) != null)
		{
			Properties.recordTestResults = true;
		}

	}

	protected void unconfigureResultGeneration()
	{
		Properties.recordTestResults = false;
	}

	protected abstract String getPropertyId();

	protected File getStorageLocation()
	{
		return file.getParentFile();
	}

	protected File getInputLocation()
	{
		return file.getParentFile();
	}

	@Override
	protected File createResultFile(String filename)
	{
		getStorageLocation().mkdirs();
		return getResultFile(filename);
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(getStorageLocation(), filename);
	}
}
