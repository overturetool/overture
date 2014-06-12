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

//	public static String toCpCliArgument(Collection<? extends String> entries)
//	{
//		if (entries.size() > 0)
//		{
//			StringBuffer classPath = new StringBuffer(" ");
//			for (String cp : new HashSet<String>(entries))// remove dublicates
//			{
//				if (cp == null)
//				{
//					continue;
//				}
//				classPath.append(toPlatformPath(cp));
//				classPath.append(getCpSeperator());
//			}
//			classPath.deleteCharAt(classPath.length() - 1);
//			return classPath.toString().trim();
//
//		}
//		return "";
//	}
//
//	private static String getCpSeperator()
//	{
//		if (isWindowsPlatform())
//		{
//			return ";";
//		} else
//		{
//			return ":";
//		}
//	}
//
//	public static boolean isWindowsPlatform()
//	{
//		return System.getProperty("os.name").toLowerCase().contains("win");
//	}
//
//	public static boolean isMac()
//	{
//		return (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0);
//	}
//
//	protected static String toPlatformPath(String path)
//	{
//		if (isWindowsPlatform())
//		{
//			return "\"" + path + "\"";
//		} else if(isMac())
//		{
//			return path;// Bug: 255; Since this is used in a process builder Linux/Mac must not have this escape
//						// enabled:.replace(" ", "\\ ");
//		}else
//		{
//			return path.replace(" ", "\\ ");// Bug: 255; Not sure how to fix it for Linux!
//		}
//	}
}
