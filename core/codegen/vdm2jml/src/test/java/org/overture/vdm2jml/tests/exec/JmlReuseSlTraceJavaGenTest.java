package org.overture.vdm2jml.tests.exec;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.config.Release;
import org.overture.vdm2jml.tests.JmlReuseSlTraceOutputTest;

@RunWith(value = Parameterized.class)
public class JmlReuseSlTraceJavaGenTest extends JmlSlJavaGenTestBase
{
	/* OpenJML crashes on these tests although the tests are correct.. */
	private static final List<String> SKIPPED = Arrays.asList("StateOtherModule.vdmsl");
	
	public JmlReuseSlTraceJavaGenTest(String name, File vdmSpec,
			TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}
	
	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(JmlReuseSlTraceOutputTest.ROOT),new JmlTraceTestHandler(Release.VDM_10, Dialect.VDM_SL));
	}
	
	public void assumeTest()
	{
		Assume.assumeFalse("OpenJML crashes on this test although it is correct", SKIPPED.contains(file.getName()));
	};
	
	@Override
	protected String getPropertyId()
	{
		return "reuse";
	}
}
