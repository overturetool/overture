package org.overture.ide.ui.wizard;

import java.net.URI;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.overture.ide.core.utility.ProjectUtility;


public abstract class VdmNewProjectWizard extends Wizard implements IWorkbenchWizard{

	
	private WizardNewProjectCreationPage _pageOne;
	private static final String WIZARD_NAME = "VDM New Project Wizard"; 
	private String fPageName;
	private String fPageTitle;
	private String fPageDescription;
	
	
	public VdmNewProjectWizard(){
		setWindowTitle(WIZARD_NAME);
		this.fPageName = getPageName();
		this.fPageTitle = getPageTitle();
		this.fPageDescription = getPageDescription();
	}
	
	
	protected abstract String getPageName();
	protected abstract String getPageTitle();
	protected abstract String getPageDescription();


	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPages() {
		super.addPages();
		_pageOne = new WizardNewProjectCreationPage(this.fPageName);
		_pageOne.setTitle(this.fPageTitle);
		_pageOne.setDescription(this.fPageDescription);
		
		addPage(_pageOne);
	}

	public boolean canFinish() {
		return _pageOne.getErrorMessage() == null;
	}

	@Override
	public boolean performFinish(){
		String name = _pageOne.getProjectName();
	    URI location = null;
	    if (!_pageOne.useDefaults()) {
	        location = _pageOne.getLocationURI();
	    } // else location == null

	    ProjectUtility.createProject(name, location, getNature());

	    return true;
	}

	protected abstract String getNature();
	

	

	
}
