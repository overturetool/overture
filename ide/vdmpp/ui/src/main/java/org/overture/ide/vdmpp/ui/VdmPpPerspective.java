package org.overture.ide.vdmpp.ui;

import org.eclipse.ui.IPageLayout;
import org.overture.ide.ui.VdmDefaultPerspective;
 
public class VdmPpPerspective extends VdmDefaultPerspective {

	@Override
	protected void addNewWiardShortcuts(IPageLayout layout) {
		// VDM++
		layout.addNewWizardShortcut(VdmPpUiPluginConstants.NEW_VDMPP_CLASS_WIZARD_ID);

		layout.addNewWizardShortcut(VdmPpUiPluginConstants.NEW_PROJECT_WIZARD_ID);
		//layout.addNewWizardShortcut(RubyNewModuleWizard.WIZARD_ID);
		//layout.addNewWizardShortcut(RubyNewFileWizard.WIZARD_ID);
	}

}
