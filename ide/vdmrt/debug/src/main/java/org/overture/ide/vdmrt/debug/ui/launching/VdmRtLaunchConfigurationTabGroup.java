package org.overture.ide.vdmrt.debug.ui.launching;


import org.overture.ide.debug.ui.launching.AbstractVdmLaunchConfigurationTabGroup;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;
import org.overture.ide.debug.ui.launching.VdmRuntimeChecksLaunchConfigurationTab;

public class VdmRtLaunchConfigurationTabGroup extends
		AbstractVdmLaunchConfigurationTabGroup
{

	@Override
	protected AbstractVdmMainLaunchConfigurationTab getMainTab()
	{
		return new VdmRtMainLaunchConfigurationTab();
	}
	
	@Override
	protected VdmRuntimeChecksLaunchConfigurationTab getRuntimeTab()
	{
		return new VdmRtRuntimeChecksLaunchConfigurationTab();
	}

}
