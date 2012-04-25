package org.overturetool.umltrans.basic;

import java.io.File;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

public abstract class UmlTestCore
{
	final String expected = "expected";
	final String source = "source";
	final String testBaseLocation = "src/test/resources/tests";
	final String testOutputBaseLocation = "target/tests/";

	String testName;
	String group = "";
	File sourceLocation = null;
	File expectedLocation = null;
	File testOutputLocation = null;

	public UmlTestCore(String testName) {
		this.testName = testName;
	}

	public UmlTestCore(String group, String testName) {
		this.testName = testName;
		this.group = group;
	}

	protected abstract boolean runTest() throws Exception;

	public boolean run() throws Exception
	{
		if (!setup())
			return false; // we could not setup the env
		return runTest();
	}

	private boolean setup()
	{
		try
		{
			File currentExecutingFile = new File(getClass().getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.getFile());

			Assert.assertTrue("Check that the path to the current executing file is ok",
					currentExecutingFile.exists());

			File projectLocation = currentExecutingFile.getParentFile()
					.getParentFile();

			File testRootLocation = new File(projectLocation,
					testBaseLocation.replace('/', File.separatorChar));

			Assert.assertTrue("Check for test root folder",
					testRootLocation.exists());

			if (group.length() > 0)
				testRootLocation = new File(testRootLocation, group);

			File testCaseRoot = new File(testRootLocation, testName);

			sourceLocation = new File(testCaseRoot, source);

			if (!sourceLocation.exists())
				sourceLocation.mkdirs(); // create source location if not exists
											// to make it easier to copy source

			expectedLocation = new File(testCaseRoot, expected);

			if (!expectedLocation.exists())
				expectedLocation.mkdirs(); // create expected result location if
											// not exists to make it easier to
											// copy expected results

			File outputTestRoot = new File(projectLocation,
					testOutputBaseLocation.replace('/', File.separatorChar));

			if (group.length() > 0)
				outputTestRoot = new File(outputTestRoot, group);

			testOutputLocation = new File(outputTestRoot, testName);

			testOutputLocation.mkdirs();
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected List<File> getSourceFiles(String extension)
	{
		return getFiles(sourceLocation, extension);
	}

	protected List<File> getExpectedFiles(String extension)
	{
		return getFiles(expectedLocation, extension);
	}

	protected List<File> getOutputFiles(String extension)
	{
		return getFiles(testOutputLocation, extension);
	}

	protected File getOutputLocation()
	{
		return testOutputLocation;
	}

	public String getTestName()
	{
		return testName;
	}

	private List<File> getFiles(File location, String extension)
	{
		List<File> files = new Vector<File>();
		for (File f : location.listFiles())
		{
			if (f.getName().endsWith(extension))
				files.add(f);
		}
		return files;
	}
}
