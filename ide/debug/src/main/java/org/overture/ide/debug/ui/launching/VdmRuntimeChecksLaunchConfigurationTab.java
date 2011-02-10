package org.overture.ide.debug.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
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
	private Button checkBoxUsePostChecks = null;
	private Button checkBoxUsePreChecks = null;
	private Button checkBoxInvChecks = null;
	private Button checkBoxDynamicTypeChecks = null;
	private Button checkBoxUseMeasure = null;

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		createInterperterGroupCheckGroup(comp);
	}
	
	void createInterperterGroupCheckGroup(Composite controlGroup)
	{
		Group interperterGroup = new Group(controlGroup, SWT.NONE);
		interperterGroup.setText("Interpreting");
//		GridLayout layout = new GridLayout();
//		layout.numColumns = 1;
//		interperterGroup.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		interperterGroup.setLayoutData(gd);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		interperterGroup.setLayout(layout);

		checkBoxDynamicTypeChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxDynamicTypeChecks.setText("Dynamic type checks");
//		checkBoxDynamicTypeChecks.setSelection(project.hasDynamictypechecks());

		checkBoxInvChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxInvChecks.setText("Invariants checks");
//		checkBoxInvChecks.setSelection(project.hasInvchecks());

		checkBoxUsePreChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePreChecks.setText("Pre condition checks");
//		checkBoxUsePreChecks.setSelection(project.hasPrechecks());

		checkBoxUsePostChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePostChecks.setText("Post condition checks");
//		checkBoxUsePostChecks.setSelection(project.hasPostchecks());

		checkBoxUseMeasure = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseMeasure.setText("Measure Run-Time checks");
//		checkBoxUseMeasure.setSelection(project.hasMeasurechecks());

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
			
		} catch (CoreException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				VdmDebugPlugin.log(new Status(IStatus.ERROR,VdmDebugPlugin.PLUGIN_ID,"Error in vdmruntimechecks launch configuration tab",e));
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
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true);
	}

}
