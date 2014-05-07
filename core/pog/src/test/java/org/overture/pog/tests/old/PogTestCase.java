package org.overture.pog.tests.old;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.LexLocation;
import org.overture.parser.messages.VDMMessage;
import org.overture.pog.obligation.ProofObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.tests.old.TestPogUtil.PogResult;
import org.overture.test.framework.Properties;
import org.overture.test.framework.ResultTestCase;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;
import org.overture.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class PogTestCase extends ResultTestCase<List<String>>
{
	private static final String TESTS_PO_PROPERTY_PREFIX = "tests.po.override.";

	public PogTestCase()
	{
		super();

	}

	public PogTestCase(File file)
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

	public void encodeResult(List<String> result, Document doc,
			Element resultElement)
	{
		for (String po : result)
		{
			Element message = doc.createElement("po");
			message.setAttribute("message", po);
			resultElement.appendChild(message);
		}

	}

	
	public List<String> decodeResult(Node node)
	{
		List<String> list = new LinkedList<String>();

		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			Node cn = node.getChildNodes().item(i);
			if (cn.getNodeType() == Node.ELEMENT_NODE
					&& cn.getNodeName().equals("po"))
			{
				String nodeType = cn.getAttributes().getNamedItem("object").getNodeValue();
				if (nodeType != null && !nodeType.isEmpty())
					try
					{
						String msg= cn.getAttributes().getNamedItem("message").getNodeValue();
						
						list.add(msg);
					} catch (Exception e)
					{
						fail("Not able to decode object stored result");
					}
			}
		}
		return list;
	}



	protected boolean assertEqualResults(List<String> expected,
			List<String> actual)
	{

		if (expected.size() != actual.size())
		{
			System.out.print("The expected number of generated POs differs from what was actually generated in: "
					+ file.getName());
			System.out.println(" #Actual: " + actual.size() + ". #Expected: "
					+ expected.size());
			return false;
		}

		boolean testPasssed = true;

		for (String string : actual)
		{

			/*
			 * The order of the POs is not assumed to be the same
			 */
			if (!expected.contains(string))
			{
				/*
				 * String permutations are also accepted as valid POs. This will be the case when the parameter order is
				 * different
				 */
				
				if (!PogTestHelper.containsPermutation(string, expected))
				{

					/*
					 * A valid PO may still exist. Sometimes PO parameters differ in name considering two equivalent
					 * POs. For example, a parameter may be called "any24" for an expected PO, while it is referred to
					 * as "any32" in another. Currently this is dealt with by considering whether the closest match
					 * passes a tolerance check.
					 */
					StringComparison comp = PogTestHelper.findClosestMatch(string, expected);

					if (comp == null
							|| !PogTestHelper.ToleranceCheckPassed(comp))
					{
						testPasssed = false;

						System.out.println("\nNo equivalent PO found Deviation:"
								+ comp.getDistLengthRatio());
						System.out.println();
						System.out.println("Actual  PO: " + comp.getActual().replaceAll("\\s+", " "));
						System.out.println("Closest PO: " + comp.getResultStr().replaceAll("\\s+",  " "));
						System.out.println("\n");
					}
				}
			}
		}

		return testPasssed;
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
	protected static Result<List<String>> convert(
			@SuppressWarnings("rawtypes") PogResult result)
	{
		if (result.result == null)
		{
			return new Result<List<String>>(result.result, convert(result.typeCheckResult.warnings), convert(result.typeCheckResult.errors));
		}
		return new Result<List<String>>(result.result, convert(result.warnings), convert(result.errors));
	}

	public static List<IMessage> convert(List<? extends VDMMessage> messages)
	{
		List<IMessage> testMessages = new Vector<IMessage>();

		for (VDMMessage msg : messages)
		{
			testMessages.add(new Message(msg.location.getFile().getName(), msg.number, msg.location.getStartLine(), msg.location.getStartPos(), msg.message));
		}

		return testMessages;
	}
	
	
	
	protected void configureResultGeneration()
	{
		LexLocation.absoluteToStringLocation = false;
		if (System.getProperty(TESTS_PO_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
				&& System.getProperty(TESTS_PO_PROPERTY_PREFIX
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
