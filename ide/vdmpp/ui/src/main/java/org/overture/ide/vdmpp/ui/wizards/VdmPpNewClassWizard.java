package org.overture.ide.vdmpp.ui.wizards;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VdmPpNewClassWizard extends NewSourceModuleWizard {

	
	public VdmPpNewClassWizard() {
		//setDefaultPageImageDescriptor(); // TODO Image
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("new VDM++ class");
	}

	@Override
	protected NewSourceModulePage createNewSourceModulePage() {
		return new NewSourceModulePage() {
			
			@Override
			protected String getRequiredNature() {
				return VdmPpProjectNature.VDM_PP_NATURE;
			}
			
			@Override
			protected String getPageTitle() {
				return "VDM++ class";
			}
			
			@Override
			protected String getPageDescription() {
				return "Create a new VDM++ class";
			}
			
			@Override
			protected String getFileContent() {
				String className = getFileText();
				return "class " + className + "\n"
				+ "\ttypes\n\n"
				+ "\tvalues\n\n"
				+ "\tinstance variables\n\n"
				+ "\toperations\n\n"
				+ "\tfunctions\n\n"
				+ "\tsync\n\n"
				+ "\t--thread\n\n"
				+ "\ttraces\n\n"
				+ "end " + className;
			}
		};
	}

}
