/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.overture.ide.core.utility.ClasspathUtils;

public class ClassPathCollector
{


	public static List<String> getClassPath(IProject project,
			String[] bundleIds, String... additionalCpEntries)
	{
		// List<String> commandList = new Vector<String>();
		List<String> entries = new Vector<String>();
		// get the bundled class path of the debugger
		ClasspathUtils.collectClasspath(bundleIds, entries);
		// add custom properties file vdmj.properties
		entries.addAll(Arrays.asList(additionalCpEntries));

		return entries;
	}
	
	/**
	 * Creates a class path string from the entries using the path.seperator
	 * @param entries
	 * @return the class path string
	 */
	public static String toCpEnvString(Collection<? extends String> entries)
	{
		if (entries.size() > 0)
		{
			StringBuffer classPath = new StringBuffer("");
			for (String cp : new HashSet<String>(entries))// remove dublicates
			{
				if (cp == null)
				{
					continue;
				}
				classPath.append(cp);
				classPath.append(System.getProperty("path.separator"));
			}
			classPath.deleteCharAt(classPath.length() - 1);
			return classPath.toString().trim();

		}
		return "";
	}

}
