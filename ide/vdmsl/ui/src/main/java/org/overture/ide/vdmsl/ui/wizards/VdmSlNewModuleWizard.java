package org.overture.ide.vdmsl.ui.wizards;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class VdmSlNewModuleWizard extends NewSourceModuleWizard {

	public VdmSlNewModuleWizard() {
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("new VDM-SL module");
	}

	@Override
	protected NewSourceModulePage createNewSourceModulePage() {
		return new NewSourceModulePage() {
			
			@Override
			protected String getRequiredNature() {
				return VdmSlProjectNature.VDM_SL_NATURE;
			}
			
			@Override
			protected String getPageTitle() {
				return "VDM-SL Module";
			}
			
			@Override
			protected String getPageDescription() {
				return "Create a new VDM-SL class";
			}
			
			@Override
			protected String getFileContent() {
				String moduleName = getFileText();
				return "module " + moduleName + "\n"
				+ "export all\n"
				+ "definitions \n\n"
				+ "\tstate StateName of\n \n\n"
				+ "\tend \n\n"
				+ "\ttypes \n\n"
				+ "\tvalues \n\n"
				+ "\tfunctions \n\n"
				+ "\toperations \n\n"
				+ "end " + moduleName;
			}
		};
	}
}
