package org.overturetool.umltrans.basic;

import java.io.File;

import junit.framework.Assert;

import org.overturetool.umltrans.Main.CmdLineProcesser;

public class ToUmlTestHelper extends UmlTestCore
{

	public ToUmlTestHelper(String testName) {
		super(testName);
	}
	public ToUmlTestHelper(String group,String testName) {
		super(group,testName);
	}

	@Override
	protected boolean runTest() throws Exception
	{
		Assert.assertTrue("No input  source files found", getSourceFiles(".vpp").size()>0);
		
			CmdLineProcesser.toUml(new File(getOutputLocation(),getTestName()+".xml"), getSourceFiles(".vpp"));
			if (getExpectedFiles(".xml").size() > 0)
			{
				File expectedFile = getExpectedFiles(".xml").get(0);
				File result = getOutputFiles(".xml").get(0);
				XmlComparator comparator = new XmlComparator();
				// add attributes which should only be present but not value checked
				comparator.addIgnoreAttributeValueName("xmi:id");
				comparator.addIgnoreAttributeValueName("isNavigable");
				//comparator.addIgnoreAttributeValueName("name");
				comparator.addIgnoreAttributeValueName("isActive");
				comparator.addIgnoreAttributeValueName("xmi:idref");
				comparator.addIgnoreAttributeValueName("visibility");
				
				comparator.setOutput(null);
				boolean testResult = comparator.compare(result, expectedFile);
				
				Assert.assertTrue(comparator.getErrorMessage()+"\n"+ comparator.getTrace(),testResult);
				
				return testResult;
			}else
				Assert.assertFalse("No expected result file was found",true);
		
		return false;
	}

}
