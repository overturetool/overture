package org.overture.ide.vdmsl.ui.wizards;

import org.overture.ide.ui.wizard.VdmNewProjectWizard;
import org.overture.ide.vdmsl.core.IVdmSlCoreConstants;



public class VdmSlNewProjectWizard extends VdmNewProjectWizard {	

	@Override
	protected String getNature() {
		return IVdmSlCoreConstants.NATURE;
	}

	@Override
	protected String getPageDescription() {		
		return "Chose location for VDM-Sl project";
	}

	@Override
	protected String getPageName() {		
		return "VDM-Sl Project Location";
	}

	@Override
	protected String getPageTitle() {
		return "VDM-Sl Project";
	}

}
