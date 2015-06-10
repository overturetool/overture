package org.overture.vdm2jml.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2jml.JmlGenMain;
import org.overture.vdm2jml.tests.util.ProcessResult;

abstract public class OpenJmlValidationBase
{
	private static final String VDM_LIB_PATH = "src" + File.separatorChar
			+ "test" + File.separatorChar + "resources" + File.separatorChar
			+ "lib";

	protected static final String VDMSL_FILE_EXT = ".vdmsl";

	public static final String OPENJML_ENV_VAR = "OPENJML";

	public static final String OPEN_JML = "openjml.jar";
	
	public static final String JML_RUNTIME = "jmlruntime.jar";

	public static final String TEST_EXEC_FOLDER_PATH = "target"
			+ File.separatorChar + "jml";

	public static final String GEN_JAVA_FOLDER = TEST_EXEC_FOLDER_PATH
			+ File.separatorChar + "code";

	public static final String CODEGEN_RUNTIME = TEST_EXEC_FOLDER_PATH
			+ File.separatorChar + "lib" + File.separatorChar
			+ "codegen-runtime.jar";

	public static final int EXIT_OK = 0;

	private static final boolean VERBOSE = false;

	protected File inputFile;

	protected File openJml;
	protected File jmlRuntime;
	protected File cgRuntime;
	protected File genJavaFolder;
	
	public OpenJmlValidationBase(File inputFile)
	{
		this.inputFile = inputFile;
		this.cgRuntime = new File(CODEGEN_RUNTIME);
		this.genJavaFolder = new File(GEN_JAVA_FOLDER, getTestName());
		
		setOpenJmlTools();
	}

	public String getTestName()
	{
		int dotIdx = inputFile.getName().indexOf('.');

		Assert.assertTrue("Got unexpected file name '" + inputFile.getName()
				+ "'", dotIdx > 0);

		return inputFile.getName().substring(0, dotIdx);
	}

	public void setOpenJmlTools()
	{
		String openJmlDir = System.getenv(OPENJML_ENV_VAR);

		openJml = new File(openJmlDir, OPEN_JML);
		jmlRuntime = new File(openJmlDir, JML_RUNTIME);
	}
	
	public void assumeOpenJml()
	{
		assumeFile(OPEN_JML, openJml);
	}

	public void assumeJmlRuntime()
	{
		assumeFile(JML_RUNTIME, jmlRuntime);
	}
	
	private void assumeFile(String fileName, File file)
	{
		Assume.assumeTrue("Could not find " + fileName, file != null
				&& file.exists());
	}

	public static Collection<Object[]> collectVdmslFiles(List<File> files)
	{
		List<Object[]> testInputFiles = new LinkedList<Object[]>();

		for (File f : files)
		{
			if (f.getName().endsWith(VDMSL_FILE_EXT))
			{
				testInputFiles.add(new Object[] { f });
			}
		}

		return testInputFiles;
	}
	
	public void assertNoProcessErrors(ProcessResult processResult)
	{
		Assert.assertTrue("Expected test to exit without any errors. Got: "
				+ processResult.getOutput(), processResult.getExitCode() == OpenJmlValidationBase.EXIT_OK);
	}

	public void codeGenerateInputFile()
	{
		List<String> javaCgArgs = new LinkedList<String>();
		
		javaCgArgs.add(inputFile.getAbsolutePath());
		if(VERBOSE)
		{
			javaCgArgs.add(JmlGenMain.PRINT_ARG);
		}
		javaCgArgs.add(JmlGenMain.OUTPUT_ARG);
		javaCgArgs.add(genJavaFolder.getAbsolutePath());
		javaCgArgs.add(JmlGenMain.FOLDER_ARG);
		javaCgArgs.add(new File(VDM_LIB_PATH).getAbsolutePath());
		//javaCgArgs.add(JmlGenMain.REPORT_VIOLATIONS_ARG);
		
		JmlGenMain.main(javaCgArgs.toArray(new String[]{}));
	}
	
	public ProcessResult runOpenJmlProcess()
	{
		beforeRunningOpenJmlProcess();

		String s;
		Process p;
		
		int exitCode = 1;
		StringBuilder openJmlOutput = new StringBuilder();
		
		try
		{
			String[] openJmlArgs = getProcessArgs();

			ProcessBuilder pb = new ProcessBuilder(openJmlArgs);

			p = pb.start();

			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while ((s = br.readLine()) != null)
			{
				openJmlOutput.append(s).append('\n');
			}

			br.close();

			br = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((s = br.readLine()) != null)
			{
				openJmlOutput.append(s).append('\n');
			}

			br.close();

			exitCode = p.waitFor();

			if (VERBOSE)
			{
				Logger.getLog().println(openJmlOutput.toString());
				Logger.getLog().println("Exit value: " + p.exitValue());
			}

			p.destroy();

		} catch (Exception e)
		{
			e.printStackTrace();
			Assume.assumeTrue("Problems launching OpenJML", false);
		}
		
		return new ProcessResult(exitCode, openJmlOutput);
	}
	
	public void clearCodeFolder()
	{
		// Just make sure that the folder we are using is empty
		GeneralUtils.deleteFolderContents(genJavaFolder, true);

	}

	abstract public void beforeRunningOpenJmlProcess();

	abstract public String[] getProcessArgs();
}