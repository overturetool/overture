/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.tests.util;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.codegen.tests.exec.util.FileComparator;

public class TestUtils
{
	public static List<File> getTestInputFiles(File file)
	{
		List<File> files = new Vector<>();
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
	
	public static Collection<Object[]> collectFiles(String root)
	{
		List<File> testFiles = getTestInputFiles(new File(root));
		
		List<Object[]> testFilesPacked = new LinkedList<>();
		
		for(File file : testFiles)
		{
			testFilesPacked.add(new Object[]{file});
		}
		
		return testFilesPacked;
	}
}
