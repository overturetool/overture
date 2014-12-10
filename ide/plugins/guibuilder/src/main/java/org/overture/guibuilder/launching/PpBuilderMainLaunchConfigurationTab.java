/*
 * #%~
 * org.overture.ide.vdmpp.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
import org.overture.ide.vdmpp.debug.ui.launching.VdmPpMainLaunchConfigurationTab;
import org.overture.parser.messages.Console;

/**
 * Main configuration Tab for GUI Models. Inherits
 * {@link VdmPpMainLaunchConfigurationTab} and redefines it as remote launch only. 
 * The remote control class is locked to {@link GuiBuilderRemote}.
 * 
 * @author ldc
 *
 */
public class PpBuilderMainLaunchConfigurationTab extends
		VdmPpMainLaunchConfigurationTab {

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
