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

public class VdmRuntimeChecksLaunchConfigurationTab extends
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

	protected WidgetListener fListener = new WidgetListener();
	private Button checkBoxUsePostChecks = null;
	private Button checkBoxUsePreChecks = null;
	private Button checkBoxInvChecks = null;
	private Button checkBoxDynamicTypeChecks = null;
	private Button checkBoxUseMeasure = null;
	private Button checkBoxUseStrictLetDef = null;

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		createInterperterGroupCheckGroup(comp);
		createExtendableContent(comp);
	}

	/**
	 * Enables sub classes to add groups to the existing view
	 * 
	 * @param comp
	 */
	protected void createExtendableContent(Composite comp)
	{

	}

	void createInterperterGroupCheckGroup(Composite controlGroup)
	{
		Group interperterGroup = new Group(controlGroup, SWT.NONE);
		interperterGroup.setText("Interpreting");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		interperterGroup.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		interperterGroup.setLayout(layout);

		checkBoxDynamicTypeChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxDynamicTypeChecks.setText("Dynamic type checks");
		checkBoxDynamicTypeChecks.addSelectionListener(fListener);

		checkBoxInvChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxInvChecks.setText("Invariants checks");
		checkBoxInvChecks.addSelectionListener(fListener);

		checkBoxUsePreChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePreChecks.setText("Pre condition checks");
		checkBoxUsePreChecks.addSelectionListener(fListener);

		checkBoxUsePostChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePostChecks.setText("Post condition checks");
		checkBoxUsePostChecks.addSelectionListener(fListener);

		checkBoxUseMeasure = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseMeasure.setText("Measure Run-Time checks");
		checkBoxUseMeasure.addSelectionListener(fListener);
		
		checkBoxUseStrictLetDef = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseStrictLetDef.setText("Strict let def checks");
		checkBoxUseStrictLetDef.addSelectionListener(fListener);

	}

	public String getName()
	{
		return "Runtime";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			checkBoxDynamicTypeChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true));
			checkBoxInvChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true));
			checkBoxUsePostChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true));
			checkBoxUsePreChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true));
			checkBoxUseMeasure.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true));
			checkBoxUseStrictLetDef.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STRICT_LET_DEF_CHECKS, false));

		} catch (CoreException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				VdmDebugPlugin.log(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, "Error in vdmruntimechecks launch configuration tab", e));
			}
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, checkBoxDynamicTypeChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, checkBoxInvChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, checkBoxUsePostChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, checkBoxUsePreChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, checkBoxUseMeasure.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STRICT_LET_DEF_CHECKS, checkBoxUseStrictLetDef.getSelection());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STRICT_LET_DEF_CHECKS, false);
	}

}
