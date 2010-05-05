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
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;

public class VmArgumentsLaunchConfigurationTab extends AbstractLaunchConfigurationTab
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
	
	private Text fArgumentsText;
	private WidgetListener fListener = new WidgetListener();
	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		
//		Group group = new Group(comp, comp.getStyle());
//		group.setText("Arguments");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//
//		group.setLayoutData(gd);
//
//		GridLayout layout = new GridLayout();
//		layout.makeColumnsEqualWidth = false;
//		layout.numColumns = 3;
//		group.setLayout(layout);

		// editParent = group;

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
		return "VM Arguments";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			fArgumentsText.setText( configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, ""));
		} catch (CoreException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, fArgumentsText.getText());
		
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, "");
		
	}

}
