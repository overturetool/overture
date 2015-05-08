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
import org.overture.codegen.vdm2java.IJavaCodeGenConstants;
import org.overture.codegen.vdm2jml.JmlGenMain;

abstract public class OpenJmlValidationBase
{
	protected static final String VDMSL_FILE_EXT = ".vdmsl";

	public static final String OPENJML_ENV_VAR = "OPENJML";

	public static final String OPEN_JML = "openjml.jar";

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

	public OpenJmlValidationBase()
	{
		super();
	}

	public static File getOpenJml()
	{
		String openJmlDir = System.getenv(OPENJML_ENV_VAR);

		if (openJmlDir != null)
		{
			File file = new File(openJmlDir, OPEN_JML);

			if (file.exists())
			{
				return file;
			}
		}

		return null;
	}

	public String[] findJavaFilePathsRec(File srcCodeFolder)
	{
		List<File> files = GeneralUtils.getFilesRecursive(srcCodeFolder);

		List<String> javaFilePaths = new LinkedList<String>();

		for (File f : files)
		{
			if (f.getName().endsWith(IJavaCodeGenConstants.JAVA_FILE_EXTENSION))
			{
				javaFilePaths.add(f.getAbsolutePath());
			}
		}

		return javaFilePaths.toArray(new String[] {});
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

	public void tcFiles()
	{
		File openJml = getOpenJml();

		Assume.assumeTrue("Could not find OpenJML installation", openJml != null);

		int dotIdx = inputFile.getName().indexOf('.');

		Assert.assertTrue("Got unexpected file name '" + inputFile.getName()
				+ "'", dotIdx > 0);

		String inputFileNameNoExt = inputFile.getName().substring(0, dotIdx);
		File genJavaFolder = new File(GEN_JAVA_FOLDER, inputFileNameNoExt);

		// Just make sure that the folder we are using is empty
		GeneralUtils.deleteFolderContents(genJavaFolder, true);

		JmlGenMain.main(new String[] { inputFile.getAbsolutePath(),
				JmlGenMain.OUTPUT_ARG, genJavaFolder.getAbsolutePath() });

		String s;
		Process p;
		try
		{
			File cgRuntime = new File(CODEGEN_RUNTIME);

			String[] openJmlConfig = getOpenJmlConfig(openJml, cgRuntime);

			String[] javaFiles = findJavaFilePathsRec(genJavaFolder);

			String[] openJmlArgs = GeneralUtils.concat(openJmlConfig, javaFiles);

			// Arguments to run the OpenJML type checker on a set of Java files.
			// A requirement is to have OpenJML installed in $OPENJML
			// java
			// -jar
			// $OPENJML/openjml.jar
			// -classpath
			// "codegen-runtime.jar"
			// -rac
			// -racCompileToJavaAssert
			// -no-purityCheck
			// <javafiles>

			ProcessBuilder pb = new ProcessBuilder(openJmlArgs);

			p = pb.start();

			StringBuilder openJmlOutput = new StringBuilder();

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

			p.waitFor();

			Assert.assertTrue("OpenJML exiting with error:\n"
					+ openJmlOutput.toString(), p.exitValue() == EXIT_OK);

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
	}

	abstract public String[] getOpenJmlConfig(File openJml, File cgRuntime);
}