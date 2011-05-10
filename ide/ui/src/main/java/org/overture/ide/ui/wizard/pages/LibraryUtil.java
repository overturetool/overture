package org.overture.ide.ui.wizard.pages;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.utility.PluginFolderInclude;
import org.overturetool.vdmj.lex.Dialect;

public class LibraryUtil
{
	public static void createSelectedLibraries(IVdmProject prj, LibrarySelection selection) throws CoreException
	{
		boolean useMath = selection.isMathSelected();
		boolean useIo = selection.isIoSelected();
		boolean useUtil = selection.isUtilSelected();
		boolean useCsvIo = selection.isCsvSelected();
		boolean useVdmUnit = selection.isVdmUnitSelected();
		
		if(useCsvIo)
		{
			useIo = true;
		}

		if (useIo || useMath || useUtil || useVdmUnit)
		{
			IProject project = (IProject) prj.getAdapter(IProject.class);
			Assert.isNotNull(project, "Project could not be adapted");
			
			
			File projectRoot = project.getLocation().toFile();
			File libFolder = new File(projectRoot,"lib");
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
						copyFile(libFolder,
								"includes/lib/sl/MATH.vdmsl",
								"MATH." + extension);
					else
						copyFile(libFolder,
								"includes/lib/pp/MATH.vdmpp",
								"MATH." + extension);

				if (useUtil)
					if (dialect == Dialect.VDM_SL)
						copyFile(libFolder,
								"includes/lib/sl/VDMUtil.vdmsl",
								"VDMUtil." + extension);
					else
						copyFile(libFolder,
								"includes/lib/pp/VDMUtil.vdmpp",
								"VDMUtil." + extension);
				if(useCsvIo)
					if (dialect == Dialect.VDM_SL)
						copyFile(libFolder,
								"includes/lib/sl/CSV.vdmsl",
								"CSV." + extension);
					else
						copyFile(libFolder,
								"includes/lib/pp/CSV.vdmpp",
								"CSV." + extension);
				if(useVdmUnit)
					if (dialect != Dialect.VDM_SL)
						copyFile(libFolder,
								"includes/lib/pp/VDMUnit.vdmpp",
								"VDMUnit." + extension);

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
		String io = PluginFolderInclude.readFile(IVdmUiConstants.PLUGIN_ID,
				sourceLocation);
		PluginFolderInclude.writeFile(libFolder, newName, io);

	}
}
