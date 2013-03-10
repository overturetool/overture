package org.overture.pog.tests.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.overture.parser.messages.VDMMessage;
import org.overture.pog.obligation.ProofObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.util.PogUtil.PogResult;
import org.overture.util.Base64;
import org.overture.test.framework.ResultTestCase;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;
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
				
		if(expected.size() != actual.size())
		{
			System.out.println("The expected number of generated POs differs from what was actually generated.");
			return false;
		}
		
		boolean testPasssed = true;
		
		for (String string : actual) {
			
			/*
			 * The order of the POs is not assumed to be the same
			 */
			if(!expected.contains(string))
			{
				/*
				 * String permutations are also accepted as valid POs. This will be the case
				 * when the parameter order is different
				 */
				if(!PogTestHelper.containsPermutation(string, expected))
				{
					
					/*
					 * A valid PO may still exist. Sometimes PO parameters differ in name
					 * considering two equivalent POs. For example, a parameter may be called
					 * "any24" for an expected PO, while it is referred to as "any32" in another.
					 * Currently this is dealt with by considering whether the closest match
					 * passes a tolerance check.
					 */
					StringComparison comp = PogTestHelper.findClosestMatch(string, expected);
					
					if(comp == null || !PogTestHelper.ToleranceCheckPassed(comp))
					{
						testPasssed = false;
						
						System.out.println("\nNo equivalent PO found Deviation:" + comp.getDistLengthRatio());
						System.out.println();
						System.out.println("Actual  PO: " +  comp.getActual());
						System.out.println("Closest PO: " + comp.getResultStr());
						System.out.println("\n");
					}
				}
			}
		}
		
		return testPasssed;
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
