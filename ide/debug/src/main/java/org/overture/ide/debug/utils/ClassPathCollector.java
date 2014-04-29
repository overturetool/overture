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
			String... bundleId)
	{
		return getClassPath(project, bundleId, new String[] {});
	}

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

	public static String toCpCliArgument(Collection<? extends String> entries)
	{
		if (entries.size() > 0)
		{
			StringBuffer classPath = new StringBuffer(" ");
			for (String cp : new HashSet<String>(entries))// remove dublicates
			{
				if (cp == null)
				{
					continue;
				}
				classPath.append(toPlatformPath(cp));
				classPath.append(getCpSeperator());
			}
			classPath.deleteCharAt(classPath.length() - 1);
			return classPath.toString().trim();

		}
		return "";
	}

	private static String getCpSeperator()
	{
		if (isWindowsPlatform())
		{
			return ";";
		} else
		{
			return ":";
		}
	}

	public static boolean isWindowsPlatform()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	protected static String toPlatformPath(String path)
	{
		if (isWindowsPlatform())
		{
			return "\"" + path + "\"";
		} else
		{
			return path.replace(" ", "\\ ");
		}
	}
}
