package org.overture.ide.ui.wizards;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.GenericDLTKProjectWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.jface.wizard.IWizardPage;

public class CreateVdmProjectWizard extends GenericDLTKProjectWizard
{

	public CreateVdmProjectWizard() {
		
		super();
		

		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		DLTKUIPlugin.getImageDescriptorRegistry();
		setWindowTitle("New VDM project");
		//getPages()[0].setTitle("Create a VDM Project");
		
	}
	@Override
	public void addPages()
	{
		
		super.addPages();
		getStartingPage().setTitle("New VDM project");
		getStartingPage().setDescription("Create a new VDM Project");
	}
	

	@Override
	public boolean performFinish()
	{

		boolean ok = super.performFinish();
		IProject prj = null;
		for (IWizardPage p : super.getPages())
		{
			if (p instanceof ProjectWizardFirstPage)
				prj = ((ProjectWizardFirstPage) p).getProjectHandle();
		}

		try
		{
			addBuilder(prj, "org.eclipse.dltk.core.scriptbuilder");
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ok;
	}

	private void addBuilder(IProject project, String id) throws CoreException
	{
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		for (int i = 0; i < commands.length; ++i)
			if (commands[i].getBuilderName().equals(id))
				return;
		// add builder to project
		ICommand command = desc.newCommand();
		command.setBuilderName(id);
		ICommand[] nc = new ICommand[commands.length + 1];
		// Add it before other builders.
		System.arraycopy(commands, 0, nc, 1, commands.length);
		nc[0] = command;
		desc.setBuildSpec(nc);
		project.setDescription(desc, null);
	}

}
