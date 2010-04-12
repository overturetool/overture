package org.overture.ide.vdmrt.ui.wizard;

import org.overture.ide.ui.wizard.VdmNewProjectWizard;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;

public class VdmRtNewProjectWizard extends VdmNewProjectWizard {	

	@Override
	protected String getNature() {
		return IVdmRtCoreConstants.NATURE;
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
