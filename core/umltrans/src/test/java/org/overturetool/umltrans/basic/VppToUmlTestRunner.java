package org.overturetool.umltrans.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLTestCase;
import org.overturetool.umltrans.Main.MainClass;

public class VppToUmlTestRunner
{
	String inFile;
	String exFile;

	public VppToUmlTestRunner(String folder, String inputFile)
	{

		setFiles(folder, inputFile);

	}

	public VppToUmlTestRunner(String inputFile)
	{

		setFiles("", inputFile);

	}

	private void setFiles(String folder, String inputFile)
	{
		inFile = folder + File.separatorChar + inputFile;
		File file = new File(inFile);
		exFile = folder + File.separatorChar
				+ file.getName().substring(0, file.getName().length() - 4)
				+ ".xml";

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
				+ "TestResults"+ File.separatorChar+"vppToUml");
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
		return base.getAbsolutePath() + File.separatorChar + "vppmodels"
				+ File.separatorChar + fileName;
	}

	private static String getPathUmlModels(File base, String fileName)
	{
		return base.getAbsolutePath() + File.separatorChar + "umlmodels"
				+ File.separatorChar + fileName;
	}

	public void test(XMLTestCase tc) throws Exception
	{
		runToUml(inFile, exFile, tc);
		// You have to have one to run all the JUnit tests in a package :-)
	}



	private void runToUml(String inputFile, String expectedOutputFile,
			XMLTestCase tc) throws Exception
	{
		setUp();
		// startTimer();
		String input = getPathVdmModels(inputFolder, inputFile);
		String tmpName = new File(inputFile).getName().substring(
				0,
				new File(inputFile).getName().length() - 4);
		
		int index = inputFile.lastIndexOf(File.separatorChar);
		File folder = outputFolder;
		if(index>0)
		{
			folder =new File(folder, inputFile.substring(0,index)+File.separatorChar);
			folder.mkdirs();
		}
		String output = getPath(folder, tmpName + ".xml");

		String expectedOutput = getPathUmlModels(
				inputFolder,
				expectedOutputFile);
		// startTimer();
		MainClass.main(new String[] { "-uml",
				"-output", output, input });

//		Assert.assertTrue(
//				"Expected output does not match: " + getMethodName(3),
//				FileCompare.compareXml(output, expectedOutput));
		
		String expectedXML = readFileAsString(expectedOutput);
		String objectAsXML = readFileAsString(output);
		// ...set up some object here and serialize its state into
		// our test String...
		tc.assertXMLEqual(expectedXML, objectAsXML);
		
		// System.out.println(getMethodName(3)+" done in "
		// + (double) (getElapsed() - begin) / 1000 + " secs.");

	}
	
	/**
	 * @param filePath
	 *            the name of the file to open. Not sure if it can accept URLs
	 *            or just filenames. Path handling could be better, and buffer
	 *            sizes are hardcoded
	 */
	private static String readFileAsString(String filePath)
			throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}
}
