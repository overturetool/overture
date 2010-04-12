package org.overture.ide.vdmrt.debug.ui.launching;


import org.overture.ide.debug.ui.launching.AbstractVdmLaunchConfigurationTabGroup;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;

public class VdmRtLaunchConfigurationTabGroup extends
		AbstractVdmLaunchConfigurationTabGroup
{

	@Override
	protected AbstractVdmMainLaunchConfigurationTab getMainTab()
	{
		return new VdmRtMainLaunchConfigurationTab();
	}

}
