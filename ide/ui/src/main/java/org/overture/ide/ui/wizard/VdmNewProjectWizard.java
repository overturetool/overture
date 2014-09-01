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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.utility.LanguageManager;
import org.overture.ide.ui.wizard.pages.LibraryIncludePage;
import org.overture.ide.ui.wizard.pages.LibraryUtil;

/*
 * Abstract wizard for creating a VDM project
 */
public abstract class VdmNewProjectWizard extends BasicNewProjectResourceWizard
{

	private LibraryIncludePage _pageTwo;
	private static final String WIZARD_NAME = "VDM New Project Wizard";

	public VdmNewProjectWizard()
	{
		setWindowTitle(WIZARD_NAME);
		getPageName();
		getPageTitle();
		getPageDescription();
	}

	@Override
	public void addPages()
	{
		super.addPages();
		getPages()[0].setTitle(getPageTitle());
		getPages()[0].setDescription(getPageDescription());
		Dialect d = LanguageManager.getInstance().getLanguage(getNature()).getDialect();
		_pageTwo = new LibraryIncludePage("Library Include",d == Dialect.VDM_PP || d==Dialect.VDM_RT,null);
		addPage(_pageTwo);
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
				setVdmBuilder(prj);
				IVdmProject p = (IVdmProject) prj.getAdapter(IVdmProject.class);
				LibraryUtil.createSelectedLibraries(p,_pageTwo.getLibrarySelection());

			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		return ok;
	}

	private void setVdmBuilder(IProject prj) throws CoreException
	{

		addNature(prj, getNature());
		IVdmProject p = (IVdmProject) prj.getAdapter(IVdmProject.class);
		Assert.isNotNull(p, "Project could not be adapted");
		p.setBuilder(Release.DEFAULT);
	}

	private static void addNature(IProject project, String nature)
			throws CoreException
	{
		if (!project.hasNature(nature))
		{
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nature;
			description.setNatureIds(newNatures);

			IProgressMonitor monitor = null;
			project.setDescription(description, monitor);
		}
	}

	protected abstract String getPageName();

	protected abstract String getPageTitle();

	protected abstract String getPageDescription();

	protected abstract String getNature();

}
