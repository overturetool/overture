/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.overture.ide.debug.ui.tabs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.messages.DLTKLaunchConfigurationsMessages;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.debug.core.DebugCoreConstants;
import org.overture.ide.debug.ui.DebugUIConstants;
import org.overture.ide.ui.outline.DisplayNameCreator;
import org.overture.ide.ui.outline.ExecutableFilter;
import org.overture.ide.ui.outline.VdmOutlineLabelProvider;
import org.overture.ide.ui.outline.VdmOutlineTreeContentProvider;
import org.overture.ide.utility.VdmProject;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;

/**
 * Main launch configuration tab for overture scripts
 */
public abstract class VdmMainLaunchConfigurationTab extends 
		MainLaunchConfigurationTab
{

	public VdmMainLaunchConfigurationTab(String mode) {
		super(mode);
	}

	// private Button enableLogging;
	private Button fOperationButton;
	private Text fModuleNameText;
	private Text fOperationText;
	private Button checkBoxgenerateLatexCoverage = null;
	private Button fdebugInConsole;
	private WidgetListener fListener = new WidgetListener();
private String vmOption = "";

public void setVmOptions(String option)
{
	this.vmOption = option;
}
	// private String moduleDefinitionPath;

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			validatePage();
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			fOperationText.setEnabled(!fdebugInConsole.getSelection());
			updateLaunchConfigurationDialog();
		}
	}

	@Override
	protected boolean validate()
	{
		return super.validate() && validateClass() && validateOperation();
	}

	private boolean validateOperation()
	{
		return !(fOperationText == null || fOperationText.getText().length() == 0);

	}

	private boolean validateClass()
	{
		return !(fModuleNameText == null || fModuleNameText.getText().length() == 0);

	}

	@Override
	protected void doCreateControl(Composite composite)
	{
		super.doCreateControl(composite);
		createOperationEditor(composite);

	}

	/*
	 * Default is class
	 */
	protected String getModuleLabelName()
	{
		return "Class";
	}

	private void createOperationEditor(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Operation:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		// editParent = group;

		Label label = new Label(group, SWT.MIN);
		label.setText(getModuleLabelName() + ":");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fModuleNameText = new Text(group, SWT.SINGLE | SWT.BORDER
				);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fModuleNameText.setLayoutData(gd);
		fModuleNameText.addModifyListener(fListener);

		fOperationButton = createPushButton(group,
				DLTKLaunchConfigurationsMessages.mainTab_projectButton,
				null);
		fOperationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				handleOperationButtonSelected();
			}
		});

		label = new Label(group, SWT.NORMAL);
		label.setText("Operation:");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gd);

		fOperationText = new Text(group, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fOperationText.setLayoutData(gd);
		fOperationText.addModifyListener(fListener);

		setControl(parent);
	}

	@Override
	protected void createMainModuleEditor(Composite parent, String text)
	{
		// we do not want this gui part
	}

	@Override
	protected String getScriptName()
	{
		return ".project";//Cant disable file check so we select a file which always will be there
	}

	@Override
	protected boolean validateScript()
	{
		return validateClass() && validateOperation();
		// return moduleDefinitionPath != null;
	}

	@Override
	protected void createDebugOptions(Composite group)
	{
		super.createDebugOptions(group);
		org.eclipse.swt.widgets.Control[] temp = group.getChildren();

		temp[0].setVisible(false);// HACK TO REMOVE "BREAK OF FIRST LINE
		temp[1].setVisible(false);// HACK TO REMOVE "BREAK OF FIRST LINE
		// fdebugInConsole = SWTFactory.createCheckButton(group,
		// OvertureDebugConstants.DEBUG_FROM_CONSOLE);
		// fdebugInConsole.addSelectionListener(fListener);

		checkBoxgenerateLatexCoverage = new Button(group, SWT.CHECK);
		checkBoxgenerateLatexCoverage.setText("Generate Latex coverage");
		checkBoxgenerateLatexCoverage.setSelection(false);
	}

	/**
	 * Show a dialog that lets the user select a project. This in turn provides
	 * context for the main type, allowing the user to key a main type name, or
	 * constraining the search for main types to the specified project.
	 */
	protected void handleOperationButtonSelected()
	{
		try
		{
			chooseOperation();
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * chooses a project for the type of launch config that it is
	 * 
	 * @return
	 * @throws CoreException
	 */
	protected void chooseOperation() throws CoreException
	{
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(),
				new VdmOutlineLabelProvider(),
				new VdmOutlineTreeContentProvider());
		dialog.setTitle(getModuleLabelName()
				+ " and operation/function selection");
		dialog.setMessage(DLTKLaunchConfigurationsMessages.mainTab_searchButton_message);
		IScriptProject proj = getProject();
		if (proj == null)
		{
			return;
		}

		dialog.addFilter(new ExecutableFilter());

		RootNode node = AstManager.instance()
				.getRootNode(getProject().getProject(), getNatureID());
		if (node == null)
		{
			new VdmProject(getProject().getProject()).typeCheck();
			node = AstManager.instance().getRootNode(getProject().getProject(),
					getNatureID());
		}
		dialog.setInput(node);

		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		if (dialog.open() == IDialogConstants.OK_ID)
		{
			Definition method = (Definition) dialog.getFirstResult();
			if (method.classDefinition != null)
			{
				boolean foundConstructor = false;
				for(Definition def : method.classDefinition.definitions)
				{
					if(def instanceof ExplicitOperationDefinition && ((ExplicitOperationDefinition)def).isConstructor)
					{
						foundConstructor = true;
						fModuleNameText.setText(DisplayNameCreator.getDisplayName(def));
					}
				}if(!foundConstructor)
				fModuleNameText.setText(DisplayNameCreator.getDisplayName(method.classDefinition)+"()");
			}
			else if (method.location != null && method.location.module != null)
				fModuleNameText.setText(method.location.module);
			else
				fModuleNameText.setText("DEFAULT");// undetermined module

			//fOperationText.setText(method.name.name + "()");
			fOperationText.setText(DisplayNameCreator.getDisplayName( method));
			
		}
	}

	/*
	 * @see
	 * org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab
	 * #
	 * breakOnFirstLinePrefEnabled(org.eclipse.dltk.core.PreferencesLookupDelegate
	 * )
	 */
	@Override
	protected boolean breakOnFirstLinePrefEnabled(
			PreferencesLookupDelegate delegate)
	{
		return false;
		// return
		// delegate.getBoolean(OvertureDebugConstants.PLUGIN_ID,DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE);
	}

	/*
	 * @see
	 * org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab
	 * #dbpgLoggingPrefEnabled(org.eclipse.dltk.core.PreferencesLookupDelegate)
	 */
	@Override
	protected boolean dbpgLoggingPrefEnabled(PreferencesLookupDelegate delegate)
	{
		return delegate.getBoolean(DebugUIConstants.PLUGIN_ID,
				DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING);
		// return true;
	}

	/*
	 * @see
	 * org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab
	 * #getNatureID()
	 */
	@Override
	protected abstract String getNatureID();

	// {
	// return VdmPpProjectNature.VDM_PP_NATURE;
	// }

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config)
	{
		// cons
		config.setAttribute(DebugCoreConstants.DEBUGGING_MODULE,
				fModuleNameText.getText());
		config.setAttribute(DebugCoreConstants.DEBUGGING_OPERATION,
				fOperationText.getText());

		if(vmOption!=null &&vmOption.trim().length()>0 )
			config.setAttribute(DebugCoreConstants.DEBUGGING_VM_MEMORY_OPTION, vmOption.trim());
		
		// config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
		// moduleDefinitionPath);
//		config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
//				"");
		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE,
				false);
		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING,
				true);
		config.setAttribute(DebugCoreConstants.DEBUGGING_CREATE_COVERAGE,
				checkBoxgenerateLatexCoverage.getSelection());

		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_CONNECTION_TIMEOUT,
				5000);
		
		
		
		super.doPerformApply(config);
	}

	@Override
	protected void updateMainModuleFromConfig(ILaunchConfiguration config)
	{
		try
		{
			fModuleNameText.setText(config.getAttribute(DebugCoreConstants.DEBUGGING_MODULE,
					""));
			fOperationText.setText(config.getAttribute(DebugCoreConstants.DEBUGGING_OPERATION,
					""));
			
			vmOption=	config.getAttribute(DebugCoreConstants.DEBUGGING_VM_MEMORY_OPTION,"");

		} catch (CoreException e)
		{
			e.printStackTrace();
		}
		// /super.updateMainModuleFromConfig(config);

	}

}
