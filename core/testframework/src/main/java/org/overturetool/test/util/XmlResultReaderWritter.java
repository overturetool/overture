package org.overturetool.test.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
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
	
//	public XmlResultReaderWritter(String path)
//	{
//		this(new File(path));
//	}
	
	public void setResult(String type, Result result)
	{
		this.type = type;
		this.result = result;				
	}
	
	
	public void saveInXml() throws ParserConfigurationException, TransformerException
	{
		
		
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
		StreamResult result = new StreamResult(file);
		//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

	}

	private void createWarningsAndErrors(Document doc, Element rootElement) {
		
		List<IMessage> warnings = getResult().warnings;
		List<IMessage> errors =  getResult().errors;
		List<IMessage> obligations = getResult().proofObligations;

		if (warnings != null) {
			for (IMessage warning : warnings) {
				Element message = doc.createElement("message");
				message.setAttribute("messageType", "warning");
				message.setAttribute("resource", file.getName());
				message.setAttribute("number",
						new Integer(warning.getNumber()).toString());
				message.setAttribute("message", warning.getMessage());
				message.setAttribute("column",
						new Integer(warning.getCol()).toString());
				message.setAttribute("line",
						new Integer(warning.getLine()).toString());
				rootElement.appendChild(message);
			}
		}
		
		if (errors != null) {
			for (IMessage warning : errors) {
				Element message = doc.createElement("message");
				message.setAttribute("messageType", "error");
				message.setAttribute("resource", file.getName());
				message.setAttribute("number",
						new Integer(warning.getNumber()).toString());
				message.setAttribute("message", warning.getMessage());
				message.setAttribute("column",
						new Integer(warning.getCol()).toString());
				message.setAttribute("line",
						new Integer(warning.getLine()).toString());
				rootElement.appendChild(message);
			}
		}
		
		if (obligations != null) {
			for (IMessage po : obligations) {
				Element message = doc.createElement("message");
				message.setAttribute("messageType", "error");
				message.setAttribute("resource", file.getName());
				message.setAttribute("number",
						new Integer(po.getNumber()).toString());
				message.setAttribute("message", po.getMessage());
				message.setAttribute("column",
						new Integer(po.getCol()).toString());
				message.setAttribute("line",
						new Integer(po.getLine()).toString());
				rootElement.appendChild(message);
			}
		}
	}	
	
	public boolean loadFromXml(){
		//File resultFile = new File(file.getAbsoluteFile()+ ".result");
		List<IMessage> warnings = new Vector<IMessage>();
		List<IMessage> errors = new Vector<IMessage>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();

			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("message");
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node node = nodeLst.item(i);
				if (node.getAttributes().getNamedItem("messageType")
						.getNodeValue().equals("error")) {
					convertNodeToMessage(errors, node);
				} else {
					convertNodeToMessage(warnings, node);
				}
			}
			setResult(new Result<String>(file.getName(), warnings, errors,null));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void convertNodeToMessage(List<IMessage> set, Node node) {

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

	public Result<String> getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public List<IMessage> getWarnings() {
		if(result != null)
		{
			return result.warnings;
		}
		
		return null;
	}

	public List<IMessage> getErrors() {
		if(result != null)
		{
			return result.errors;
		}
		
		return null;
	}

	
}
