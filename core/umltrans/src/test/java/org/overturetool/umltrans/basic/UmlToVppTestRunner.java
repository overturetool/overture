package org.overturetool.umltrans.basic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.overturetool.umltrans.Main.FileCompare;
import org.overturetool.umltrans.Main.MainClass;

public class UmlToVppTestRunner
{
	String inFile;
	String exFile;

	public UmlToVppTestRunner(String folder, String inputFile)
	{

		setFiles(folder, inputFile);

	}

	public UmlToVppTestRunner(String inputFile)
	{

		setFiles("", inputFile);
		setUp();
	}

	private void setFiles(String folder, String inputFile)
	{
		if (folder.length() > 0)
			inFile = folder + File.separatorChar + inputFile;
		else
			inFile = inputFile;
		File file = new File(inFile);
		exFile = file.getName().substring(0, file.getName().length() - 4);
		if(folder.length()>0)
			exFile=folder + File.separatorChar + exFile;
			

	}

	File outputFolder;
	File inputFolder;

	public void setUp()
	{

		File f = new File(
				getClass().getProtectionDomain().getCodeSource().getLocation().getFile());

		Assert.assertTrue(f.exists());
		// System.out.println("current location detected: OK");

		inputFolder = new File(f.getParentFile().getParent()
				+ File.separatorChar + "src" + File.separatorChar + "test"
				+ File.separatorChar + "resources");
		// System.out.println("Setting input folder");
		Assert.assertTrue(inputFolder.exists());

		outputFolder = new File(f.getParent() + File.separatorChar
				+ "TestResults" + File.separatorChar + "umlToVpp");
		outputFolder.mkdirs();
		// System.out.println("Output folder set to: " + outputFolder);
		Assert.assertTrue(outputFolder.exists());
		// System.out.println("Setup done");

	}

	public static String getMethodName(final int depth)
	{

		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		// System.
		// out.println(ste[ste.length-depth].getClassName()+"#"+ste[ste.length-depth].getMethodName());
		return ste[depth].getMethodName();
	}

	private static String getPath(File base, String fileName)
	{
		return base.getAbsolutePath() + File.separatorChar + fileName;
	}

	private static String getPathVdmModels(File base, String fileName)
	{
		return base.getAbsolutePath() + File.separatorChar + "vppmodelsTransformed"
				+ File.separatorChar + fileName;
	}

	private static String getPathUmlModels(File base, String fileName)
	{
		return base.getAbsolutePath() + File.separatorChar + "umlmodels"
				+ File.separatorChar + fileName;
	}

	public void test(TestCase tc) throws Exception
	{
		runToUml(inFile,  exFile, tc);
		// You have to have one to run all the JUnit tests in a package :-)
	}

	private void runToUml(String inputFile, String expectedOutputDir,
			TestCase tc) throws Exception
	{
		setUp();
		// startTimer();
		String input = getPathUmlModels(inputFolder, inputFile);
		String tmpName = new File(inputFile).getName().substring(
				0,
				new File(inputFile).getName().length() - 4);

		int index = inputFile.lastIndexOf(File.separatorChar);
		File folder = outputFolder;
		if (index > 0)
		{
			folder = new File(folder, inputFile.substring(0, index)
					+ File.separatorChar);
			folder.mkdirs();
		}

		File outputFolder = new File(getPath(folder, tmpName));
		outputFolder.mkdirs();

		 MainClass.main(new String[] { "-vpp",
				"-output", outputFolder.getAbsolutePath(), input });

		compareResults(expectedOutputDir, outputFolder);
		
		
	}

	private void compareResults(String expectedOutputDir, File outputFolder)
	{
		List<File> expectedOutputFiles = getExpectedOutputFiles(expectedOutputDir);
		List<File> generatedFiles = getVppFilesFromFolder(outputFolder);
		
		Assert.assertEquals("Number of gennerated files do not match",expectedOutputFiles.size(), generatedFiles.size());
		
		for (File file : expectedOutputFiles)
		{
			File f = getFileByName(generatedFiles, file.getName());
			Assert.assertNotNull("Exspected output file ("+ file.getAbsolutePath()+") not found",f);
			try
			{
				Assert.assertTrue("Output file do not match exspected file ("+ file.getName()+")",FileCompare.compare(file, f));
			} catch (IOException e)
			{
				Assert.assertFalse("Error compairing files: "+e.getMessage(),true);
			}
		}
		
		
	}
	
	private File getFileByName(List<File> files, String name)
	{
		for (File file : files)
		{
			if(file.getName().equals(name))
				return file;
		}
		return null;
	}

	private List<File> getExpectedOutputFiles(String expectedOutputDir)
	{
		String expectedOutput = getPathVdmModels(inputFolder, expectedOutputDir);

		return getVppFilesFromFolder(new File(expectedOutput));
	}

	private List<File> getVppFilesFromFolder(File folder)
	{

		List<File> files = new Vector<File>();
		if (folder.isDirectory())
		{
			for (File file : folder.listFiles())
			{
				if (file.getAbsolutePath().endsWith(".vpp"))
					files.add(file);
			}

		}
		return files;
	}

//	/**
//	 * @param filePath
//	 *            the name of the file to open. Not sure if it can accept URLs
//	 *            or just filenames. Path handling could be better, and buffer
//	 *            sizes are hardcoded
//	 */
//	private static String readFileAsString(String filePath)
//			throws java.io.IOException
//	{
//		StringBuffer fileData = new StringBuffer(1000);
//		BufferedReader reader = new BufferedReader(new FileReader(filePath));
//		char[] buf = new char[1024];
//		int numRead = 0;
//		while ((numRead = reader.read(buf)) != -1)
//		{
//			fileData.append(buf, 0, numRead);
//		}
//		reader.close();
//		return fileData.toString();
//	}
}
