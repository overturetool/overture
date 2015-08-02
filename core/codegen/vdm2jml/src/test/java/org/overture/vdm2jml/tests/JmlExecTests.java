package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.utils.GeneralUtils;

@RunWith(Parameterized.class)
public class JmlExecTests extends JmlExecTestBase
{
	public static final String TEST_RES_DYNAMIC_ANALYSIS_ROOT = AnnotationTestsBase.TEST_RESOURCES_ROOT
			+ "dynamic_analysis" + File.separatorChar;

	public static final String PROPERTY_ID = "exec";
	
	public JmlExecTests(File inputFile)
	{
		super(inputFile);
		this.isTypeChecked = false;
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		File folder = new File(TEST_RES_DYNAMIC_ANALYSIS_ROOT);
		List<File> files = GeneralUtils.getFilesRecursively(folder);

		return collectVdmslFiles(files);
	}
	
	protected String getPropertyId()
	{
		return PROPERTY_ID;
	}
}
