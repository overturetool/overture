package org.overturetool.test.framework;

import java.io.File;

public abstract class TestResourcesResultTestCase<R> extends ResultTestCase<R>
{
	protected final File testSuiteRoot;
	protected final String suiteName;

	public TestResourcesResultTestCase()
	{
		super();
		testSuiteRoot = null;
		suiteName = null;
	}

	public TestResourcesResultTestCase(File file)
	{
		super(file);
		testSuiteRoot = null;
		suiteName = null;
	}

	public TestResourcesResultTestCase(String name, String content)
	{
		super(name, content);
		testSuiteRoot = null;
		suiteName = null;
	}

	public TestResourcesResultTestCase(File file, String suiteName,
			File testSuiteRoot)
	{
		super(file);
		this.testSuiteRoot = testSuiteRoot;
		this.suiteName = suiteName;
	}

	@Override
	protected File createResultFile(String filename)
	{
		new File(getResultDirectoryPath()).mkdirs();
		return getResultFile(filename);
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(new File(getResultDirectoryPath()), filename);
	}

	private String getResultDirectoryPath()
	{
		// first try choice 1
		if (testSuiteRoot != null)
		{
			String tmp = ("src/test/resources/" + suiteName + "/" + file.getAbsolutePath().substring(testSuiteRoot.getAbsolutePath().length() + 1)).replace('\\', '/').replace('/', File.separatorChar).replaceAll(" ", "");
			return tmp;
		}

		String tmp = file.getParentFile().getAbsolutePath();
		tmp = tmp.replace('\\', '/');
		if (tmp.startsWith("/") || tmp.contains(":"))
		{
			tmp = tmp.substring(tmp.indexOf('/') + 1);
		}
		return ("src/test/resources/" + tmp).replace('/', File.separatorChar).replaceAll(" ", "");
	}

}
