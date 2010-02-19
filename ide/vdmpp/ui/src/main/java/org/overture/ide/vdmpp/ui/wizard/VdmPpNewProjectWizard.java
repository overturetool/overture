package org.overture.ide.vdmpp.ui.wizard;

import org.overture.ide.ui.wizard.VdmNewProjectWizard;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VdmPpNewProjectWizard extends VdmNewProjectWizard {	

	@Override
	protected String getNature() {
		return VdmPpProjectNature.VDM_PP_NATURE;
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
