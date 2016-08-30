/*
 * #%~
 * org.overture.ide.vdmrt.debug
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
package org.overture.ide.vdmrt.debug.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.overture.ide.debug.ui.launching.VdmRuntimeChecksLaunchConfigurationTab;
import org.overture.ide.vdmrt.debug.IVdmRtDebugConstants;

public class VdmRtRuntimeChecksLaunchConfigurationTab extends
		VdmRuntimeChecksLaunchConfigurationTab
{
	private Button checkBoxEnableRealTimeLog;

	@Override
	protected void createExtendableContent(Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText("VDM Real-Time options");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		checkBoxEnableRealTimeLog = new Button(group, SWT.CHECK);
		checkBoxEnableRealTimeLog.setText("Log Real-Time Events");
		checkBoxEnableRealTimeLog.addSelectionListener(fListener);
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		super.performApply(configuration);
		configuration.setAttribute(IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING, checkBoxEnableRealTimeLog.getSelection());
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		super.initializeFrom(configuration);
		try
		{
			checkBoxEnableRealTimeLog.setSelection(configuration.getAttribute(IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING, IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING_DEFAULT));
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		super.setDefaults(configuration);
		configuration.setAttribute(IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING, IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING_DEFAULT);
	}
}
