/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.wizard.pages;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.overture.ast.lex.Dialect;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.utility.PluginFolderInclude;

public class LibraryUtil
{
	public static final String LIB_VDM_UNIT = "VDMUnit";
	public static final String LIB_CSV = "CSV";
	public static final String LIB_VDM_UTIL = "VDMUtil";
	public static final String LIB_MATH = "MATH";
	public static final String LIB_IO = "IO";

	public static void setSelections(IVdmProject prj, LibrarySelection selection)
			throws CoreException
	{
		if (prj == null)
		{
			return;
		}
		String extension = "vdmpp";
		switch (prj.getDialect())
		{
			case VDM_PP:
				extension = "vdmpp";
				break;
			case VDM_RT:
				extension = "vdmrt";
				break;
			case VDM_SL:
				extension = "vdmsl";
				break;
			case CML:
				break;

		}

		extension = "." + extension;

		selection.setIoChecked(prj.getModelBuildPath().getLibrary().getFile(new Path(LIB_IO
				+ extension)).exists());
		selection.setMathChecked(prj.getModelBuildPath().getLibrary().getFile(new Path(LIB_MATH
				+ extension)).exists());
		selection.setVdmUtilChecked(prj.getModelBuildPath().getLibrary().getFile(new Path(LIB_VDM_UTIL
				+ extension)).exists());
		selection.setCsvChecked(prj.getModelBuildPath().getLibrary().getFile(new Path(LIB_CSV
				+ extension)).exists());
		selection.setVdmUnitChecked(prj.getModelBuildPath().getLibrary().getFile(new Path(LIB_VDM_UNIT
				+ extension)).exists());
	}

	public static void createSelectedLibraries(IVdmProject prj,
			LibrarySelection selection) throws CoreException
	{
		Set<String> importLibraries = new HashSet<String>();

		if (selection.isMathSelected())
		{
			importLibraries.add(LIB_MATH);
		}

		if (selection.isIoSelected())
		{
			importLibraries.add(LIB_IO);
		}
		if (selection.isUtilSelected())
		{
			importLibraries.add(LIB_VDM_UTIL);
		}
		if (selection.isCsvSelected())
		{
			importLibraries.add(LIB_IO);
			importLibraries.add(LIB_CSV);
		}
		if (selection.isVdmUnitSelected())
		{
			importLibraries.add(LIB_VDM_UNIT);
		}

		createSelectedLibraries(prj, importLibraries);
	}

	public static void createSelectedLibraries(IVdmProject prj,
			Set<String> importLibraries) throws CoreException
	{

		if (!importLibraries.isEmpty())
		{
			IProject project = (IProject) prj.getAdapter(IProject.class);
			Assert.isNotNull(project, "Project could not be adapted");

			File libFolder = null;

			IContainer tmp = prj.getModelBuildPath().getLibrary();
			if (tmp != null && tmp instanceof IFolder)
			{
				libFolder = ((IFolder) tmp).getLocation().toFile();
			} else
			{
				File projectRoot = project.getLocation().toFile();
				libFolder = new File(projectRoot, "lib");
			}

			if (!libFolder.exists())
				libFolder.mkdirs();

			String extension = "pp";

			Dialect dialect = prj.getDialect();

			extension = dialect.name().replace("_", "").toLowerCase();
			try
			{

				for (String lib : importLibraries)
				{
					String path = "includes/lib/";
					if (dialect == Dialect.VDM_SL)
					{
						path += "SL/" + lib + ".vdmsl";
					} else
					{
						path += "PP/" + lib + ".vdmpp";
					}
					copyFile(libFolder, path, lib + "." + extension);
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}

			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}

	}

	private static void copyFile(File libFolder, String sourceLocation,
			String newName) throws IOException
	{
		String io = PluginFolderInclude.readFile(IVdmUiConstants.PLUGIN_ID, sourceLocation);
		PluginFolderInclude.writeFile(libFolder, newName, io);

	}
}
