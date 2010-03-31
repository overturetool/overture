package org.overture.ide.vdmsl.debug.ui.launching;

import org.overture.ide.debug.ui.launching.AbstractVdmLaunchConfigurationTabGroup;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;

public class VdmSlLaunchConfigurationTabGroup extends
		AbstractVdmLaunchConfigurationTabGroup
{

	@Override
	protected AbstractVdmMainLaunchConfigurationTab getMainTab()
	{
		return new VdmSlMainLaunchConfigurationTab();
	}

}
