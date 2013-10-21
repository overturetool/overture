package org.overture.ide.debug.utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IProject;

public class VdmProjectClassPathCollector extends ClassPathCollector
{
	
	public static List<String> getClassPath(IProject project, String[] bundleIds,
			File vdmProperties)
	{
		return getClassPath(project, bundleIds, vdmProperties.getParentFile().getAbsolutePath());
	}
	
	public static List<String> getClassPath(IProject project, String[] bundleIds,
			String... additionalCpEntries)
	{
		List<String> entries = new Vector<String>();
		// get the class path for all jars in the project lib folder
		File lib = new File(project.getLocation().toFile(), "lib");
		if (lib.exists() && lib.isDirectory())
		{
			for (File f : getAllDirectories(lib))
			{
				entries.add(f.getAbsolutePath());
			}

			for (File f : getAllFiles(lib, new HashSet<String>(Arrays.asList(new String[] { ".jar" }))))
			{
				entries.add(f.getAbsolutePath());
			}
		}
		
		//add custom properties file vdmj.properties
//		entries.add(vdmjPropertiesFile.getParentFile().getAbsolutePath());
		entries.addAll(Arrays.asList(additionalCpEntries));
		return ClassPathCollector.getClassPath(project, bundleIds, entries.toArray(new String[]{}));
	}

	
	private static List<File> getAllDirectories(File file)
	{
		List<File> files = new Vector<File>();
		if (file.isDirectory())
		{
			files.add(file);
			for (File f : file.listFiles())
			{
				files.addAll(getAllDirectories(f));
			}

		}
		return files;
	}

	private static List<File> getAllFiles(File file, Set<String> extensionFilter)
	{
		List<File> files = new Vector<File>();
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				files.addAll(getAllFiles(f, extensionFilter));
			}

		} else
		{
			for (String filter : extensionFilter)
			{
				if (file.getAbsolutePath().endsWith(filter))
				{
					files.add(file);
				}
			}

		}
		return files;
	}
}
