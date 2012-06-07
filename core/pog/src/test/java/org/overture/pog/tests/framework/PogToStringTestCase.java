package org.overture.pog.tests.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.overture.pog.obligations.ProofObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.util.PogUtil.PogResult;
import org.overturetool.test.framework.ResultTestCase;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.messages.VDMMessage;
import org.overturetool.vdmj.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class PogToStringTestCase extends ResultTestCase<List<String>>
{
	public PogToStringTestCase()
	{
		super();

	}

	public PogToStringTestCase(File file)
	{
		super(file);
	}

	@Override
	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	public void encondeResult(List<String> result, Document doc,
			Element resultElement) {
		// TODO Auto-generated method stub
		
	}
	public void encondeResult(ProofObligationList result, Document doc,
			Element resultElement)
	{
		for (ProofObligation po : result)
		{
			Element message = doc.createElement("po");
			message.setAttribute("resource", file.getName());
			message.setAttribute("number", new Integer(po.number).toString());
			message.setAttribute("message", po.toString());
			message.setAttribute("column", po.location.startPos + "");
			message.setAttribute("line", po.location.startLine + "");
			try
			{
				message.setAttribute("object", toString(po));
			} catch (IOException e)
			{
				e.printStackTrace();
				fail("Failed to serialize po");
			}
			resultElement.appendChild(message);
		}

	}

	public List<String> decodeResult(Node node)
	{
		List<String> list = new Vector<String>();

		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			Node cn = node.getChildNodes().item(i);
			if (cn.getNodeType() == Node.ELEMENT_NODE
					&& cn.getNodeName().equals("po"))
			{
				String nodeType = cn.getAttributes().getNamedItem("toString").getNodeValue();
				if(nodeType!=null && !nodeType.isEmpty())
				{
					try
					{
						list.add(nodeType);
					} catch (Exception e)
					{
						fail("Not able to decode toString stored result");
					}
				}
			}
		}
		return list;
	}

	@Override
	protected boolean assertEqualResults(List<String> expected,
			List<String> actual)
	{
		
		boolean errorFound = true;
		for (String string : actual) {
			if(!expected.contains(string))
			{
				System.out.println("PO found but not expected: " + string);
				errorFound = false;
			}
		}
		
		for (String string : expected) {
			if(!actual.contains(string))
			{
				System.out.println("PO expected but not found: " + string);
				errorFound = false;
			}
		}
		return errorFound;
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
	
	
	@SuppressWarnings("unchecked")
	protected static Result<List<String>> convert(@SuppressWarnings("rawtypes") PogResult result)
	{
		if(result.result==null)
		{
			return new Result<List<String>>(convertToStringList(result), convert(result.typeCheckResult.warnings), convert(result.typeCheckResult.errors));
		}
		return new Result<List<String>>(convertToStringList(result), convert(result.warnings), convert(result.errors));
	}
	
	private static List<String> convertToStringList(@SuppressWarnings("rawtypes") PogResult result) {
		List<String> list = new Vector<String>();
		for (ProofObligation po : result.result) {
			list.add(po.toString());
		}
		
		return list;
	}

	public static List<IMessage> convert(List<? extends VDMMessage> messages)
	{
		List<IMessage> testMessages = new Vector<IMessage>();

		for (VDMMessage msg : messages)
		{
			testMessages.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
		}

		return testMessages;
	}
}
