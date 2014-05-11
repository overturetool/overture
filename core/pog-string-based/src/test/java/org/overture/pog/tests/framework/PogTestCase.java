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

import org.overture.ast.lex.LexLocation;
import org.overture.parser.messages.VDMMessage;
import org.overture.pog.obligation.ProofObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.util.PogUtil.PogResult;
import org.overture.test.framework.Properties;
import org.overture.test.framework.ResultTestCase;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;
import org.overture.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class PogTestCase extends ResultTestCase<ProofObligationList>
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

	public void encodeResult(ProofObligationList result, Document doc,
			Element resultElement)
	{
		for (ProofObligation po : result)
		{
			Element message = doc.createElement("po");
			message.setAttribute("resource", file.getName());
			message.setAttribute("number", new Integer(po.number).toString());
			message.setAttribute("message", po.toString());
			message.setAttribute("column", po.location.getStartPos() + "");
			message.setAttribute("line", po.location.getStartLine() + "");
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

	public ProofObligationList decodeResult(Node node)
	{
		ProofObligationList list = new ProofObligationList();

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
						list.add((ProofObligation) fromString(nodeType));
					} catch (Exception e)
					{
						fail("Not able to decode object stored result");
					}
			}
		}
		return list;
	}

	@Override
	protected boolean assertEqualResults(ProofObligationList expected,
			ProofObligationList actual)
	{

		if (expected == null || actual == null
				|| expected.size() != actual.size())
		{
			return false;
		}

		List<String> expectedList = new Vector<String>();
		List<String> actualList = new Vector<String>();

		for (ProofObligation po : expected)
		{
			expectedList.add(po.toString());
		}

		int i = 0;
		for (ProofObligation po : actual)
		{
			i++;
			if(i==3)
			{
//				continue;
			}
			actualList.add(po.toString());
			
		}

		return assertEqualResults(expectedList, actualList);

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
						System.out.println("Actual  PO: " + comp.getActual());
						System.out.println("Closest PO: " + comp.getResultStr());
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
	protected static Result<ProofObligationList> convert(
			@SuppressWarnings("rawtypes") PogResult result)
	{
		if (result.result == null)
		{
			return new Result<ProofObligationList>(result.result, convert(result.typeCheckResult.warnings), convert(result.typeCheckResult.errors));
		}
		return new Result<ProofObligationList>(result.result, convert(result.warnings), convert(result.errors));
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
