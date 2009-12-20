package org.overture.ide.vdmrt.debug.ui.tabs;

import org.overture.ide.debug.ui.tabs.VdmMainLaunchConfigurationTab;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

/**
 * Main launch configuration tab for overture scripts
 */
public class VdmRtMainLaunchConfigurationTab extends VdmMainLaunchConfigurationTab {

	public VdmRtMainLaunchConfigurationTab(String mode) {
		super(mode);
	}
	@Override
	protected String getNatureID() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}
}

