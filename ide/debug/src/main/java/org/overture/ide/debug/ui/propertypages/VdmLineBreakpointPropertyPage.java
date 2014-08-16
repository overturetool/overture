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
package org.overture.ide.debug.ui.propertypages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.overture.ide.debug.core.model.IVdmLineBreakpoint;

@SuppressWarnings("restriction")
public class VdmLineBreakpointPropertyPage extends VdmBreakpointPropertyPage
		implements IWorkbenchPropertyPage
{

	private Button fEnableConditionButton;
	private BreakpointConditionEditor fConditionEditor;
	private Button fConditionIsTrue;
	private Button fConditionHasChanged;
	private Label fSuspendWhenLabel;

	// Watchpoint editors
	// private Button fFieldAccess;
	// private Button fFieldModification;
	// // Method breakpoint editors
	// private Button fMethodEntry;
	// private Button fMethodExit;
	//
	// private static final String fgWatchpointError =
	// "fgWatchpointError";//PropertyPageMessages.JavaLineBreakpointPage_0;
	// private static final String fgMethodBreakpointError =
	// "fgMethodBreakpointError";//PropertyPageMessages.JavaLineBreakpointPage_1;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.ui.propertypages.JavaBreakpointPage#doStore()
	 */
	protected void doStore() throws CoreException
	{
		IVdmLineBreakpoint breakpoint = (IVdmLineBreakpoint) getBreakpoint();
		super.doStore();
		if (fConditionEditor != null)
		{
			boolean enableCondition = fEnableConditionButton.getSelection();
			String condition = fConditionEditor.getCondition();
			// boolean suspendOnTrue= fConditionIsTrue.getSelection();
			if (breakpoint.getExpressionState() != enableCondition)
			{
				breakpoint.setExpressionState(enableCondition);
			}
			if (!condition.equals(breakpoint.getExpression()))
			{
				breakpoint.setExpression(condition);
			}
			// if (breakpoint.isConditionSuspendOnTrue() != suspendOnTrue) {
			// breakpoint.setConditionSuspendOnTrue(suspendOnTrue);
			// }
		}
		// if (breakpoint instanceof IJavaWatchpoint) {
		// IJavaWatchpoint watchpoint= (IJavaWatchpoint) getBreakpoint();
		// boolean access = fFieldAccess.getSelection();
		// boolean modification = fFieldModification.getSelection();
		// if (access != watchpoint.isAccess()) {
		// watchpoint.setAccess(access);
		// }
		// if (modification != watchpoint.isModification()) {
		// watchpoint.setModification(modification);
		// }
		// }
		// if (breakpoint instanceof IJavaMethodBreakpoint) {
		// IJavaMethodBreakpoint methodBreakpoint= (IJavaMethodBreakpoint) getBreakpoint();
		// boolean entry = fMethodEntry.getSelection();
		// boolean exit = fMethodExit.getSelection();
		// if (entry != methodBreakpoint.isEntry()) {
		// methodBreakpoint.setEntry(entry);
		// }
		// if (exit != methodBreakpoint.isExit()) {
		// methodBreakpoint.setExit(exit);
		// }
		// }
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jdt.internal.debug.ui.propertypages.JavaBreakpointPage#createTypeSpecificLabels(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected void createTypeSpecificLabels(Composite parent)
	{
		// Line number
		IVdmLineBreakpoint breakpoint = (IVdmLineBreakpoint) getBreakpoint();
		StringBuffer lineNumber = new StringBuffer(4);
		try
		{
			int lNumber = breakpoint.getLineNumber();
			if (lNumber > 0)
			{
				lineNumber.append(lNumber);
			}
		} catch (CoreException ce)
		{
			// JDIDebugUIPlugin.log(ce);
		}
		if (lineNumber.length() > 0)
		{
			createLabel(parent, "Line:");
			Text text = SWTFactory.createText(parent, SWT.READ_ONLY, 1, 1);
			text.setText(lineNumber.toString());
			text.setBackground(parent.getBackground());
		}
		// IMember member = BreakpointUtils.getMember(breakpoint);
		// if (member == null) {
		// return;
		// }

		// TODO: I removed this label until we find a solution to fidn the member

		// String label = "Member:"; //PropertyPageMessages.JavaLineBreakpointPage_3;
		// if (breakpoint instanceof IJavaMethodBreakpoint) {
		// label = PropertyPageMessages.JavaLineBreakpointPage_4;
		// } else if (breakpoint instanceof IJavaWatchpoint) {
		// label = PropertyPageMessages.JavaLineBreakpointPage_5;
		// }
		// createLabel(parent, label);

		// TODO: the member should be reated here

		// Text text = SWTFactory.createText(parent, SWT.READ_ONLY, 1, 1);
		// text.setText("fJavaLabelProvider.getText(member)");
		// text.setBackground(parent.getBackground());
	}

	/**
	 * Create the condition editor and associated editors.
	 * 
	 * @see org.eclipse.jdt.internal.debug.ui.propertypages.JavaBreakpointPage#createTypeSpecificEditors(org.eclipse.swt.widgets.Composite)
	 */
	protected void createTypeSpecificEditors(Composite parent)
			throws CoreException
	{
		setTitle("Line Breakpoint");
		IVdmLineBreakpoint breakpoint = (IVdmLineBreakpoint) getBreakpoint();
		// if (breakpoint..supportsCondition()) {
		createConditionEditor(parent);
		// }
		// if (breakpoint instanceof IJavaWatchpoint) {
		// setTitle(PropertyPageMessages.JavaLineBreakpointPage_19);
		// IJavaWatchpoint watchpoint= (IJavaWatchpoint) getBreakpoint();
		// SelectionAdapter watchpointValidator= new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// validateWatchpoint();
		// }
		// };
		// createLabel(parent, PropertyPageMessages.JavaLineBreakpointPage_6);
		// fEnabledButton.addSelectionListener(watchpointValidator);
		// fFieldAccess = createCheckButton(parent, PropertyPageMessages.JavaLineBreakpointPage_7);
		// fFieldAccess.setSelection(watchpoint.isAccess());
		// fFieldAccess.addSelectionListener(watchpointValidator);
		// fFieldModification = createCheckButton(parent, PropertyPageMessages.JavaLineBreakpointPage_8);
		// fFieldModification.setSelection(watchpoint.isModification());
		// fFieldModification.addSelectionListener(watchpointValidator);
		// }
		// if (breakpoint instanceof IJavaMethodBreakpoint) {
		// setTitle(PropertyPageMessages.JavaLineBreakpointPage_20);
		// IJavaMethodBreakpoint methodBreakpoint = (IJavaMethodBreakpoint) getBreakpoint();
		// SelectionAdapter methodBreakpointValidator= new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// validateMethodBreakpoint();
		// }
		// };
		// createLabel(parent, PropertyPageMessages.JavaLineBreakpointPage_9);
		// fEnabledButton.addSelectionListener(methodBreakpointValidator);
		// fMethodEntry = createCheckButton(parent, PropertyPageMessages.JavaLineBreakpointPage_10);
		// fMethodEntry.setSelection(methodBreakpoint.isEntry());
		// fMethodEntry.addSelectionListener(methodBreakpointValidator);
		// fMethodExit = createCheckButton(parent, PropertyPageMessages.JavaLineBreakpointPage_11);
		// fMethodExit.setSelection(methodBreakpoint.isExit());
		// fMethodExit.addSelectionListener(methodBreakpointValidator);
		// }
	}

	/**
	 * Creates the controls that allow the user to specify the breakpoint's condition
	 * 
	 * @param parent
	 *            the composite in which the condition editor should be created
	 * @throws CoreException
	 *             if an exception occurs accessing the breakpoint
	 */
	private void createConditionEditor(Composite parent) throws CoreException
	{
		IVdmLineBreakpoint breakpoint = (IVdmLineBreakpoint) getBreakpoint();
		String label = null;
		// if (BreakpointUtils.getType(breakpoint) != null) {
		// IBindingService bindingService =
		// (IBindingService)PlatformUI.getWorkbench().getAdapter(IBindingService.class);
		// if(bindingService != null) {
		// TriggerSequence keyBinding =
		// bindingService.getBestActiveBindingFor(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		// if (keyBinding != null) {
		// label = MessageFormat.format("messageFormat", new String[] {keyBinding.format()});
		// }
		// }
		// }
		if (label == null)
		{
			label = "Enable Condition";
		}

		Composite conditionComposite = SWTFactory.createGroup(parent, EMPTY_STRING, 1, 1, GridData.FILL_BOTH);
		fEnableConditionButton = createCheckButton(conditionComposite, label);
		fEnableConditionButton.setSelection(breakpoint.getExpressionState());
		fEnableConditionButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				setConditionEnabled(fEnableConditionButton.getSelection());
			}
		});
		fConditionEditor = new BreakpointConditionEditor(conditionComposite, this);
		fSuspendWhenLabel = createLabel(conditionComposite, "Suspend when:");
		fConditionIsTrue = createRadioButton(conditionComposite, "condition is 'true'");
		fConditionHasChanged = createRadioButton(conditionComposite, "value of condition changes");
		// if (breakpoint.isConditionSuspendOnTrue()) {
		// fConditionIsTrue.setSelection(true);
		// }
		// else {
		fConditionHasChanged.setSelection(true);
		// }
		setConditionEnabled(fEnableConditionButton.getSelection());
	}

	/**
	 * Sets the enabled state of the condition editing controls.
	 * 
	 * @param enabled
	 */
	private void setConditionEnabled(boolean enabled)
	{
		fConditionEditor.setEnabled(enabled);
		fSuspendWhenLabel.setEnabled(enabled);
		fConditionIsTrue.setEnabled(enabled);
		fConditionHasChanged.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#convertHeightInCharsToPixels(int)
	 */
	public int convertHeightInCharsToPixels(int chars)
	{
		return super.convertHeightInCharsToPixels(chars);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#convertWidthInCharsToPixels(int)
	 */
	public int convertWidthInCharsToPixels(int chars)
	{
		return super.convertWidthInCharsToPixels(chars);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
	 */
	public void dispose()
	{
		if (fConditionEditor != null)
		{
			fConditionEditor.dispose();
		}
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), "context"); // TODO: HELP CONTEXT
	}

}
