package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.vdm2jml.JmlGenMain;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.test.framework.Properties;
import org.overture.typechecker.util.TypeCheckerUtil;

abstract public class JmlGenTestBase
{
	public static final boolean VERBOSE = false;

	public static final String VDM_LIB_PATH = "src" + File.separatorChar
			+ "test" + File.separatorChar + "resources" + File.separatorChar
			+ "lib";

	public static final String TESTS_VDM2JML_PROPERTY_PREFIX = "tests.vdm2jml.override.";

	public static final String TEST_EXEC_FOLDER_PATH = "target"
			+ File.separatorChar + "jml";

	public static final String GEN_JAVA_FOLDER = TEST_EXEC_FOLDER_PATH
			+ File.separatorChar + "code";

	protected File inputFile;
	protected File genJavaFolder;

	public JmlGenTestBase(File inputFile)
	{
		super();
		this.inputFile = inputFile;
		this.genJavaFolder = new File(GEN_JAVA_FOLDER, getTestName());
	}

	public void validateModel() {

		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;

		TypeCheckerUtil.TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(inputFile);

		if(!result.parserResult.errors.isEmpty())
		{
			Assert.fail("Input model contains parse errors:\n" + result.parserResult.errors);
		}

		if(!result.parserResult.errors.isEmpty())
		{
			Assert.fail("Input model contains type errors:\n" + result.errors);
		}
	}

	protected File getTestDataFolder()
	{
		return inputFile.getParentFile();
	}

	public String getTestName()
	{
		int dotIdx = inputFile.getName().indexOf('.');

		Assert.assertTrue("Got unexpected file name '" + inputFile.getName()
				+ "'", dotIdx > 0);

		return inputFile.getName().substring(0, dotIdx);
	}

	protected void unconfigureResultGeneration()
	{
		Properties.recordTestResults = false;
	}

	abstract protected String getPropertyId();

	protected void configureResultGeneration()
	{
		LexLocation.absoluteToStringLocation = false;
		if (System.getProperty(TESTS_VDM2JML_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
						&& System.getProperty(TESTS_VDM2JML_PROPERTY_PREFIX
								+ getPropertyId()) != null)
		{
			Properties.recordTestResults = true;
		}
	}

	public String[] getJmlGenMainProcessArgs(File outputFolder)
	{
		List<String> javaCgArgs = new LinkedList<String>();

		javaCgArgs.add(inputFile.getAbsolutePath());
		if (VERBOSE)
		{
			javaCgArgs.add(JmlGenMain.PRINT_ARG);
		}
		javaCgArgs.add(JmlGenMain.OUTPUT_ARG);
		javaCgArgs.add(outputFolder.getAbsolutePath());
		javaCgArgs.add(JmlGenMain.FOLDER_ARG);
		javaCgArgs.add(new File(VDM_LIB_PATH).getAbsolutePath());
		javaCgArgs.add(JmlGenMain.NO_TRACE);
		// javaCgArgs.add(JmlGenMain.REPORT_VIOLATIONS_ARG);

		return javaCgArgs.toArray(new String[] {});
	}
}