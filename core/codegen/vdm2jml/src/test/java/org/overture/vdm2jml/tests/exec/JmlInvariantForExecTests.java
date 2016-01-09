package org.overture.vdm2jml.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2jml.JmlGenMain;
import org.overture.vdm2jml.tests.util.TestUtil;

@RunWith(Parameterized.class)
public class JmlInvariantForExecTests extends JmlExecTestBase
{
	public static final String TEST_DIR = JmlExecTestBase.TEST_RES_DYNAMIC_ANALYSIS_ROOT + "invariant_for";

	public static final String PROPERTY_ID = "invariant_for";
	
	public JmlInvariantForExecTests(File inputFile)
	{
		super(inputFile);
	}
	
	@Override
	public String[] getJmlGenMainProcessArgs(File outputFolder)
	{
		String[] stdArgs = super.getJmlGenMainProcessArgs(outputFolder);
		String[] invariantForArg = new String[]{JmlGenMain.INVARIANT_FOR};
		
		return GeneralUtils.concat(stdArgs, invariantForArg);
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		return TestUtil.collectVdmslFiles(GeneralUtils.getFilesRecursively(new File(TEST_DIR)));
	}
	
	protected String getPropertyId()
	{
		return PROPERTY_ID;
	}
}
