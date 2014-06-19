package org.overture.pog.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * The class InputsProvider provides inputs for parameterized tests.
 * 
 * @author ldc
 */
public class InputsProvider
{

	private final static String RESULT_EXTENSION = ".RESULT";
	private final static String EXAMPLES_ROOT = "src/test/resources/allexamples";
	private final static String BUG_REG_ROOT = "src/test/resources/bug-regression";
	private final static String OLD_ROOT = "src/test/resources/old/adapted";
	private final static String NOPAREN_ROOT = "src/test/resources/old/noparen";
	private final static String MICRO_ROOT = "src/test/resources/micro";

	/**
	 * Provides a collection of paths for the adpated test files from the old string pog.
	 * 
	 * @return A list of test file and result paths in the form {modelpath, resultpath}
	 */
	public static Collection<Object[]> old()
	{
		File dir = new File(OLD_ROOT);
		Collection<Object[]> r = makePathsWResults(dir);
		dir = new File(NOPAREN_ROOT);
		r.addAll(makePathsWResults(dir));
		return r;
	}

	/**
	 * Provides a collection of paths for the micro model test files for the new pog.
	 * 
	 * @return A list of test file and result paths in the form {modelpath, resultpath}
	 */
	public static Collection<Object[]> basics()
	{
		File dir = new File(MICRO_ROOT);
		return makePathsWResults(dir);
	}

	/**
	 * Provides a collection of paths for the bug regression tests
	 * 
	 * @return A list of test file and result paths
	 */
	public static Collection<Object[]> bugRegs()
	{
		File dir = new File(BUG_REG_ROOT);
		return makePathsWResults(dir);
	}

	/**
	 * Provides a collection of paths for the bundled overture examples
	 * 
	 * @param foldername
	 *            the folder with tests. No nesting allowed.
	 * @return A list of test file paths
	 */
	public static Collection<Object[]> allExamples()
	{
		File dir = new File(EXAMPLES_ROOT);

		return makePaths(dir);
	}

	private static Collection<Object[]> makePathsWResults(File dir)
	{
		Collection<File> files = FileUtils.listFiles(dir, new RegexFileFilter("(.*)\\.vdm(pp|rt|sl)"), DirectoryFileFilter.DIRECTORY);

		List<Object[]> paths = new Vector<Object[]>();

		for (File file : files)
		{
			paths.add(new Object[] { file.getPath(),
					file.getPath() + RESULT_EXTENSION });
		}

		return paths;
	}

	private static Collection<Object[]> makePaths(File dir)
	{
		Collection<File> files = FileUtils.listFiles(dir, new RegexFileFilter("(.*)\\.vdm(pp|rt|sl)"), DirectoryFileFilter.DIRECTORY);

		List<Object[]> paths = new Vector<Object[]>();

		for (File file : files)
		{
			paths.add(new Object[] { file.getPath() });
		}

		return paths;
	}

}
