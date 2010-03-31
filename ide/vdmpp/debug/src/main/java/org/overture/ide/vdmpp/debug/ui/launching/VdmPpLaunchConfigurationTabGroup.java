package org.overture.ide.vdmpp.debug.ui.launching;

import org.overture.ide.debug.ui.launching.AbstractVdmLaunchConfigurationTabGroup;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;

public class VdmPpLaunchConfigurationTabGroup extends
		AbstractVdmLaunchConfigurationTabGroup
{

	@Override
	protected AbstractVdmMainLaunchConfigurationTab getMainTab()
	{
		return new VdmPpMainLaunchConfigurationTab();
	}

}
