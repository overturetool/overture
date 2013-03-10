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
import org.overture.test.framework.TestResourcesResultTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class InterpreterBaseTestCase extends TestResourcesResultTestCase<Value>
{

	public InterpreterBaseTestCase()
	{
		super();

	}

	public InterpreterBaseTestCase(File file)
	{
		super(file);
	}

	public InterpreterBaseTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}
	
	public InterpreterBaseTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file,suiteName,testSuiteRoot);
	}

	public void encondeResult(Value result, Document doc, Element resultElement)
	{
		Element message = doc.createElement("output");
		
		try
		{
			message.setAttribute("object", toString(result));
		} catch (IOException e)
		{
			e.printStackTrace();
			fail("Failed to serialize po");
		}
		message.setAttribute("resource", file.getName());
		message.setAttribute("value", result + "");
		
		resultElement.appendChild(message);
	}

	public Value decodeResult(Node node)
	{

		Value val = null;
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
						val = ((Value) fromString(nodeType));
					} catch (Exception e)
					{
						fail("Not able to decode object stored result");
					}
			}
		}
		return val;
	}

	@Override
	protected boolean assertEqualResults(Value expected, Value actual)
	{
		// FIXME: check is not sufficient
		if (expected == null)
		{
			if(actual==null)
			{
				fail("No result calculated. Actual: "+actual);
			}
			return true;
//			assert false : "No result file";
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
