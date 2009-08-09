package org.overturetool.umltrans;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.overturetool.umltrans.basic.VppToUmlTestRunner;

public class VppToUmlTypesTest extends XMLTestCase
{
	
	// set the JAXP factories to use the Xerces parser
	// - declare to throw Exception as if this fails then all the tests will
	// fail, and JUnit copes with these Exceptions for us
	public void setUp() throws Exception
	{
		XMLUnit.setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		// this next line is strictly not required - if no test parser is
		// explicitly specified then the same factory class will be used for
		// both test and control
		XMLUnit.setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

		XMLUnit.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
		XMLUnit.setTransformerFactory("org.apache.xalan.processor.TransformerFactoryImpl");
	}
	
	public VppToUmlTypesTest()
	{
		super("VDM to UML - Types Test");
	}



	public void test_expert() throws Exception
	{
		VppToUmlTestRunner tc = new VppToUmlTestRunner("types",
				"expert.vpp");
		tc.test(this);
	}

	public void test_string() throws Exception
	{
		VppToUmlTestRunner tc = new VppToUmlTestRunner("types",
				"string.vpp");
		tc.test(this);
	}

	
	
}
