package org.overture.guibuilder.launching;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.vdmsl.debug.ui.launching.VdmSlMainLaunchConfigurationTab;
import org.overture.parser.messages.Console;

public class RtBuilderMainLaunchConfigurationTab extends
		VdmSlMainLaunchConfigurationTab {
	
	
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createProjectSelection(comp);

		createOtherOptions(comp);

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			fProjectText.setText(configuration.getAttribute(
					IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, ""));

			if (fProjectText.getText().length() == 0) {
				String newLaunchConfigName = autoFillBaseSettings();
				if (newLaunchConfigName != null) {
					ILaunchConfigurationWorkingCopy wConfig = configuration
							.getWorkingCopy();
					wConfig.rename(newLaunchConfigName);
					wConfig.doSave();// we do not need to handle to the new
					// ILaunchConfiguration since no future
					// access is needed
				}
			}

		} catch (CoreException e) {
			if (VdmDebugPlugin.DEBUG) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean isValid(ILaunchConfiguration config) {
		// TODO Auto-generated method stub
		setErrorMessage(null);
		if (getProject() == null || !getProject().exists()
				|| !getProject().getName().equals(fProjectText.getText())) {
			setErrorMessage("Project does not exist");
			return false;
		}

		if (!getProject().isOpen()) {
			setErrorMessage("Project is not open");
			return false;
		}

		try {
			Console.charset = getProject().getDefaultCharset();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return true;

	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT,
				fProjectText.getText());

		configuration.setAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL,
				IGuiBuilderConstants.REMOTE_CONTROL_CLASS);

		configuration.setAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE,
				checkBoxGenerateLatexCoverage.getSelection());
		
		configuration.setAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_MCDC_COVERAGE,
				checkBoxGenerateMCDCCoverage.getSelection());

		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT,
				defaultModule);

		// System.out.println("Expression: " + expression);
		configuration.setAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, expression);

		if (!fProjectText.getText().equals("")) {
			IResource[] resources = new IResource[] { (IResource) ResourcesPlugin
					.getWorkspace().getRoot()
					.getProject(fProjectText.getText()) };

			configuration.setMappedResources(resources);
		} else {
			configuration.setMappedResources(null);
		}
	}

	@Override
	public String getName() {
		return "GUIBuilder";
	}
	

}
