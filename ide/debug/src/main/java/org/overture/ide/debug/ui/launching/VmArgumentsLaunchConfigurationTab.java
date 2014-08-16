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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.utils.DebuggerProperties;
import org.overture.ide.debug.utils.ui.DebuggerPropertiesManager;

/**
 * Tab page to set the VM options used in the launch delegate. Posible options might be.
 * <ul>
 * <li>-Xmx1024M
 * <li>-Xss20M
 * <ul>
 * 
 * @author kela
 */
public class VmArgumentsLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab
{
	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			updateLaunchConfigurationDialog();
		}
	}

	private Text fArgumentsText;
	private WidgetListener fListener = new WidgetListener();
	DebuggerPropertiesManager propMan;

	public VmArgumentsLaunchConfigurationTab()
	{
		try
		{
			propMan = new DebuggerPropertiesManager(IDebugConstants.VDM_LAUNCH_CONFIG_CUSTOM_DEBUGGER_PROPERTIES, DebuggerProperties.getDefaults());
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		Group group = new Group(comp, SWT.NONE);
		group.setText("Java Virtual Machine custom arguments (e.g: -Xmx1024M -Xss20M):");

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		createVmInput(group);
		propMan.createControl(comp, fListener);
	}

	private void createVmInput(Composite comp)
	{
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		Label label = new Label(comp, SWT.MIN);
		label.setText("Arguments:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fArgumentsText = new Text(comp, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fArgumentsText.setLayoutData(gd);
		fArgumentsText.addModifyListener(fListener);
	}

	public String getName()
	{
		return "Debugger";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			fArgumentsText.setText(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, ""));
			propMan.initializeFrom(configuration);
		} catch (CoreException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				e.printStackTrace();
			}
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, fArgumentsText.getText());
		propMan.performApply(configuration);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, "");
		// try
		// {
		// propMan.setDefaults(DebuggerProperties.getDefaults(),configuration);
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }
	}

}
