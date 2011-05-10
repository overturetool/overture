package org.overture.ide.ui.wizard;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.wizard.pages.LibraryIncludePage;
import org.overture.ide.ui.wizard.pages.LibraryUtil;
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
			LibraryUtil.createSelectedLibraries(project,_pageTwo.getLibrarySelection());
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
		if(selection.getFirstElement() instanceof IProject ){
			IProject project = (IProject) selection.getFirstElement();
			this.project = (IVdmProject) project.getAdapter(IVdmProject.class);
		}
		if (selection.getFirstElement() instanceof IVdmProject)
		{					
			this.project = (IVdmProject) selection.getFirstElement();
		}else if(selection.getFirstElement() instanceof IFolder)
		{
			IProject project = ((IFolder)selection.getFirstElement()).getProject();
			this.project = (IVdmProject) project.getAdapter(IVdmProject.class);
			
			if(this.project == null)
			{
				MessageDialog.openError(getShell(), "Project type error", "Project is not a VDM project");
			}
		}

	}

	@Override
	public void addPages()
	{
		_pageTwo = new LibraryIncludePage("Add Library",isOoDialect());
		addPage(_pageTwo);
	}
	
	private boolean isOoDialect()
	{
		if(project!= null)
		{
			return project.getDialect() == Dialect.VDM_PP || project.getDialect() == Dialect.VDM_RT;
		}
		return false;
	}



}
