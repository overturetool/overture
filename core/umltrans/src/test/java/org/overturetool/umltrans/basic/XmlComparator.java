package org.overturetool.umltrans.basic;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlComparator
{

	PrintWriter printer = new PrintWriter(System.out);
	int matchedAttributes = 0;
	int matchedElements = 0;
	List<Node> location = new Vector<Node>();
	Node lastNotMatchedAttribute = null;
	String errorMessage = "";
	List<String> ignoreAttributeValueNames = new Vector<String>();

	public boolean compare(File testResultFile, File expectedResultFile)
			throws ParserConfigurationException, Exception
	{
		initialize();

		// We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(testResultFile);
		Document resultDoc = docBuilder.parse(expectedResultFile);

		return checkElements(doc, resultDoc);

	}

	public boolean compare(Document testResultFileDoc,
			Document expectedResultDoc) throws Exception
	{
		initialize();
		return checkElements(testResultFileDoc, expectedResultDoc);

	}

	public String getTrace()
	{
		return printNodes(location);
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setOutput(PrintWriter printer)
	{
		this.printer = printer;
	}
	
	public void addIgnoreAttributeValueName(String name)
	{
		this.ignoreAttributeValueNames.add(name);
	}

	private void initialize()
	{
		matchedAttributes = 0;
		matchedElements = 0;
		location.clear();
		lastNotMatchedAttribute = null;
		errorMessage = "";
	}

	private boolean checkElements(Document doc, Document resultDoc)
			throws Exception
	{

		matchedAttributes = 0;
		matchedElements = 0;
		Long beginTime = System.currentTimeMillis();
		boolean result = check(doc.getChildNodes().item(0),
				resultDoc.getChildNodes().item(0));

		printDebug("Completed in "
				+ (double) (System.currentTimeMillis() - beginTime) / 1000
				+ " secs");

		if (!result)
		{
			printDebug("Trace: \n" + printNodes(location));
			printDebug();

			if (lastNotMatchedAttribute == null)
				errorMessage = "<"
						+ location.get(location.size() - 1).getNodeName() + ">";
			else
			{
				location.add(lastNotMatchedAttribute); // add the last attribute to the location to make it show up in the trace at the propper location
				errorMessage ="Attribute not matched: "+ lastNotMatchedAttribute.toString();
			}
			printDebug("FAIL on: " + errorMessage);
		} else
		{
			printDebug();
			printDebug("OK matched: elements = " + matchedElements
					+ " attributes = " + matchedAttributes);
		}
		return result;
	}

	private boolean check(Node testNode, Node resultNode) throws Exception
	{
		lastNotMatchedAttribute = null;
		printDebug();
		printDebug("Checking: " + printElment(testNode));
		if (testNode.getNodeType() == Node.ELEMENT_NODE)
			if (!testNode.getNodeName().equals(resultNode.getNodeName())
					|| !check(testNode.getAttributes(),
							resultNode.getAttributes()))
			{
				printDebug("No match: " + printElment(resultNode));
				return false;
			} else
			{
				printDebug("Match   : " + printElment(resultNode));
				for (Node n : getNodeList(testNode.getChildNodes()))
				{

					if (!n.getNodeName().startsWith("#text"))
					{
						enter(n);
						boolean found = false;

						for (Node resultN : getNodeList(resultNode.getChildNodes()))
						{
							if (n.getNodeName().equals(resultN.getNodeName()))
							{
								found = check(n, resultN);
								if (found)
									break;
							}
						}

						if (!found)
							return false;
						exit();
					}
				}
			}
		matchedElements++;
		return true;
	}

	private boolean check(NamedNodeMap testAttributes,
			NamedNodeMap resultAttributes) throws Exception
	{

		if (testAttributes == null && resultAttributes == null
				|| testAttributes.getLength() == 0
				&& resultAttributes.getLength() == 0)
		{
			matchedAttributes++;
			return true;
		}
		for (int i = 0; i < testAttributes.getLength(); i++)
		{
			Node attribute = testAttributes.item(i);

			if (ignoreAttributeValueNames.contains(attribute.getNodeName()))
				continue;

			enter(attribute);
			if (resultAttributes.getNamedItem(attribute.getNodeName()) == null)
			{
				lastNotMatchedAttribute = location.get(location.size() - 1);
				exit();
				return false;
			} else

			if (!resultAttributes.getNamedItem(attribute.getNodeName())
					.getNodeValue()
					.equals(attribute.getNodeValue()))
			{
				lastNotMatchedAttribute = location.get(location.size() - 1);
				exit();
				return false;
			}
			exit();
		}
		matchedAttributes++;
		return true;
	}

	private List<Node> getNodeList(NodeList nodes)
	{
		List<Node> list = new Vector<Node>();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			list.add(nodes.item(i));

		}
		return list;
	}

	private void enter(Node node)
	{
		location.add(node);
	}

	private void exit()
	{
		location.remove(location.size() - 1);
	}

	private static String printNodes(List<Node> nodes)
	{
		int indent = 0;
		boolean first = true;
		StringBuffer sb = new StringBuffer();
		for (Node n : nodes)
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				if (!first)
				{
					sb.append(">\n");
					indent++;
				}
				first = false;
				for (int i = 0; i < indent; i++)
				{
					sb.append(" ");
				}
				sb.append("<" + n.getNodeName());

				for (int i = 0; i < n.getAttributes().getLength(); i++)
				{
					Node att = n.getAttributes().item(i);

					if (att.getNodeType() == Node.ATTRIBUTE_NODE)
					{
						sb.append(" " + att.toString());
					}
				}

			} else if (n.getNodeType() == Node.ATTRIBUTE_NODE)
			{
				sb.append(" " + n.toString());
			}
		}
		return sb.toString();
	}

	public static String printElment(Node n)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<" + n.getNodeName());

		for (int i = 0; i < n.getAttributes().getLength(); i++)
		{
			Node att = n.getAttributes().item(i);

			if (att.getNodeType() == Node.ATTRIBUTE_NODE)
			{
				sb.append(" " + att.toString());
			}
		}
		sb.append(">");

		return sb.toString();
	}

	private void printDebug(String message)
	{
		if (printer != null)
		{
			printer.println(message);
			printer.flush();
		}
	}

	private void printDebug()
	{
		if (printer != null)
		{
			printer.println();
			printer.flush();
		}
	}
}
