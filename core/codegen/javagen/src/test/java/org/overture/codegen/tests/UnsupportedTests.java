package org.overture.codegen.tests;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.util.modules.ModuleList;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.config.Release;
import org.overture.config.Settings;

@RunWith(Parameterized.class)
public class UnsupportedTests
{
	public static final String TEST_INPUT_FOLDER_PATH = "src"
			+ File.separatorChar + "test" + File.separatorChar + "resources"
			+ File.separatorChar + "unsupported";

	public static final String VDMSL_FILE_TEXT = ".vdmsl";

	private JavaCodeGen javaGen;

	private File testInputFile;

	public UnsupportedTests(File testInputFile)
	{
		this.testInputFile = testInputFile;
	}
	
	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		File folder = new File(TEST_INPUT_FOLDER_PATH);
		List<File> files = GeneralUtils.getFiles(folder);

		List<Object[]> testInputFiles = new LinkedList<Object[]>();

		for (File f : files)
		{
			if (f.getName().endsWith(VDMSL_FILE_TEXT))
			{
				testInputFiles.add(new Object[] { f });
			}
		}

		return testInputFiles;
	}

	@Before
	public void init()
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		this.javaGen = new JavaCodeGen();
	}

	@Test
	public void hello()
	{
		List<File> files = new LinkedList<File>();
		files.add(testInputFile);

		try
		{
			ModuleList ast = GeneralCodeGenUtils.consModuleList(files);

			GeneratedData genData = javaGen.generateJavaFromVdmModules(ast);

			Assert.assertTrue("Expected only a single module to be generated. Got: "
					+ genData.getClasses().size(), genData.getClasses().size() == 1);

			GeneratedModule mod = genData.getClasses().get(0);

			Assert.assertTrue("Expected no content to be generated", mod.getContent() == null);

			Assert.assertTrue("Expected a single unsupported construct to be reported. Got: "
					+ mod.getUnsupportedInIr().size(), mod.getUnsupportedInIr().size() == 1);

		} catch (AnalysisException e)
		{
			Assert.assertTrue("Could not parse VDM-SL model", false);
		} catch (UnsupportedModelingException e)
		{
			Assert.assertTrue("Unexpected problem encountered when trying to code generate VDM-SL model", false);
		}
	}
}
