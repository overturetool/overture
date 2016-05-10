/*
 * #%~
 * The VDM Type Checker
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
			tests = new LinkedList<>();
		}

		Collection<Object[]> actualTests = new LinkedList<>();
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
