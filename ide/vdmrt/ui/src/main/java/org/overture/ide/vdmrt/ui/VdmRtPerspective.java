package org.overture.ide.vdmrt.ui;

import org.eclipse.ui.IPageLayout;
import org.overture.ide.ui.VdmDefaultPerspective;

public class VdmRtPerspective extends VdmDefaultPerspective {

	@Override
	protected void addNewWiardShortcuts(IPageLayout layout) {
		layout.addNewWizardShortcut(VdmRtUiPluginConstants.NEW_VDMRT_CLASS_WIZARD_ID);
		layout.addNewWizardShortcut(VdmRtUiPluginConstants.NEW_PROJECT_WIZARD_ID);
	}

}
