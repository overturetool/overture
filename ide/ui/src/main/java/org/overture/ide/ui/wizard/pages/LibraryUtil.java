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
package org.overture.ide.ui.wizard.pages;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.utility.PluginFolderInclude;
import org.overturetool.vdmj.lex.Dialect;

public class LibraryUtil
{
	public static void setSelections(IVdmProject prj, LibrarySelection selection)
			throws CoreException
	{
		if(prj == null)
		{
			return;
		}
		String extension="vdmpp";
		switch (prj.getDialect())
		{
			case VDM_PP:
				extension="vdmpp";
				break;
			case VDM_RT:
				extension="vdmrt";
				break;
			case VDM_SL:
				extension="vdmsl";
				break;

		}
			selection.setIoChecked(prj.getModelBuildPath().getLibrary().getFile(new Path("IO."+extension)).exists());
			selection.setMathChecked(prj.getModelBuildPath().getLibrary().getFile(new Path("MATH."+extension)).exists());
			selection.setVdmUtilChecked(prj.getModelBuildPath().getLibrary().getFile(new Path("VDMUtil."+extension)).exists());
			selection.setCsvChecked(prj.getModelBuildPath().getLibrary().getFile(new Path("CSV."+extension)).exists());
			selection.setVdmUnitChecked(prj.getModelBuildPath().getLibrary().getFile(new Path("VDMUnit."+extension)).exists());
	}
	
	

	public static void createSelectedLibraries(IVdmProject prj,
			LibrarySelection selection) throws CoreException
	{
		boolean useMath = selection.isMathSelected();
		boolean useIo = selection.isIoSelected();
		boolean useUtil = selection.isUtilSelected();
		boolean useCsvIo = selection.isCsvSelected();
		boolean useVdmUnit = selection.isVdmUnitSelected();

		if (useCsvIo)
		{
			useIo = true;
		}

		if (useIo || useMath || useUtil || useVdmUnit)
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
				if (useIo)
					if (dialect == Dialect.VDM_SL)
						copyFile(libFolder, "includes/lib/sl/IO.vdmsl", "IO."
								+ extension);
					else
						copyFile(libFolder, "includes/lib/pp/IO.vdmpp", "IO."
								+ extension);

				if (useMath)
					if (dialect == Dialect.VDM_SL)
						copyFile(libFolder, "includes/lib/sl/MATH.vdmsl", "MATH."
								+ extension);
					else
						copyFile(libFolder, "includes/lib/pp/MATH.vdmpp", "MATH."
								+ extension);

				if (useUtil)
					if (dialect == Dialect.VDM_SL)
						copyFile(libFolder, "includes/lib/sl/VDMUtil.vdmsl", "VDMUtil."
								+ extension);
					else
						copyFile(libFolder, "includes/lib/pp/VDMUtil.vdmpp", "VDMUtil."
								+ extension);
				if (useCsvIo)
					if (dialect == Dialect.VDM_SL)
						copyFile(libFolder, "includes/lib/sl/CSV.vdmsl", "CSV."
								+ extension);
					else
						copyFile(libFolder, "includes/lib/pp/CSV.vdmpp", "CSV."
								+ extension);
				if (useVdmUnit)
					if (dialect != Dialect.VDM_SL)
						copyFile(libFolder, "includes/lib/pp/VDMUnit.vdmpp", "VDMUnit."
								+ extension);

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
