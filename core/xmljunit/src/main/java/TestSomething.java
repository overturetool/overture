//import junit.framework.TestSuite;
//
//import org.custommonkey.xmlunit.Diff;
//import org.custommonkey.xmlunit.Transform;
//import org.custommonkey.xmlunit.XMLTestCase;
//import org.custommonkey.xmlunit.XMLUnit;
//
//public class TestSomething extends XMLTestCase
//{
//	// standard JUnit style constructor
//	public TestSomething(String name)
//	{
//		super(name);
//	}
//
//	// standard JUnit style method
//	public static TestSuite suite()
//	{
//		return new TestSuite(TestSomething.class);
//	}
//
//	// set the JAXP factories to use the Xerces parser
//	// - declare to throw Exception as if this fails then all the tests will
//	// fail, and JUnit copes with these Exceptions for us
//	public void setUp() throws Exception
//	{
//		XMLUnit.setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
//		// this next line is strictly not required - if no test parser is
//		// explicitly specified then the same factory class will be used for
//		// both test and control
//		XMLUnit.setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
//
//		XMLUnit.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
//		XMLUnit.setTransformerFactory("org.apache.xalan.processor.TransformerFactoryImpl");
//	}
//
//	public void testObjectAsXML() throws Exception
//	{
//		String expectedXML = "<A>HEj</A>";
//		String objectAsXML = "<A>HEj</A>";
//		// ...set up some object here and serialize its state into
//		// our test String...
//		assertXMLEqual(expectedXML, objectAsXML);
//	}
//
//	public void testTransformToFormatB() throws Exception
//	{
//		String expectedFormatB = "....";
//		String formatA = "....";
//		String transformXSLT = "....";
//		Transform formatAToFormatB = new Transform(formatA, transformXSLT);
//		assertXMLEqual(new Diff(expectedFormatB, formatAToFormatB), true);
//	}
//
//	public void testIsValidAfterTransform() throws Exception
//	{
//		String incomingMessage = "....";
//		String toSourceSystemXSLT = "....";
//		Transform transform = new Transform(incomingMessage, toSourceSystemXSLT);
//		assertXMLValid(transform.getResultString());
//	}
//
//	public void testXpaths() throws Exception
//	{
//		String ukCustomerContactPhoneNos = "//customer[@country='UK']/contact/phone";
//		String customerExtract1 = "....";
//		String customerExtract2 = "....";
//		assertXpathsNotEqual(
//				ukCustomerContactPhoneNos,
//				customerExtract1,
//				ukCustomerContactPhoneNos,
//				customerExtract2);
//	}
//
//	public void testXpathValues() throws Exception
//	{
//		String firstListItem = "/html/body/div[@id='myList']/h1/ol/li[1]";
//		String secondListItem = "/html/body/div[@id='myList']/h1/ol/li[2]";
//		String myHtmlPage = "....";
//		assertXpathValuesNotEqual(firstListItem, secondListItem, myHtmlPage);
//	}
//
//	public void testSpecificXpath() throws Exception
//	{
//		String todaysTop10 = "count(//single[@topTen='true'])";
//		String playlist = "....";
//		assertXpathEvaluatesTo("10", todaysTop10, playlist);
//
//	}
//
//}
