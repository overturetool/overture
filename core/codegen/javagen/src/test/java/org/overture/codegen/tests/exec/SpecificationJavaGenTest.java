package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.base.JavaGenTestBase;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.tests.output.SpecificationOutputTest;
import org.overture.config.Release;

@RunWith(value = Parameterized.class)
public class SpecificationJavaGenTest extends JavaGenTestBase
{
	public SpecificationJavaGenTest(String name, File vdmSpec,
			TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(SpecificationOutputTest.ROOT), new TestHandler(Release.VDM_10, Dialect.VDM_PP));
	}

	@Override
	protected String getPropertyId()
	{
		return "specification";
	}
}
