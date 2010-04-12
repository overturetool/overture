package org.overture.ide.vdmsl.ui.wizards;

import org.overture.ide.ui.wizard.VdmNewFileWizard;

public class VdmSlNewModuleWizard extends VdmNewFileWizard {

	@Override
	protected String getPageDescription() {		
		return "Chose new VDM-Sl file name and location";
	}

	@Override
	protected String getPageName() {		
		return "VDM-Sl New Module Wizard";
	}

	@Override
	protected String getPageTitle() {		
		return "VDM-Sl New Module Wizard";
	}

	@Override
	protected String getFileExtension()
	{
		return "vdmsl";
	}
	
	@Override
	protected String getFileTemplate(String fileName)
	{
		String moduleName = fileName;
		return "module " + moduleName + "\n"
		+ "exports all\n"
		+ "definitions \n\n"
		+ "\tstate StateName of\n \n-- TODO Define state here\n"
		+ "\tend \n\n"
		+ "\ttypes \n-- TODO Define types here\n"
		+ "\tvalues \n-- TODO Define values here\n"
		+ "\tfunctions \n-- TODO Define functions here\n"
		+ "\toperations \n-- TODO Define operations here\n"
		+ "end " + moduleName;
	}

}
