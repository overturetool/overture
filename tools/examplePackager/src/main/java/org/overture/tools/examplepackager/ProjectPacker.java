/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.tools.examplepackager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.tools.examplepackager.util.FileUtils;
import org.overture.tools.examplepackager.util.FolderZipper;

public class ProjectPacker implements Comparable<ProjectPacker>
{
	static final String VDM_README_FILENAME = "README.txt";
	File root;
	VdmReadme settings = null;
	Dialect dialect = Dialect.VDM_PP;
	File newLocation;
	boolean verbose = true;

	public ProjectPacker(File root, Dialect dialect, boolean verbose) {
		this(root,dialect);
		this.verbose=verbose;
	}
	
	public ProjectPacker(File root, Dialect dialect) {
		this.root = root;
		this.dialect = dialect;
		File readme = new File(root, VDM_README_FILENAME);

		settings = new VdmReadme(readme, root.getName()
				+ getName(dialect).toUpperCase(), dialect, false);
		if (!readme.exists())
		{
			System.out.println("Creating initial README file for: "
					+ root.getAbsolutePath());
			settings.createReadme();
		}

		settings.initialize();
	}

	public File packTo(File location)
	{
		File outputLocation = new File(location, settings.getName());
		return packTo(location,outputLocation);
	}
	public File packTo(File location,File outputLocation)
	{
		if (settings == null)
		{
			System.out.println("Skipping project: " + root.getAbsolutePath());
			return null;
		}

		if (verbose)
			System.out.println("Writing project: " + settings.getName());

		outputLocation.mkdirs();

		copyFiles(root, outputLocation, dialect);
		settings.writeProjectFile(outputLocation);
		settings.writeReadmeContentFile(outputLocation, VDM_README_FILENAME);
		settings.writeLaunchFile(outputLocation);
		if (settings.getLibs().size() > 0)
		{
			File libDir = new File(outputLocation,"lib");
			libDir.mkdirs();
			for (String lib : settings.getLibs())
			{
				try
				{
					FileUtils.writeFile( FileUtils.readFile("/libs/"+getName(getDialect())+"/"+lib),new File(libDir, "/"+lib));
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		settings.writeSettings(outputLocation);
		newLocation = outputLocation;
		return outputLocation;
	}
	
	public void zipTo(File zipFile)
	{
		File tmp = new File("zipTmp");
		tmp.mkdirs();
		File destination = packTo(tmp);
		
		FolderZipper.zipFolder(destination.getAbsolutePath(), zipFile.getAbsolutePath());
		Controller.delete(tmp);
		
		
		
	}

	private static String createNewFileName(File newExample, File file,
			Dialect dialect)
	{

		String name = file.getName();
		String extension = ".vdm" + getName(dialect).toLowerCase();

		name = name.substring(0, name.indexOf('.')) + extension;

		return new File(newExample, name).getAbsolutePath();
	}

	public File getNewLocation()
	{
		return newLocation;
	}

	public VdmReadme getSettings()
	{
		return settings;
	}

	public Dialect getDialect()
	{
		return dialect;
	}

	public List<File> getSpecFiles()
	{
		return getSpecFiles(getNewLocation());
	}

	public List<File> getSpecFiles(File root)
	{
		List<File> specFiles = new Vector<File>();
		for (File f : root.listFiles())
		{
			if (f.isFile() && f.getName().toLowerCase().endsWith(".vdmpp")
					|| f.getName().toLowerCase().endsWith(".vdmrt")
					|| f.getName().toLowerCase().endsWith(".vdmsl"))
				specFiles.add(f);
			else if(f.isDirectory())
				specFiles.addAll(getSpecFiles(f));
		}
		return specFiles;
	}

	public static String getName(Dialect dialect)
	{
		switch (dialect)
		{
		case VDM_PP:
			return "PP";
		case VDM_RT:
			return "RT";
		case VDM_SL:
			return "SL";
		default:
			return "PP";
		}
	}

	private static void copyFiles(File source, File destination, Dialect dialect)
	{

		for (File file : source.listFiles())
		{
			if (file.getName().equals(".svn")
					|| file.getName().equals(".project")
					|| file.getName().equals(".classpath")
					|| file.getName().equals(".DS_Store")
					|| file.getName().endsWith(".result")
					)
				continue;
			if (file.isFile())
			{
				String fileName = new File(destination, file.getName()).getAbsolutePath();

				if (file.getName().endsWith("vpp")
						|| file.getName().endsWith("vdm"))
					fileName = createNewFileName(destination, file, dialect);
				copyfile(file.getAbsolutePath(), fileName);
			} else
			{
				File newFolder = new File(destination, file.getName());
				newFolder.mkdirs();
				copyFiles(file, newFolder, dialect);
			}
		}

	}

	public static void copyfile(String srFile, String dtFile)
	{
		try
		{
			File f1 = new File(srFile);
			File f2 = new File(dtFile);

			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			out.flush();
			in.close();
			out.close();

			// System.out.println("File copied: "+ f1.getName());
		} catch (FileNotFoundException ex)
		{
			System.out.println(ex.getMessage() + " in the specified directory.");

		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public int compareTo(ProjectPacker o)
	{
		return this.getSettings()
				.getName()
				.toLowerCase()
				.compareTo(o.getSettings().getName().toLowerCase());
	}

}
