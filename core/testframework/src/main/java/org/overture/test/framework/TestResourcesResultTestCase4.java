/*
 * #%~
 * Test Framework for Overture
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.test.framework;

import java.io.File;

public abstract class TestResourcesResultTestCase4<R> extends
		ResultTestCase4<R>
{
	protected final File testSuiteRoot;
	protected final String suiteName;

	public TestResourcesResultTestCase4()
	{
		super();
		testSuiteRoot = null;
		suiteName = null;
	}

	public TestResourcesResultTestCase4(File file)
	{
		super(file);
		testSuiteRoot = null;
		suiteName = null;
	}

	// public TestResourcesResultTestCase4(File rootSource,String name, String content)
	// {
	// super(rootSource,name, content);
	// testSuiteRoot = null;
	// suiteName = null;
	// }

	public TestResourcesResultTestCase4(File file, String suiteName,
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
			String base = file.getParentFile().getAbsolutePath();
			String suiteBase = testSuiteRoot.getAbsolutePath();
			if (base.startsWith(suiteBase)
					&& base.length() != suiteBase.length())
			{
				String tmp = ("src/test/resources/" + suiteName + "/" + base.substring(suiteBase.length() + 1)).replace('\\', '/').replace('/', File.separatorChar).replaceAll(" ", "");
				return tmp;
			}
		}

		String tmp = file.getParentFile().getPath();
		tmp = tmp.replace('\\', '/');
		if (tmp.startsWith("/") || tmp.contains(":"))
		{
			tmp = tmp.substring(tmp.indexOf('/') + 1);
		}
		return tmp.replace('/', File.separatorChar).replaceAll(" ", "");
	}

}
