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
			checkBoxEnableRealTimeLog.setSelection(configuration.getAttribute(IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING, true));
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
		configuration.setAttribute(IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING, true);
	}
}
