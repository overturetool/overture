package org.overture.ide.vdmpp.ui.wizard;

import org.overture.ide.ui.wizard.VdmNewProjectWizard;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;

public class VdmPpNewProjectWizard extends VdmNewProjectWizard {	

	@Override
	protected String getNature() {
		return IVdmPpCoreConstants.NATURE;
	}

	@Override
	protected String getPageDescription() {		
		return "Chose location for VDM++ project";
	}

	@Override
	protected String getPageName() {		
		return "VDM++ Project Location";
	}

	@Override
	protected String getPageTitle() {
		return "VDM++ Project";
	}

}
