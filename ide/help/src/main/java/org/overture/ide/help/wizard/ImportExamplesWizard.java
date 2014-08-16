/*
 * #%~
 * org.overture.ide.help
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
package org.overture.ide.help.wizard;

import java.io.IOException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.overture.ide.help.IVdmHelpConstants;
import org.overture.ide.help.VdmHelpPlugin;
import org.overture.ide.help.wizard.SelectDialectWizardPage.DialectSelectedHandler;
import org.overture.ide.ui.wizard.pages.WizardProjectsImportPageProxy;

public class ImportExamplesWizard extends Wizard implements IImportWizard
{

	private WizardProjectsImportPageProxy importPageProxy = new WizardProjectsImportPageProxy();
	private SelectDialectWizardPage dialectSelectionPage = new SelectDialectWizardPage(new DialectSelectedHandler()
	{

		@Override
		public void dialectSelected(String dialect)
		{

			String dialectName = "SL";
			if (dialect.equalsIgnoreCase("VDM-SL"))
			{
				dialectName = "VDMSL";
			} else if (dialect.equalsIgnoreCase("VDM-PP"))
			{
				dialectName = "VDM++";
			} else if (dialect.equalsIgnoreCase("VDM-RT"))
			{
				dialectName = "VDMRT";
			}

			archiveInputPath = "html/Example_package_" + dialectName + ".zip";

			updateImportPage();
		}
	});
	private String archiveInputPath = "html/Example_package_VDMSL.zip";

	public ImportExamplesWizard()
	{
	}

	@Override
	public void createPageControls(Composite pageContainer)
	{
		super.createPageControls(pageContainer);
	}

	void updateImportPage()
	{
		try
		{
			this.importPageProxy.setBundleRelativeInputPath(IVdmHelpConstants.PLUGIN_ID, this.archiveInputPath);
		} catch (IOException e)
		{
			VdmHelpPlugin.logErrorMessage("Failed to get path for embedded exmaples.zip");
		}
		this.importPageProxy.createPageControlsPostconfig();
	}

	@Override
	public void addPages()
	{
		super.addPages();
		addPage(this.dialectSelectionPage);
		addPage(this.importPageProxy.getPage());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{

	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{
		IWizardPage p =  super.getNextPage(page);
		updateImportPage();
		return p;
	}
	

	@Override
	public boolean performFinish()
	{
		updateImportPage();
		this.importPageProxy.performFinish();
		return true;
	}

	@Override
	public boolean performCancel()
	{
		this.importPageProxy.performCancel();
		return true;
	}

}
