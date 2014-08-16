/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;

public class VdmDevelopLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab
{
	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			// validatePage();
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			// fOperationText.setEnabled(!fdebugInConsole.getSelection());

			updateLaunchConfigurationDialog();
		}
	}

	private Button checkBoxRemoteDebug = null;
	private Button checkBoxEnableLogging = null;
	private Button checkBoxShowVmSettings = null;
	private Button checkBoxExperimentalTimeInvariantCheck = null;
	private WidgetListener fListener = new WidgetListener();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		Group group = new Group(comp, SWT.NONE);
		group.setText("Development options");
		// GridLayout layout = new GridLayout();
		// layout.numColumns = 1;
		// interperterGroup.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		checkBoxRemoteDebug = new Button(group, SWT.CHECK);
		checkBoxRemoteDebug.setText("Remote debug");
		checkBoxRemoteDebug.setSelection(false);
		checkBoxRemoteDebug.addSelectionListener(fListener);

		checkBoxEnableLogging = new Button(group, SWT.CHECK);
		checkBoxEnableLogging.setText("Enable logging");
		checkBoxEnableLogging.setSelection(false);
		checkBoxEnableLogging.addSelectionListener(fListener);

		checkBoxExperimentalTimeInvariantCheck = new Button(group, SWT.CHECK);
		checkBoxExperimentalTimeInvariantCheck.setText("Enable experimental time inv checks");
		checkBoxExperimentalTimeInvariantCheck.setSelection(false);
		checkBoxExperimentalTimeInvariantCheck.addSelectionListener(fListener);
		
		checkBoxShowVmSettings = new Button(group, SWT.CHECK);
		checkBoxShowVmSettings.setText("Show VM Settings");
		checkBoxShowVmSettings.setSelection(false);
		checkBoxShowVmSettings.addSelectionListener(fListener);
	}

	public String getName()
	{
		return "Develop";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			checkBoxRemoteDebug.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false));
			checkBoxEnableLogging.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false));
			checkBoxExperimentalTimeInvariantCheck.setSelection(configuration.getAttribute("vdm_launch_config_enable_realtime_time_inv_checks", false));
			checkBoxShowVmSettings.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_SHOW_VM_SETTINGS, false));

		} catch (CoreException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				VdmDebugPlugin.log(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, "Error in develop launch configuration tab", e));
			}
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, checkBoxRemoteDebug.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, checkBoxEnableLogging.getSelection());
		configuration.setAttribute("vdm_launch_config_enable_realtime_time_inv_checks", checkBoxExperimentalTimeInvariantCheck.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_SHOW_VM_SETTINGS, checkBoxShowVmSettings.getSelection());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false);
		configuration.setAttribute("vdm_launch_config_enable_realtime_time_inv_checks", false);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_SHOW_VM_SETTINGS, false);
	}

}
