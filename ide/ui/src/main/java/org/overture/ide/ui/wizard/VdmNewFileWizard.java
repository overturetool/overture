package org.overture.ide.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.overture.ide.ui.VdmUIPlugin;

public abstract class VdmNewFileWizard extends Wizard implements IWorkbenchWizard {

	private static final String WIZARD_NAME = "VDM New File Wizard"; 
	
	private WizardNewFileCreationPage _pageOne;
	private String fPageName;
	private String fPageTitle;
	private String fPageDescription;
	private IStructuredSelection fStructuredSelection;
	
	public VdmNewFileWizard(){
		setWindowTitle(WIZARD_NAME);
		this.fPageName = getPageName();
		this.fPageTitle = getPageTitle();
		this.fPageDescription = getPageDescription();
	}
	
	@Override
	public void addPages() {		
		super.addPages();
		_pageOne = new WizardNewFileCreationPage(this.fPageName, this.fStructuredSelection);
		_pageOne.setTitle(this.fPageTitle);
		_pageOne.setDescription(this.fPageDescription);
		
		addPage(_pageOne);
		
	}
	
	protected abstract String getPageName();
	protected abstract String getPageTitle();
	protected abstract String getPageDescription();
	
	
	@Override
	public boolean canFinish() {
		return _pageOne.getErrorMessage() == null;
	}
	
	@Override
	public boolean performFinish() {
		_pageOne.setFileExtension("vdmpp");		
		_pageOne.createNewFile();
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		VdmUIPlugin.println("VdmNewFileWizard.init");
		this.fStructuredSelection = selection;
	}

	
}
