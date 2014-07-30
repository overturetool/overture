package ctruntime;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.overture.interpreter.traces.Verdict;
import org.overture.interpreter.traces.util.LazyTestSequence;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadXmlResult
{
	@Test
	public void test() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
	{
		System.out.println(read(new File("target/trace-output/T1/DEFAULT-T1.xml".replace('/', File.separatorChar))));
	}
	
	public class TraceResult
	{
		public String traceName;
		public List<TraceTest> tests = new Vector<TraceTest>();
	}
	
	
	public class TraceTest
	{
		public Integer no;
		public String test;
		public String result;
		public Verdict verdict;
	}
	
	public List<TraceResult> read(File file) throws SAXException, IOException,
			ParserConfigurationException, XPathExpressionException
	{
		List<TraceResult> results = new Vector<TraceResult>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(file);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
		
		XPathExpression expr = xpath.compile("//Trace");
		
		final NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		for (Node n : new NodeIterator(list))
		{
			final String traceName = n.getAttributes().getNamedItem("Name").getNodeValue();
			
			TraceResult tr = new TraceResult();
			results.add(tr);
			tr.traceName = traceName;
			
			expr = xpath.compile("Test");
			final NodeList tests = (NodeList) expr.evaluate(n, XPathConstants.NODESET);
			
			for (Node testNode : new NodeIterator(tests))
			{
				Integer testNo =Integer.parseInt( testNode.getAttributes().getNamedItem("No").getNodeValue());
				String test = testNode.getTextContent().trim();
			
				TraceTest tt = new TraceTest();
				tr.tests.add(tt);
				
				tt.no = testNo;
				tt.test = test;
				
				expr = xpath.compile("Result[@No='"+testNo+"']");
				
				
				final NodeList resultNodeList= (NodeList) expr.evaluate(n, XPathConstants.NODESET);
				
				final Node resultNode = resultNodeList.item(0);
				final String result = resultNode.getTextContent().trim();
				final String verdict = resultNode.getAttributes().getNamedItem("Verdict").getNodeValue();
				System.out.println(traceName+"("+testNo+"): "+test+" => "+result+ " Verdict: "+verdict);
				
				tt.result = result;
				tt.verdict = Verdict.valueOf(verdict);
				
			}
		}
		
		
		return results;
	}
}
