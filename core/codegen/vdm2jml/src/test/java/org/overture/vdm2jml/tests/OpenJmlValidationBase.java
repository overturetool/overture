package org.overture.vdm2jml.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Assume;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.tests.exec.util.ProcessResult;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2jml.JmlGenMain;

abstract public class OpenJmlValidationBase extends JmlGenTestBase
{
	private static final String SKIPPING_A_SPECIFICATION_CLAUSE_FILTER_MSG = "Skipping a specification clause ";

	public static final String VDMSL_FILE_EXT = ".vdmsl";

	public static final String OPENJML_ENV_VAR = "OPENJML";

	public static final String OPEN_JML = "openjml.jar";
	
	public static final String JML_RUNTIME = "jmlruntime.jar";

	public static final String TEST_EXEC_LIB_FOLDER_PATH = TEST_EXEC_FOLDER_PATH
			+ File.separatorChar + "lib";
	
	public static final String CODEGEN_RUNTIME = TEST_EXEC_LIB_FOLDER_PATH + File.separatorChar
			+ "codegen-runtime.jar";
	
	public static final String VDM_TO_JML_RUNTIME = TEST_EXEC_LIB_FOLDER_PATH + File.separatorChar
			+ "vdm2jml-runtime.jar";
	
	public static final String EXEC_PROPERTY = "tests.vdm2jml.openjml";

	public static final int EXIT_OK = 0;

	protected File openJml;
	protected File jmlRuntime;
	protected File cgRuntime;
	protected File vdm2jmlRuntime;
	public OpenJmlValidationBase(File inputFile)
	{
		super(inputFile);
		this.cgRuntime = new File(CODEGEN_RUNTIME);
		this.vdm2jmlRuntime = new File(VDM_TO_JML_RUNTIME);
		
		setOpenJmlTools();
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
	
	public static void assumeFile(String fileName, File file)
	{
		Assume.assumeTrue("Could not find " + fileName, file != null
				&& file.exists());
	}

	public void assertNoProcessErrors(ProcessResult processResult)
	{
		Assert.assertTrue("Expected test to exit without any errors. Got: "
				+ processResult.getOutput(), processResult.getExitCode() == OpenJmlValidationBase.EXIT_OK);
	}

	public ProcessResult runOpenJmlProcess()
	{
		beforeRunningOpenJmlProcess();

		try
		{
			return runProcess(getProcessArgs());
		} catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
			Assume.assumeTrue("Problems launching OpenJML", false);
			return null;
		}
	}

	public static ProcessResult runProcess(String[] openJmlArgs) throws IOException, InterruptedException
	{
		String s;
		Process p;
		int exitCode = 1;
		StringBuilder processOutput = new StringBuilder();
		
		ProcessBuilder pb = new ProcessBuilder(openJmlArgs);

		p = pb.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

		while ((s = br.readLine()) != null)
		{
			if (!mustFilter(s))
			{
				processOutput.append(s).append('\n');
			}
		}

		br.close();

		br = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		while ((s = br.readLine()) != null)
		{
			processOutput.append(s).append('\n');
		}

		br.close();

		exitCode = p.waitFor();

		if (VERBOSE)
		{
			Logger.getLog().println(processOutput.toString());
			Logger.getLog().println("Exit value: " + p.exitValue());
		}

		p.destroy();
		
		return new ProcessResult(exitCode, processOutput);
	}

	private static boolean mustFilter(String s)
	{
		return s.startsWith(SKIPPING_A_SPECIFICATION_CLAUSE_FILTER_MSG);
	}
	
	public void clearCodeFolder()
	{
		// Just make sure that the folder we are using is empty
		GeneralUtils.deleteFolderContents(genJavaFolder, true);

	}
	
	protected void generateJavaJml()
	{
		JmlGenMain.main(getJmlGenMainProcessArgs(genJavaFolder));
	}
	
	abstract public void beforeRunningOpenJmlProcess();

	abstract public String[] getProcessArgs();
}