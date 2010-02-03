package org.overture.ide.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.utility.PluginFolderInclude;
import org.overture.ide.utility.VdmProject;
import org.overturetool.vdmj.lex.Dialect;

public class CreateVdmProjectWizard extends BasicNewProjectResourceWizard
{
	//private IConfigurationElement fConfigElement;
	private String nature;

	public CreateVdmProjectWizard() {
		setWindowTitle("New VDM project");

	}

	@Override
	public void addPages()
	{

		super.addPages();
		getPages()[0].setTitle("VDM Project");
		getPages()[0].setDescription("Create a new VDM Project");

		addPage(new LibraryIncludePage("VDM Libraries"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data)
	{
		//fConfigElement = cfig;
		if (data instanceof String)
		{
			this.nature = (String) data;
		} else if (data instanceof Map)
		{
			this.nature = (String) ((Map) data).get("nature"); //$NON-NLS-1$
		}
		if (this.nature == null || this.nature.length() == 0)
		{
			throw new RuntimeException("Messages.GenericDLTKProjectWizard_natureMustBeSpecified");
		}
	}

	@Override
	public boolean performFinish()
	{

		boolean ok = super.performFinish();
		IProject prj = getNewProject();

		if (prj != null)
		{
			try
			{
				VdmProject.addNature(prj, nature);
				VdmProject.addBuilder(prj, VdmProject.BUILDER_ID, "", "");

				for (IWizardPage page : getPages())
				{
					if (page instanceof LibraryIncludePage)
					{
						boolean useMath = ((LibraryIncludePage) page).getLibrarySelection()
								.isMathSelected();
						boolean useIo = ((LibraryIncludePage) page).getLibrarySelection()
								.isIoSelected();
						boolean useUtil = ((LibraryIncludePage) page).getLibrarySelection()
								.isUtilSelected();

						File projectRoot = prj.getLocation().toFile();
						File libFolder = new File(projectRoot, "lib");
						if (!libFolder.exists())
							libFolder.mkdirs();

						String extension = "pp";
						
						Dialect dialect = Dialect.VDM_PP;
						if(nature.contains(Dialect.VDM_PP.name().replace("_", "").toLowerCase()))
							dialect = Dialect.VDM_PP;
						else if(nature.contains(Dialect.VDM_RT.name().replace("_", "").toLowerCase()))
							dialect = Dialect.VDM_RT;
						else if(nature.contains(Dialect.VDM_SL.name().replace("_", "").toLowerCase()))
							dialect = Dialect.VDM_SL;
						
						extension = dialect.name().replace("_", "").toLowerCase();
						try
						{
							if (useIo)
								if(dialect==Dialect.VDM_SL)
									copyFile(libFolder, "includes/lib/sl/IO.vdmsl", "IO."+extension);
								else
									copyFile(libFolder, "includes/lib/pp/IO.vdmpp", "IO."+extension);
							
							if (useMath)
								if(dialect==Dialect.VDM_SL)
									copyFile(libFolder, "includes/lib/sl/MATH.vdmsl", "MATH."+extension);
								else
									copyFile(libFolder, "includes/lib/pp/MATH.vdmpp", "MATH."+extension);
							
							if (useUtil)
								if(dialect==Dialect.VDM_SL)
									copyFile(libFolder, "includes/lib/sl/VDMUtil.vdmsl", "VDMUtil."+extension);
								else
									copyFile(libFolder, "includes/lib/pp/VDMUtil.vdmpp", "VDMUtil."+extension);
								
							
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						prj.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
				}

			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		return ok;
	}

	private static void  copyFile(File libFolder, String sourceLocation, String newName)
			throws IOException
	{
		String io = PluginFolderInclude.readFile(VdmUIPlugin.PLUGIN_ID,
				sourceLocation);
		PluginFolderInclude.writeFile(libFolder, newName, io);

	}

}
