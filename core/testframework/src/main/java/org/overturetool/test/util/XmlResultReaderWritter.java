package org.overturetool.test.util;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlResultReaderWritter {

	private File file;
	private Result result = null;
	private String type;

	public XmlResultReaderWritter() {	
	}
	
	public XmlResultReaderWritter(File file)
	{
		this.file = file;
	}
	
	public XmlResultReaderWritter(String path)
	{
		this(new File(path));
	}
	
	public void setResult(String type, Result result)
	{
		this.type = type;
		this.result = result;				
	}
	
	
	public void saveInXml() throws ParserConfigurationException, TransformerException
	{
		
		File resultFile = new File(file.getAbsoluteFile()+ ".result");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("testResult");
		doc.appendChild(rootElement);
		rootElement.setAttribute("type", type);
		
		createWarningsAndErrors(doc,rootElement);
		       
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(resultFile);
		//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

	}

	private void createWarningsAndErrors(Document doc, Element rootElement) {
		
		Set<IMessage> warnings = (Set<IMessage>) result.warnings;
		Set<IMessage> errors = (Set<IMessage>) result.errors;
		
		for (IMessage warning : warnings) {
			Element message = doc.createElement("message");
			message.setAttribute("messageType", "warning");
			message.setAttribute("resource", file.getName());
			message.setAttribute("number", new Integer(warning.getNumber()).toString());
			message.setAttribute("message", warning.getMessage());
			message.setAttribute("column",  new Integer(warning.getCol()).toString());
			message.setAttribute("line",  new Integer(warning.getLine()).toString());
			rootElement.appendChild(message);
		}
		
		for (IMessage warning : errors) {
			Element message = doc.createElement("message");
			message.setAttribute("messageType", "error");
			message.setAttribute("resource", file.getName());
			message.setAttribute("number", new Integer(warning.getNumber()).toString());
			message.setAttribute("message", warning.getMessage());
			message.setAttribute("column",  new Integer(warning.getCol()).toString());
			message.setAttribute("line",  new Integer(warning.getLine()).toString());
			rootElement.appendChild(message);
		}
	}	
	
	public void loadFromXml() throws ParserConfigurationException, SAXException, IOException {
		File resultFile = new File(file.getAbsoluteFile()+ ".result");
		Set<IMessage> warnings = new HashSet<IMessage>();
		Set<IMessage> errors = new HashSet<IMessage>();
		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(resultFile);
		doc.getDocumentElement().normalize();
		NodeList nodeLst = doc.getElementsByTagName("message");
		for(int i=0; i < nodeLst.getLength(); i++)
		{
			Node node = nodeLst.item(i);
			if(node.getAttributes().getNamedItem("messageType").getNodeValue().equals("error"))
			{
				convertNodeToMessage(errors,node);
			}
			else
			{
				convertNodeToMessage(warnings,node);
			}			
		}
		result = new Result<String>(file.getName(), warnings, errors);
	}

	private void convertNodeToMessage(Set<IMessage> set, Node node) {

		NamedNodeMap nnm = node.getAttributes();
		String resource = nnm.getNamedItem("resource").getNodeValue();
		IMessage m = new Message(resource,
				Integer.parseInt(nnm.getNamedItem("number").getNodeValue()),
				Integer.parseInt(nnm.getNamedItem("line").getNodeValue()),
				Integer.parseInt(nnm.getNamedItem("column").getNodeValue()),
				nnm.getNamedItem("message").getNodeValue()
				);
		set.add(m);
	}
	
}
