package org.overturetool.umltrans.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.umltrans.XmlDoc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlParser
{
	public static XmlDoc Parse(String fileName) throws Exception
	{

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document docc = docBuilder.parse(new File(fileName));

		// normalize text representation
		docc.getDocumentElement().normalize();
		// System.out.println("Root element of the doc is " +
		// docc.getDocumentElement().getNodeName());

		XmlDoc doc = new XmlDoc();
		PrintNode(doc, docc.getDocumentElement());
		return doc;

	}

	public static void PrintNode(XmlDoc doc, Element e) throws CGException
	{
		String name = e.getNodeName();
		// System.out.println("ELEMENT: " + name);

		doc.StartE(name);
		PrintAttributes(doc, e);
		NodeList childern = e.getChildNodes();
		for (int i = 0; i < childern.getLength(); i++)
		{
			if (childern.item(i).getNodeType() == Node.TEXT_NODE && childern.item(i).getNodeValue().trim().length() > 0)
			{
				// System.out.print("DATA: " +
				// childern.item(i).getNodeValue().trim());
				doc.StartD(childern.item(i).getNodeValue().trim());
			}
			if (childern.item(i).getNodeType() == Node.ELEMENT_NODE)
				PrintNode(doc, (Element) childern.item(i));
		}
		doc.StopE(name);

	}

	public static void PrintAttributes(XmlDoc doc, Node e) throws CGException
	{

		NamedNodeMap atts = e.getAttributes();
		// if (atts.getLength() > 0)
		// System.out.println("ATTRIBUTES: ");
		for (int i = 0; i < atts.getLength(); i++)
		{
			String name = atts.item(i).getNodeName();
			String value = atts.item(i).getNodeValue();
			// System.out.println(" - " + name + ": " + value);
			doc.StartA(name, value);
		}
	}
}
