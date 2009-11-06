package org.overture.ide.vdmsl.ui;

import org.eclipse.ui.IPageLayout;
import org.overture.ide.ui.VdmDefaultPerspective;

public class VdmSlPerspective extends VdmDefaultPerspective {

	@Override
	protected void addNewWiardShortcuts(IPageLayout layout) {
		layout.addNewWizardShortcut(VdmSlUiPluginConstants.NEW_VDMSL_MODULE_WIZARD_ID);
		layout.addNewWizardShortcut(VdmSlUiPluginConstants.NEW_PROJECT_WIZARD_ID);

	}

}
