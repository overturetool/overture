package org.overture.guibuilder.launching;

import org.overture.ide.debug.ui.launching.AbstractVdmLaunchConfigurationTabGroup;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;

/**
 * Configuration Tab Group for launching GUI models. Just inherits everything from VDM. The only
 * thing that changes is the main tab.
 * @author ldc
 *
 */
public class GuiBuilderPpLaunchConfigurationTabGroup extends
		AbstractVdmLaunchConfigurationTabGroup {

	@Override
	protected AbstractVdmMainLaunchConfigurationTab getMainTab() {
		return new PpBuilderMainLaunchConfigurationTab();
	}

}
