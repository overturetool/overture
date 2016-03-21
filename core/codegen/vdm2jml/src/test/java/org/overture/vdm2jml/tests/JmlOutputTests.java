package org.overture.vdm2jml.tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2jml.JmlGenMain;
import org.overture.test.framework.Properties;
import org.overture.vdm2jml.tests.exec.JmlExecTestBase;
import org.overture.vdm2jml.tests.exec.JmlInvariantForExecTests;
import org.overture.vdm2jml.tests.util.TestUtil;

@RunWith(Parameterized.class)
public class JmlOutputTests extends JmlGenTestBase
{
	private static final String PROPERTY_ID = "output";

	public JmlOutputTests(File inputFile)
	{
		super(inputFile);
	}
	
	@Test
	public void run()
	{
		JmlGenMain.main(getJmlGenMainProcessArgs(genJavaFolder));
		
		try
		{
			configureResultGeneration();

			if (Properties.recordTestResults)
			{
				storeGeneratedJml();
			} else
			{
				checkGenJavaJml();
			}
		} finally
		{
			unconfigureResultGeneration();
		}
	}
	
	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		return TestUtil.collectVdmslFiles(GeneralUtils.getFilesRecursively(new File(JmlExecTestBase.TEST_RES_DYNAMIC_ANALYSIS_ROOT)));
	}
	
	protected void storeGeneratedJml()
	{
		for(File file : TestUtil.collectStoredJavaJmlFiles(getTestDataFolder()))
		{
			Assert.assertTrue("Problems deleting stored Java/JML file", file.exists() && file.delete());
		}
		
		try
		{
			List<File> filesToStore = TestUtil.collectGenJavaJmlFiles(genJavaFolder);
	
			File testFolder = getTestDataFolder();
	
			for (File file : filesToStore)
			{
				File javaFile = new File(testFolder, file.getName());
				FileUtils.copyFile(file, javaFile);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			Assert.assertTrue("Problems storing generated JML: "
					+ e.getMessage(), false);
		}
	}
	
	protected void checkGenJavaJml()
	{
		List<File> storedJavaJmlFiles = TestUtil.collectStoredJavaJmlFiles(getTestDataFolder());
		List<File> genJavaJmlFiles = TestUtil.collectGenJavaJmlFiles(genJavaFolder);
		
		Assert.assertTrue("The number of stored Java/JML files differs "
				+ "from the number of generated Java/JML files", storedJavaJmlFiles.size() == genJavaJmlFiles.size());
	
		Comparator<File> comp = new Comparator<File>() {
			
	        @Override
	        public int compare(File f1, File f2) {
	        	
	        	// Reverse paths because root folders differ
	        	String f1PathRev = new StringBuilder(f1.getAbsolutePath()).reverse().toString();
	        	String f2PathRev = new StringBuilder(f2.getAbsolutePath()).reverse().toString();
	        	
	        	return f1PathRev.compareTo(f2PathRev);
	        }
	    };
	    
	    Collections.sort(storedJavaJmlFiles, comp);
	    Collections.sort(genJavaJmlFiles, comp);
	    
	    for(int i = 0; i < storedJavaJmlFiles.size(); i++)
	    {
	    	try
			{
				String storedContent = GeneralUtils.readFromFile(storedJavaJmlFiles.get(i)).trim();
				String genContent = GeneralUtils.readFromFile(genJavaJmlFiles.get(i)).trim();
	
				Assert.assertEquals("Stored Java/JML is different from generared Java/JML", storedContent, genContent);
			} catch (IOException e)
			{
				Assert.fail("Problems comparing stored and generated Java/JML");
				e.printStackTrace();
			}
	    }
	}
	
	@Override
	public String[] getJmlGenMainProcessArgs(File outputFolder)
	{
		if (inputFile.getAbsolutePath().contains(JmlInvariantForExecTests.TEST_DIR))
		{
			String[] stdArgs = super.getJmlGenMainProcessArgs(outputFolder);
			String[] invariantForArg = new String[] { JmlGenMain.INVARIANT_FOR };

			return GeneralUtils.concat(stdArgs, invariantForArg);
		} else
		{
			return super.getJmlGenMainProcessArgs(outputFolder);
		}
	}

	@Override
	protected String getPropertyId()
	{
		return PROPERTY_ID;
	}
}
