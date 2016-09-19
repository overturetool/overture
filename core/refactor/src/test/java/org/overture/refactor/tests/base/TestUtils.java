package org.overture.refactor.tests.base;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TestUtils {

	public static Collection<Object[]> collectFiles(String root)
	{
		List<File> testFiles = getTestInputFiles(new File(root));

		List<Object[]> testFilesPacked = new LinkedList<Object[]>();

		for (File file : testFiles)
		{
			testFilesPacked.add(new Object[] { file });
		}

		return testFilesPacked;
	}
	
	public static List<File> getTestInputFiles(File file)
	{
		List<File> files = new Vector<File>();
		for (File f : file.listFiles())
		{
			Collections.sort(files, new FileComparator());
			if (f.isDirectory())
			{
				files.addAll(getTestInputFiles(f));
			} else
			{
				String name = f.getName();

				if (name.endsWith(".vdmsl") || name.endsWith(".vdmpp")
						|| name.endsWith(".vdmrt") || name.endsWith("vpp")
						|| name.endsWith("vsl"))
				{
					files.add(f);
				}
			}
		}

		Collections.sort(files, new FileComparator());

		return files;
	}
}
