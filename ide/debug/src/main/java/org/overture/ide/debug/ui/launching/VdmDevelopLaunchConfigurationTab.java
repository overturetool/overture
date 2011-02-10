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
	private WidgetListener fListener = new WidgetListener();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		
		
		
		Group group = new Group(comp, SWT.NONE);
		group.setText("Development options");
//		GridLayout layout = new GridLayout();
//		layout.numColumns = 1;
//		interperterGroup.setLayout(layout);
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
		} catch (CoreException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				VdmDebugPlugin.log(new Status(IStatus.ERROR,VdmDebugPlugin.PLUGIN_ID,"Error in develop launch configuration tab",e));
			}
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, checkBoxRemoteDebug.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, checkBoxEnableLogging.getSelection());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false);
	}

}
