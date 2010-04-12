package org.overture.ide.vdmrt.ui.wizard;

import org.overture.ide.vdmpp.ui.wizard.VdmPpNewClassWizard;

public class VdmRtNewClassWizard extends VdmPpNewClassWizard {

	@Override
	protected String getPageDescription() {		
		return "Chose new file name and location";
	}

	@Override
	protected String getPageName() {		
		return "VDM-RT New File Wizard";
	}

	@Override
	protected String getPageTitle() {		
		return "VDM-RT New File Wizard";
	}

	@Override
	protected String getFileExtension()
	{
		return "vdmrt";
	}

}
