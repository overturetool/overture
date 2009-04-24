package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.launchConfigurations;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.interpreters.OvertureInterpreterTab;


public class OvertureTabGroup extends AbstractLaunchConfigurationTabGroup {
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new OvertureMainLaunchConfigurationTab(mode),
				//new OvertureArgumentsTab(),
				new OvertureInterpreterTab(),
				//new EnvironmentTab(),
				//new SourceContainerLookupTab(),
				//new CommonTab()
				new OvertureCommonTab()
		};
		setTabs(tabs);
	}
}
