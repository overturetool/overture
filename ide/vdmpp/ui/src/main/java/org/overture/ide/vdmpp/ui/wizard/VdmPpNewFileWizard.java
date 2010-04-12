package org.overture.ide.vdmpp.ui.wizard;

import org.overture.ide.ui.wizard.VdmNewFileWizard;

public class VdmPpNewFileWizard extends VdmNewFileWizard {

	@Override
	protected String getPageDescription() {		
		return "Chose new VDM++ file name and location";
	}

	@Override
	protected String getPageName() {		
		return "VDM++ New File Wizard";
	}

	@Override
	protected String getPageTitle() {		
		return "VDM++ New File Wizard";
	}

	@Override
	protected String getFileExtension()
	{
		return "vdmpp";
	}

}
