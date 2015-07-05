package org.overture.interpreter.runtime;

import java.io.File;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MCDCReport {
	private Document doc;
	private Element bodyElement;
	
	public MCDCReport() {
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.doc = db.newDocument();
		Element html = doc.createElement("html");
		html.setAttribute("lang", "en");
		doc.appendChild(html);
		
		Element head= doc.createElement("head");
		html.appendChild(head);
		
		Element title = doc.createElement("title");
		title.setTextContent("MC/DC Report");
		head.appendChild(title);
		
		Element meta = doc.createElement("meta");
		meta.setAttribute("name", "viewport");
		meta.setAttribute("content", "width=device-width, initial-scale=1");
		head.appendChild(meta);
		
		Element link = doc.createElement("link");
		link.setAttribute("rel", "stylesheet");
		link.setAttribute("href", "http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css");
		meta.appendChild(link);
		
		Element script1= doc.createElement("script");
		script1.setAttribute("src", "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js");
		link.appendChild(script1);
		
		Element script2= doc.createElement("script");
		script2.setAttribute("src", "http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js");
		link.appendChild(script2);
		
		bodyElement = doc.createElement("body");
		html.appendChild(bodyElement);
		
		Element h1 = doc.createElement("h1");
		h1.setTextContent("MC/DC Report");
		bodyElement.appendChild(h1);
		
		
		
	}
	
	public void addFile(String filename, float coverage){
		Element div = doc.createElement("div");
		div.setAttribute("class", "container");
		bodyElement.appendChild(div);
		
		Element h2 = doc.createElement("h2");
		div.appendChild(h2);
		
		Element anchor = doc.createElement("a");
		anchor.setTextContent("File "+filename);
		anchor.setAttribute("href", filename+".html");
		h2.appendChild(anchor);
		
		Element div2 = doc.createElement("div");
		div2.setAttribute("class", "progress");
		div.appendChild(div2);
		
		Element div3 = doc.createElement("div");
		div3.setAttribute("class", "progress-bar progress-bar-success");
		div3.setAttribute("role", "progressbar");
		div3.setAttribute("style", "width:"+String.valueOf(coverage)+"%");
		div3.setTextContent(String.valueOf(coverage)+"% Required tests covered");
		div2.appendChild(div3);
		
		Element div4 = doc.createElement("div");
		div4.setAttribute("class", "progress-bar progress-bar-danger");
		div4.setAttribute("role", "progressbar");
		div4.setAttribute("style", "width:"+String.valueOf(100.0-coverage)+"%");
		div4.setTextContent(String.valueOf(100.0-coverage)+"% Required tests untested");
		div2.appendChild(div3);
	}
	
	public void saveReportHTML(File coverage, String filename) {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(coverage.getPath()
				+ File.separator + filename + "report.html"));
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
/*

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Bootstrap Example</title>
        <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
                <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
                    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    </head>
    <body>
        
        <div class="container">
            <h2>Source File A MC/DC Report</h2>
            <h3>Date: 30-05-2015, 19:50</h3>
            
            <p>If Statement A</p> 
            <div class="progress">
                <div class="progress-bar progress-bar-success" role="progressbar" style="width:20%">
                    20% Required test cases covered
                </div>
                <div class="progress-bar progress-bar-danger" role="progressbar" style="width:80%">
                </div>
            </div>
       
            
            
        </div>
        
    </body>
</html>
*/