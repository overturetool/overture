package org.overture.ide.ui.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.utility.PluginFolderInclude;
import org.overture.ide.ui.wizard.pages.LibraryIncludePage;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.lex.Dialect;

/*
 * Abstract wizard for creating a VDM project
 */
public abstract class VdmNewProjectWizard extends BasicNewProjectResourceWizard
{

	private LibraryIncludePage _pageTwo;
	private static final String WIZARD_NAME = "VDM New Project Wizard";
	public VdmNewProjectWizard() {
		setWindowTitle(WIZARD_NAME);
		getPageName();
		getPageTitle();
		getPageDescription();
	}

//	public void init(IWorkbench workbench, IStructuredSelection selection)
//	{
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void addPages()
	{
		super.addPages();
//		_pageOne = new WizardNewProjectCreationPage(this.fPageName);
//		_pageOne.setTitle(this.fPageTitle);
//		_pageOne.setDescription(this.fPageDescription);

		getPages()[0].setTitle(getPageTitle());
		getPages()[0].setDescription(getPageDescription());
		
		_pageTwo = new LibraryIncludePage("Library Include");
		addPage(_pageTwo);
	}

//	public boolean canFinish()
//	{
//		return _pageOne.getErrorMessage() == null;
//	}

	// @Override
	// public boolean performFinish(){
	// String name = _pageOne.getProjectName();
	// URI location = null;
	// if (!_pageOne.useDefaults()) {
	// location = _pageOne.getLocationURI();
	// }
	//
	// try
	// {
	// VdmProject.createProject(name, location, getNature());
	// } catch (CoreException e)
	// {
	// if(VdmCore.DEBUG)
	// e.printStackTrace();
	// } catch (NotAllowedException e)
	// {
	// if(VdmCore.DEBUG)
	// e.printStackTrace();
	// }
	//
	// return true;
	// }
	// @SuppressWarnings("unchecked")
	// @Override
	// public void setInitializationData(IConfigurationElement cfig,
	// String propertyName, Object data)
	// {
	// // fConfigElement = cfig;
	// if (data instanceof String)
	// {
	// this.nature = (String) data;
	// } else if (data instanceof Map)
	// {
	//			this.nature = (String) ((Map) data).get("nature"); //$NON-NLS-1$
	// }
	// if (this.nature == null || this.nature.length() == 0)
	// {
	// throw new RuntimeException("Messages.GenericDLTKProjectWizard_natureMustBeSpecified");
	// }
	// }

	@Override
	public boolean performFinish()
	{

		boolean ok = super.performFinish();
		IProject prj = getNewProject();

		if (prj != null)
		{
			try
			{
				setVdmBuilder(prj);

				createSelectedLibraries(prj);
				createModelFolder(prj);

			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		return ok;
	}

	private void createModelFolder(IProject prj) {
		File projectRoot = prj.getLocation().toFile();
		File modelFolder = new File(projectRoot, "model");
		if (!modelFolder.exists())
			modelFolder.mkdirs();
	}

	private void createSelectedLibraries(IProject prj) throws CoreException
	{
		boolean useMath = _pageTwo.getLibrarySelection()
				.isMathSelected();
		boolean useIo = _pageTwo.getLibrarySelection().isIoSelected();
		boolean useUtil = _pageTwo.getLibrarySelection()
				.isUtilSelected();

		if (useIo || useMath || useUtil)
		{
			File projectRoot = prj.getLocation().toFile();
			File libFolder = new File(new File(projectRoot, "model"),"lib");
			if (!libFolder.exists())
				libFolder.mkdirs();

			String extension = "pp";

			Dialect dialect = Dialect.VDM_PP;
			if (getNature().contains(Dialect.VDM_PP.name().replace("_",
					"").toLowerCase()))
				dialect = Dialect.VDM_PP;
			else if (getNature().contains(Dialect.VDM_RT.name()
					.replace("_", "")
					.toLowerCase()))
				dialect = Dialect.VDM_RT;
			else if (getNature().contains(Dialect.VDM_SL.name()
					.replace("_", "")
					.toLowerCase()))
				dialect = Dialect.VDM_SL;

			extension = dialect.name().replace("_", "").toLowerCase();
			try
			{
				if (useIo)
					if (dialect == Dialect.VDM_SL)
						copyFile(libFolder,
								"includes/lib/sl/IO.vdmsl",
								"IO." + extension);
					else
						copyFile(libFolder,
								"includes/lib/pp/IO.vdmpp",
								"IO." + extension);

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

	private void setVdmBuilder(IProject prj) throws CoreException
	{
		VdmProject.addNature(prj, getNature());

		IVdmProject p = VdmProject.createProject(prj);
		p.setBuilder(Release.DEFAULT);
	}

	private static void copyFile(File libFolder, String sourceLocation,
			String newName) throws IOException
	{
		String io = PluginFolderInclude.readFile(IVdmUiConstants.PLUGIN_ID,
				sourceLocation);
		PluginFolderInclude.writeFile(libFolder, newName, io);

	}

	protected abstract String getPageName();

	protected abstract String getPageTitle();

	protected abstract String getPageDescription();

	protected abstract String getNature();

}
