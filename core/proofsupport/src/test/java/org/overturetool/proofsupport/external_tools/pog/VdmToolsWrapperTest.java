package org.overturetool.proofsupport.external_tools.pog;

import java.io.File;

import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;

public class VdmToolsWrapperTest extends AutomaticProofSystemTestCase {
	
	protected void setUp() throws Exception {
		super.setUp();

		// remove previously generated files
		removePreviousTestsData();
	}

	private void removePreviousTestsData() throws Exception {
		String model1FileName = sorterModel + POG_FILE_EXTENSION;
		String model2FileName = doSortModel + POG_FILE_EXTENSION;
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
		String[] vdmFiles = new String[]{ doSortModel, sorterModel };
		String expected = doSortModel + POG_FILE_EXTENSION;

		VdmToolsWrapper vdmTools = new VdmToolsWrapper(vppdeExecutable);
		String actual = vdmTools.generatePogFile(vdmFiles);

		File pogFile = new File(expected);
		
		assertTrue(pogFile.exists());
		assertEquals(expected, actual.trim());
	}
}
