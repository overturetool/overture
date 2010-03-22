package org.overture.ide.debug.ui.launching;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

public class AbstractLaunchConfigurationTabGroup extends
		org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup {

	public AbstractLaunchConfigurationTabGroup() {
		// TODO Auto-generated constructor stub
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[]{
//				new VdmppMainLaunchConfigurationTab(mode),
//				new VdmInterpreterTab(),
//				new VdmEnvironmentTab(),
				new SourceLookupTab(),
				new CommonTab()
				};
				setTabs(tabs);

	}

}
