package org.overturetool.proofsupport.external_tools.pog;

import java.io.File;

import junit.framework.TestCase;

import org.overturetool.proofsupport.test.TestSettings;

public class VdmToolsWrapperTest extends TestCase {
	
	private static final String pogExtension = ".pog";
	
	
	private static final  String VPPDE_BIN = TestSettings.getVppdeBinary();
	
	private static TestSettings settings = null;
	private static String testModel1 = null;
	private static String testModel2 = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		setUpPreferences();
		
		// remove previously generated files
		removePreviousTestsData();
	}
	
	private void setUpPreferences() throws Exception {
		settings = new TestSettings();
		testModel1 = settings.get(TestSettings.TEST_MODEL_1);
		testModel2 = settings.get(TestSettings.TEST_MODEL_2);
	}

	private void removePreviousTestsData() throws Exception {
		String model1FileName = testModel1 + pogExtension;
		String model2FileName = testModel2 + pogExtension;
		File testModel1Pog = new File(model1FileName);
		File testModel2Pog = new File(model2FileName);
		
		if(testModel1Pog.exists()) {
			if(!testModel1Pog.delete())
				throw new Exception("Can't remove file '" + model1FileName + "'.");
		}
		
		if(testModel2Pog.exists()) {
			if(!testModel2Pog.delete())
				throw new Exception("Can't remove file '" + model2FileName + "'.");
		}
	}

	public void testGeneratePogFile() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		String expected = testModel2 + ".pog";

		VdmToolsWrapper vdmTools = new VdmToolsWrapper(VPPDE_BIN);
		String actual = vdmTools.generatePogFile(vdmFiles);

		File pogFile = new File(testModel2 + pogExtension);
		
		assertTrue(pogFile.exists());
		assertEquals(expected, actual.trim());
	}

}
