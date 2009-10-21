/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.overture.ide.vdmrt.debug.ui.tabs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.messages.DLTKLaunchConfigurationsMessages;
import org.eclipse.dltk.internal.core.SourceMethod;
import org.eclipse.dltk.internal.ui.filters.FieldsFilter;
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
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;

/**
 * Main launch configuration tab for overture scripts
 */
@SuppressWarnings("restriction")
public class VdmRtMainLaunchConfigurationTab extends MainLaunchConfigurationTab {

	public VdmRtMainLaunchConfigurationTab(String mode) {
		super(mode);
	}
//	private Button enableLogging;
	private Button fOperationButton;
	private Text fClassText;
	private Text fOperationText;
	
	private Button fdebugInConsole;
	private WidgetListener fListener = new WidgetListener(); 
		
	class WidgetListener implements ModifyListener, SelectionListener {
		public void modifyText(ModifyEvent e) {			
			validatePage();			
			updateLaunchConfigurationDialog();
		}
		

		public void widgetDefaultSelected(SelectionEvent e) {			
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e) {			
			fOperationText.setEnabled(!fdebugInConsole.getSelection());			
			updateLaunchConfigurationDialog();
		}
	}
	
	@Override
	protected boolean validate() {	
		return super.validate() && validateClass() && validateOperation();
	}
	
	private boolean validateOperation(){
//		try {
//			for (IModelElement element : this.getSourceModule().getChildren()) {
//				if (element instanceof SourceType) {
//					SourceType type = (SourceType) element;
//					if (type.getElementName().equals(fClassText.getText()) &&
//							type.getMethod(fOperationText.getText()).exists()) {
//						return true;
//					}
//				}
//			}
//		} catch (ModelException e) {
//			e.printStackTrace();
//		}
		//setErrorMessage("The operation: '" + fOperationText.getText() + "' does not exist in " + this.getSourceModule().getElementName());
		return true;


	}
	
	
	// TODO validate 
	private boolean validateClass() {
		return true;
//		try {
//			for (IModelElement element : this.getSourceModule().getChildren()) {
//				if (element instanceof SourceType) {
//					SourceType type = (SourceType) element;
//					if (type.getElementName().equals(fClassText.getText())) {
//						return true;
//					}
//				}
//			}
//			// todo temp
//			return true;
//		} catch (ModelException e) {
//			e.printStackTrace();
//		}
//		setErrorMessage("The class: '" + fClassText.getText() +"' does not exist in "+ this.getSourceModule().getElementName());
//		return false;
		

	}
	
	@Override
	protected void doCreateControl(Composite composite) {
		super.doCreateControl(composite);
		createOperationEditor(composite);

	}
	
	private void createOperationEditor(Composite parent){
		Group group = new Group(parent, parent.getStyle());
		group.setText("Operation:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);
		
		//editParent = group;
		
		
		Label label = new Label(group,SWT.MIN);
		label.setText("Class:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);
		
		
		fClassText = new Text(group, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fClassText.setLayoutData(gd);	
		fClassText.addModifyListener(fListener);
		
		fOperationButton = createPushButton(group,
				DLTKLaunchConfigurationsMessages.mainTab_projectButton, null);
		fOperationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleOperationButtonSelected();
			}
		});
		
		label = new Label(group,SWT.NORMAL);
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
	protected void createDebugOptions(Composite group) {
		super.createDebugOptions(group);
		org.eclipse.swt.widgets.Control[] temp = group.getChildren();
		
		temp[0]. setVisible(false);//HACK TO REMOVE "BREAK OF FIRST LINE
		temp[1]. setVisible(false);//HACK TO REMOVE "BREAK OF FIRST LINE
//		fdebugInConsole = SWTFactory.createCheckButton(group, OvertureDebugConstants.DEBUG_FROM_CONSOLE);
//		fdebugInConsole.addSelectionListener(fListener);		
	}

	
	/**
	 * Show a dialog that lets the user select a project. This in turn provides
	 * context for the main type, allowing the user to key a main type name, or
	 * constraining the search for main types to the specified project.
	 */
	protected void handleOperationButtonSelected() {
	    chooseOperation();
	}
	
	/**
	 * chooses a project for the type of launch config that it is
	 * 
	 * @return
	 */
	protected void chooseOperation() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle("Class and operation/function selection");
		dialog.setMessage(DLTKLaunchConfigurationsMessages.mainTab_searchButton_message);
		IScriptProject proj = getProject();
		if (proj == null)
		{
			return;
		}
		dialog.addFilter(new FieldsFilter());
		//dialog.addFilter(new MethodFilter()); TODO create a method filter 		
		dialog.setInput(this.getSourceModule());
		
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		if (dialog.open() == IDialogConstants.OK_ID) {
			SourceMethod method = (SourceMethod) dialog.getFirstResult();
			String className = method.getParent().getElementName();
			String operationName = method.getElementName() + "()";
			// check extension
			fClassText.setText(className);
			fOperationText.setText(operationName);
			//proj.getScriptProject().getScriptFolders()
		}
	}

	/*
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab#breakOnFirstLinePrefEnabled(org.eclipse.dltk.core.PreferencesLookupDelegate)
	 */
	protected boolean breakOnFirstLinePrefEnabled(
			PreferencesLookupDelegate delegate) {
		return false;
		//return delegate.getBoolean(OvertureDebugConstants.PLUGIN_ID,DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE);
	}
	
	/*
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab#dbpgLoggingPrefEnabled(org.eclipse.dltk.core.PreferencesLookupDelegate)
	 */
	protected boolean dbpgLoggingPrefEnabled(PreferencesLookupDelegate delegate) {
		return delegate.getBoolean(VdmRtDebugConstants.VDMRT_DEBUG_PLUGIN_ID, DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING);
	}
	
	

	/*
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab#getNatureID()
	 */
	protected String getNatureID() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}
	
	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		// cons
		config.setAttribute(VdmRtDebugConstants.VDMRT_DEBUGGING_CLASS, fClassText.getText());
		config.setAttribute(VdmRtDebugConstants.VDMRT_DEBUGGING_OPERATION, fOperationText.getText());
		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE, false);
		config.setAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING, true);
		//config.setAttribute(OvertureDebugConstants.DEBUGGING_FROM_CONSOLE, fdebugInConsole.getSelection());
		
		super.doPerformApply(config);
	}	
	
	@Override
	protected void updateMainModuleFromConfig(ILaunchConfiguration config) {
		try {			
			fClassText.setText(config.getAttribute(VdmRtDebugConstants.VDMRT_DEBUGGING_CLASS, ""));
			fOperationText.setText(config.getAttribute(VdmRtDebugConstants.VDMRT_DEBUGGING_OPERATION, ""));
			//fdebugInConsole.setSelection(config.getAttribute(DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING, true));
			//fOperationText.setEnabled(!fdebugInConsole.getSelection());
		} catch (CoreException e) {
			e.printStackTrace();
		}
//		String projectName = LaunchConfigurationUtils.getProjectName(config);
		
//		final IProject project = getWorkspaceRoot().getProject(projectName);
//		if (project.isAccessible()) {
//			final PreferencesLookupDelegate delegate = new PreferencesLookupDelegate(project);
//			if (fdebugInConsole != null) {
//				fdebugInConsole.setSelection(org.eclipse.dltk.internal.launching.LaunchConfigurationUtils.getBoolean(config,
//						ScriptLaunchConfigurationConstants.ENABLE_DBGP_LOGGING,
//						DebugFromConsolePrefEnabled(delegate)));
//			}				
//		}
		
		super.updateMainModuleFromConfig(config);
	}
	
}
