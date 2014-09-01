package org.overture.interpreter.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.tests.utils.TestSourceFinder;

@RunWith(value = Parameterized.class)
public class ClassesRtClassicTest extends CommonInterpreterTest
{

	public ClassesRtClassicTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file)
	{
		super(dialect, file, suiteName, testSuiteRoot);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		String name = "Interpreter Class RT TestSuite";
		String root = "src\\test\\resources\\classesRTClassic";

		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_RT, name, root, "vpp", "vdmrt");
		return tests;
	}

	@Override
	protected String getPropertyId()
	{
		return "class-classic.rt";
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		Settings.release = Release.CLASSIC;
	}

	@Override
	protected File getEntryFile()
	{
		return new File(getStorageLocation(), file.getName().substring(0, file.getName().lastIndexOf('.'))
				+ ".entry");
	}

}
