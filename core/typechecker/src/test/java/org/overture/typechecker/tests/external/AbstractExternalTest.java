package org.overture.typechecker.tests.external;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.overture.ast.lex.Dialect;
import org.overture.typechecker.tests.framework.CommonTypeCheckerTest;
import org.overture.typechecker.tests.utils.TestSourceFinder;

public abstract class AbstractExternalTest extends CommonTypeCheckerTest
{

	private String storeLocationPart;

	public AbstractExternalTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file, String storeLocationPart)
	{
		super(dialect, file, suiteName, testSuiteRoot);
		this.storeLocationPart = storeLocationPart;
	}

	/**
	 * method to extract test cases
	 * 
	 * @param externalSuiteName
	 * @param inputRelativePathPart
	 * @param dialect
	 * @param extension
	 * @return
	 */
	public static Collection<Object[]> getData(String externalSuiteName,
			String inputRelativePathPart, Dialect dialect, String extension)
	{
		String name = externalSuiteName;
		File root = getBasePath(inputRelativePathPart);

		Collection<Object[]> tests = null;
		if (root != null && root.exists())
		{
			tests = TestSourceFinder.createTestCompleteFile(dialect, name, root.getAbsolutePath(), extension);
		} else
		{
			tests = new LinkedList<Object[]>();
		}

		Collection<Object[]> actualTests = new LinkedList<Object[]>();
		for (Object[] objects : tests)
		{
			Object[] temp = objects.clone();
			Object[] array = new Object[temp.length + 1];
			System.arraycopy(temp, 0, array, 0, temp.length);
			array[temp.length] = externalSuiteName;
			actualTests.add(array);
		}

		return actualTests;
	}

	public static File getBasePath(String string)
	{
		String path = System.getProperty("externalTestsPath");
		if (path != null)
		{
			File f = new File(new File(path), string);
			return f;
		} else
		{
			System.out.println("ExternalTestsPath not found");
			return null;
		}
	}

	protected File getStorageLocation()
	{
		String actualPath = file.getParentFile().getAbsolutePath();

		String divertedPath = actualPath.substring(testSuiteRoot.getAbsolutePath().length());

		File divertedFullPath = new File(new File(("src/test/resources/" + storeLocationPart).replace('/', File.separatorChar)), divertedPath.replace('/', File.separatorChar));
		return divertedFullPath;
	}

	protected File getInputLocation()
	{
		return file.getParentFile();
	}

}
