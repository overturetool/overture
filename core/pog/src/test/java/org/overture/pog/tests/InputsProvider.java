package org.overture.pog.tests;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.Assert;

/**
 * The class InputsProvider provides inputs for parameterized tests.
 * 
 * @author ldc
 * 
 */
public class InputsProvider {

	private final static String RESULT_EXTENSION = ".RESULT";
	private final static String EXAMPLES_ROOT = "src/test/resources/allexamples";
	private final static String BUG_REG_ROOT = "src/test/resources/bug-regression";

	public static Collection<Object[]> bugRegs() {
		return files(BUG_REG_ROOT);
	}

	public static Collection<Object[]> allExamples() {
		File dir = new File(EXAMPLES_ROOT);

		return makePaths(dir);
	}

	private static Collection<Object[]> makePaths(File dir) {
		Collection<File> files = FileUtils.listFiles(dir, new RegexFileFilter(
				"(.*)\\.vdm(pp|rt|sl)"), DirectoryFileFilter.DIRECTORY);

		List<Object[]> paths = new Vector<Object[]>();

		for (File file : files) {
			paths.add(new Object[] { file.getPath() });
		}

		return paths;
	}

	/**
	 * Provides the base test input and result files off a given folder.
	 * 
	 * @param foldername
	 *            the folder with tests. No nesting allowed.
	 * @return A list of test file paths represented as {<folder>, <input>,
	 *         <result>}
	 */
	public static Collection<Object[]> files(String foldername) {

		List<Object[]> paths = new Vector<Object[]>();
		File folder = new File(foldername);

		// Don't grab result files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !(name.toUpperCase().endsWith(RESULT_EXTENSION));
			}
		};

		// Get the files that match the filter
		String[] children = folder.list(filter);

		if (children == null) {
			// This should not happen
			Assert.fail("Could not find test files in " + foldername);
		} else {
			for (int i = 0; i < children.length; i++) {
				// Get paths trio
				paths.add(new Object[] { folder.getPath() + File.separatorChar,
						children[i], children[i] + RESULT_EXTENSION });
			}
		}

		return paths;

	}

}
