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
