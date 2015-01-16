package org.overture.guibuilder.launching;

import org.overture.ide.debug.ui.launching.AbstractVdmLaunchConfigurationTabGroup;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;

public class GuiBuilderRtLaunchConfigurationTabGroup extends
AbstractVdmLaunchConfigurationTabGroup {

	@Override
	protected AbstractVdmMainLaunchConfigurationTab getMainTab() {
		return new RtBuilderMainLaunchConfigurationTab();
	}

	
	

}
