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
package org.overture.ide.ui.wizard;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.overture.ast.lex.Dialect;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.wizard.pages.LibraryIncludePage;
import org.overture.ide.ui.wizard.pages.LibraryUtil;

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
		_pageTwo = new LibraryIncludePage("Add Library",isOoDialect(),project);
		
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
