package org.overture.vdm2jml.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.vdm2jml.tests.util.TestUtil;

@RunWith(Parameterized.class)
public class JmlAtomicExecTests extends JmlExecTestBase
{
	public static final String TEST_DIR = JmlExecTestBase.TEST_RES_DYNAMIC_ANALYSIS_ROOT + "atomic";

	public static final String PROPERTY_ID = "atomic";
	
	public JmlAtomicExecTests(File inputFile)
	{
		super(inputFile);
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
