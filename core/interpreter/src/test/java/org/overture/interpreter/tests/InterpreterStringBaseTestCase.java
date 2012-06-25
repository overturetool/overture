package org.overture.interpreter.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.overture.interpreter.values.Value;
import org.overture.util.Base64;
import org.overturetool.test.framework.TestResourcesResultTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class InterpreterStringBaseTestCase extends TestResourcesResultTestCase<String>
{

	public InterpreterStringBaseTestCase()
	{
		super();

	}

	public InterpreterStringBaseTestCase(File file)
	{
		super(file);
	}

	public InterpreterStringBaseTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}
	
	public InterpreterStringBaseTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file,suiteName,testSuiteRoot);
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
					try
					{
						result = nodeType;
					} catch (Exception e)
					{
						fail("Not able to decode object stored result");
					}
			}
		}
		return result;
	}

	@Override
	protected boolean assertEqualResults(String expected, String actual)
	{
		// FIXME: check is not sufficient
		if (expected == null)
		{
			assert false : "No result file";
		}
		// return expected.size() == actual.size();
		return expected.equals(actual);
	}

	@Override
	protected File createResultFile(String filename)
	{
		if (mode == ContentModed.String)
		{
			String tmp = getName().substring(name.indexOf('_') + 1);
			tmp = File.separatorChar + "" + tmp.substring(0, tmp.indexOf('_'));
			return new File(filename + "_results" + tmp + ".result");
		}
		return super.createResultFile(filename );
	}

	@Override
	protected File getResultFile(String filename)
	{
		if (mode == ContentModed.String)
		{
			String tmp = getName().substring(name.indexOf('_') + 1);
			tmp = File.separatorChar + "" + tmp.substring(0, tmp.indexOf('_'));
			return new File(filename + "_results" + tmp + ".result");
		}
		return super.getResultFile(filename );
	}

	/**
	 * Read the object from Base64 string.
	 * 
	 * @throws Exception
	 */
	private static Object fromString(String s) throws Exception
	{

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(s)));
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

		return Base64.encode(baos.toByteArray()).toString();
	}
}
