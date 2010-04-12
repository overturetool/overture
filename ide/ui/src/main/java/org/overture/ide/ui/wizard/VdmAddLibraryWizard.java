package org.overture.ide.ui.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.utility.PluginFolderInclude;
import org.overture.ide.ui.wizard.pages.LibraryIncludePage;
import org.overturetool.vdmj.lex.Dialect;

public class VdmAddLibraryWizard extends Wizard implements IWorkbenchWizard
{
	private static final String WIZARD_NAME = "Add Library Wizard";
	private IVdmProject project = null;
	private LibraryIncludePage _pageTwo;

	public VdmAddLibraryWizard() {
		setWindowTitle(WIZARD_NAME);
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			createSelectedLibraries(project);
		} catch (CoreException e)
		{
			if (VdmUIPlugin.DEBUG)
			{
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		if (selection.getFirstElement() instanceof IVdmProject)
		{
			this.project = (IVdmProject) selection.getFirstElement();
		}

	}

	@Override
	public void addPages()
	{
		_pageTwo = new LibraryIncludePage("Add Library");
		addPage(_pageTwo);
	}

	private void createSelectedLibraries(IVdmProject prj) throws CoreException
	{
		boolean useMath = _pageTwo.getLibrarySelection().isMathSelected();
		boolean useIo = _pageTwo.getLibrarySelection().isIoSelected();
		boolean useUtil = _pageTwo.getLibrarySelection().isUtilSelected();

		if (useIo || useMath || useUtil)
		{
			File projectRoot = prj.getLocation().toFile();
			File libFolder = new File(projectRoot, "lib");
			if (!libFolder.exists())
				libFolder.mkdirs();

			String extension = "pp";

			Dialect dialect = project.getDialect();

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

			} catch (IOException e)
			{
				e.printStackTrace();
			}

			prj.refreshLocal(IResource.DEPTH_INFINITE, null);
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
