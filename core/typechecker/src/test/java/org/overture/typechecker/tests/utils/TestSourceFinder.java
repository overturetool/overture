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
package org.overture.typechecker.tests.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;

public class TestSourceFinder
{

	/**
	 * Utility method to create a test suite from files
	 * 
	 * @param name
	 *            the suite name
	 * @param testRootPath
	 *            the root folder to start the file search
	 * @param testCase
	 *            The test case class instantiated. It must have a constructor taking a {@link File}
	 * @param extensions
	 *            an array of accepted extensions. If none are given all files are accepted. Empty sequence for the
	 *            empty extension.
	 * @return a new test suite
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings({ "rawtypes" })
	protected static Collection<Object[]> createTestCompleteDirectory(
			Dialect dialect, String name, String testRootPath, Class testCase,
			String... extensions) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException
	{
		File testRoot = getFile(testRootPath.replace('/', File.separatorChar));

		Collection<Object[]> tests = new LinkedList<Object[]>();

		if (testRoot != null && testRoot.exists())
		{

			for (File file : testRoot.listFiles())
			{
				if (file.isDirectory())
				{
					tests.addAll(createDirectory(dialect, name, file));
				}
			}
		}
		return tests;

	}

	/**
	 * Utility method to create a test suite from files
	 * @param dialect 
	 * 
	 * @param name
	 *            the suite name
	 * @param testRootPath
	 *            the root folder to start the file search
	 * @param extensions
	 *            an array of accepted extensions. If none are given all files are accepted. Empty sequence for the
	 *            empty extension.
	 * @return a new test suite
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 */
	public static Collection<Object[]> createTestCompleteFile(Dialect dialect,
			String name, String testRootPath, String... extensions)

	{
		File testRoot = getFile(testRootPath);

		// TestSuite suite = new TestSourceFinder(name);
		Collection<Object[]> tests = new LinkedList<Object[]>();

		if (testRoot != null && testRoot.exists())
		{
			for (File file : testRoot.listFiles())
			{
				tests.addAll(createCompleteFile(dialect, name, file, testRoot, extensions));
			}
		}
		return tests;

	}

	private static Collection<Object[]> createCompleteFile(Dialect dialect,
			String suite, File file, File testRoot, String... extensions)
	{
		Collection<Object[]> tests = new LinkedList<Object[]>();

		if (file.getName().startsWith(".")
				|| !isNotAcceptedFile(file, Arrays.asList(extensions)))
		{
			return tests;
		}
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				tests.addAll(createCompleteFile(dialect, suite, f, testRoot, extensions));
			}
		} else
		{
			// System.out.println("Creating test for:" + file);
			// Object instance = null;
			// if (ctorCustom == null)
			// {
			// instance = ctor.newInstance(new Object[] { file });
			// } else
			// {
			// instance = ctorCustom.newInstance(new Object[] { file,
			// suite.getName(), testRoot });
			// }
			// suite.addTest((Test) instance);
			tests.add(new Object[] { dialect, file.getName(), testRoot, file });
		}

		return tests;

	}

	private static boolean isNotAcceptedFile(File file, List<String> extensions)
	{
		if (extensions == null || extensions.isEmpty() || file.isDirectory())
		{
			return true;
		}
		for (String ext : extensions)
		{
			if (ext.isEmpty() && !file.getName().contains("."))
			{
				return true;
			}
			if (file.getName().endsWith("." + ext))
			{
				return true;
			}
			// System.out.println("Skipping: "+file.getName());
		}
		return false;
	}

	private static Collection<Object[]> createDirectory(Dialect dialect,
			String suite, File file, String... extensions)
	{
		Collection<Object[]> tests = new LinkedList<Object[]>();

		if (file.getName().startsWith(".")
				|| !isNotAcceptedFile(file, Arrays.asList(extensions)))
		{
			return tests;
		}
		if (file.isDirectory())
		{
			// System.out.println("Creating test for:" + file);
			// Object instance = ctor.newInstance(new Object[] { file });
			// suite.addTest((Test) instance);
			tests.add(new Object[] { dialect, suite, null, file });
		}

		return tests;

	}

	/**
	 * Utility method to create a test suite from files
	 * 
	 * @param name
	 *            the suite name
	 * @param testRootPath
	 *            the root folder to start the file search
	 * @param testCase
	 *            The test case class instantiated. It must have a constructor taking a {@link File}, {@link String} and
	 *            {@link String}
	 * @param extensions
	 *            an array of accepted extensions. If none are given all files are accepted. Empty sequence for the
	 *            empty extension.
	 * @return a new test suite
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	protected static Collection<Object[]> createTestSingleLineFile(
			Dialect dialect, String name, String testRootPath,
			String... extensions) throws IOException
	{
		File testRoot = getFile(testRootPath);

		Collection<Object[]> tests = new LinkedList<Object[]>();

		if (testRoot != null && testRoot.exists())
		{
			for (File file : testRoot.listFiles())
			{
				if (file.getName().startsWith(".")
						|| isNotAcceptedFile(file, Arrays.asList(extensions)))// ||file.getName().endsWith("_results"))
				{
					continue;
				}
				List<String> lines = readFile(file);
				if (lines != null)
				{
					for (int i = 0; i < lines.size(); i++)
					{
						// Object instance = ctor.newInstance(new Object[] { file,
						// file.getName() + "_L" + i + "_" + lines.get(i),
						// lines.get(i) });
						// suite.addTest((Test) instance);
						System.err.println("not supported");
						tests.add(new Object[] { dialect,
								file.getName() + "_L" + i + "_" + lines.get(i),
								null, file });
					}
				}

			}
		}
		return tests;

	}

	protected static List<String> readFile(File file) throws IOException
	{
		List<String> lines = new Vector<String>();
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new FileReader(file));
			String text;

			// repeat until all lines is read
			while ((text = reader.readLine()) != null)
			{
				if (text.trim().length() > 0 && !text.trim().startsWith("//"))
				{
					lines.add(text);
				}
			}
			return lines;
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
			}
		}
	}

	protected static File getFile(String pathname)
	{
		return new File(pathname.replace('\\', File.separatorChar).replace('/', File.separatorChar));
	}
}
