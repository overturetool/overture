/*
 * #%~
 * Overture Testing Framework
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
package org.overture.core.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * Input/Result handler for the test framework. Should be used in parametric constructors of test cases. Provides paths
 * for test inputs (typically VDM sources) and results (JSON files)
 * 
 * @author ldc
 */
public class PathsProvider
{

	/** The Constant RESULT_EXTENSION. */
	public final static String RESULT_EXTENSION = ".result";

	/** Path to the results folder for external test inputs */
	private final static String RESULTS_EXTERNAL = "src/test/resources/external";

	/** The Constant VDM_EXTENSION_REGEX. */
	public final static String VDM_EXTENSION_REGEX = "((.*)\\.vdm(pp|rt|sl))|(.*(PP|SL|RT))";

	private final static String EXTERNAL_VDM_EXTENSION_REGEX = "(.*)\\.(vdm|vpp)";

	/**
	 * Processes (recursively) a folder of source inputs. The source input files must have one of the
	 * three VDM extensions (,vdmsl, .vdmpp, .vdmrt). <br>
	 * <br>
	 * ex: Test.vdmsl, Test.vdmsl.RESULT
	 * 
	 * @param root
	 *            the path(s) to the root(s) folder of the test inputs
	 * @return the a collection of test model file and result paths in the form of {filename ,filepath} arrays
	 */
	public static Collection<Object[]> computePathsNoResultFiles(String... root)
	{
		File dir;
		Collection<Object[]> r = searchForFilesNoResult(new File(root[0]));

		for (int i = 1; i < root.length; i++)
		{
			dir = new File(root[i]);
			r.addAll(searchForFilesNoResult(dir));
		}
		return r;
	}

	/**
	 * Processes (recursively) a folder of source inputs and result files. The source input files must have one of the
	 * three VDM extensions (,vdmsl, .vdmpp, .vdmrt). The result files must be in the same folder as the respective
	 * inputs and have the same name including the extension but also have .RESULT as a second extension. <br>
	 * <br>
	 * ex: Test.vdmsl, Test.vdmsl.RESULT
	 * 
	 * @param root
	 *            the path(s) to the root(s) folder of the test inputs
	 * @return the a collection of test model file and result paths in the form of {filename ,filepath, resultpath}
	 *         arrays
	 */
	public static Collection<Object[]> computePaths(String... root)
	{
		File dir;
		Collection<Object[]> r = searchForFiles(new File(root[0]));

		for (int i = 1; i < root.length; i++)
		{
			dir = new File(root[i]);
			r.addAll(searchForFiles(dir));
		}
		return r;
	}

	/**
	 * Processes (recursively) a folder with external test inputs. <br>
	 * <br>
	 * The results for these tests are <b>not</b> stored in the external directory but under each plugin's
	 * <code>RESULTS_EXTERNAL</code> folder. If you use these tests, please keep that folder pure.
	 * 
	 * @param root
	 *            the root folder of the external tests
	 * @return
	 */
	public static Collection<Object[]> computeExternalPaths(String root)
	{
		Collection<Object[]> r = externalFiles(new File(root));

		return r;
	}

	private static Collection<Object[]> externalFiles(File dir)
	{
		Collection<File> files = FileUtils.listFiles(dir, new RegexFileFilter(EXTERNAL_VDM_EXTENSION_REGEX), DirectoryFileFilter.DIRECTORY);

		List<Object[]> paths = new Vector<Object[]>();

		for (File file : files)
		{
			if (!(file.getPath().contains("sltest")
					|| file.getPath().contains("pptest") || file.getPath().contains("rttest")))
			{
				continue;
			}

			paths.add(new Object[] {
					file.getName(),
					file.getPath(),
					(RESULTS_EXTERNAL
							+ file.getPath().substring(dir.getPath().length(), file.getPath().length()) + RESULT_EXTENSION).replace('\\', '/').replace('/', File.separatorChar) });

		}

		return paths;
	}

	private static Collection<Object[]> searchForFiles(File file)
	{
		return searchForFilesOption(file, true);
	}

	private static Collection<Object[]> searchForFilesNoResult(File file)
	{
		return searchForFilesOption(file, false);
	}

	private static Collection<Object[]> searchForFilesOption(File dir,
			boolean resultFile)
	{
		Collection<File> files = FileUtils.listFiles(dir, new RegexFileFilter(VDM_EXTENSION_REGEX), DirectoryFileFilter.DIRECTORY);

		List<Object[]> paths = new Vector<Object[]>();

		for (File file : files)
		{
			if (resultFile)
			{
				paths.add(buildResultArray(file));
			} else
			{
				paths.add(buildNoResultArray(file));
			}
		}

		return paths;
	}

	private static Object[] buildResultArray(File file)
	{
		return new Object[] { file.getName(), file.getPath(),
				file.getPath() + RESULT_EXTENSION };
	}

	private static Object[] buildNoResultArray(File file)
	{
		return new Object[] { file.getName(), file.getPath() };
	}

}
