package org.overture.ide.vdmrt.ui.wizards;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class VdmRtNewClassWizard extends NewSourceModuleWizard {

	public VdmRtNewClassWizard() {
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("new VDM-RT class");
	}

	@Override
	protected NewSourceModulePage createNewSourceModulePage() {
return new NewSourceModulePage() {
			
			@Override
			protected String getRequiredNature() {
				return VdmRtProjectNature.VDM_RT_NATURE;
			}
			
			@Override
			protected String getPageTitle() {
				return "VDM-RT class";
			}
			
			@Override
			protected String getPageDescription() {
				return "Create a new VDM-RT class";
			}
			
			@Override
			protected String getFileContent() {
				String className = getFileText();
				return "class " + className + "\n"
						+ "\tinstance variables \n\n"
						+ "\ttypes \n\n"
						+ "\tvalues \n\n"
						+ "\toperations \n\n"
						+ "\tfunctions \n\n"
						+ "\ttraces \n\n"
						+ "\ttraces \n\n" 
						+ "end " + className;
			}
		};
	}

}
