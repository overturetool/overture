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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.model.IVdmBreakpoint;

@SuppressWarnings("restriction")
public class VdmBreakpointPropertyPage extends PropertyPage
{

	// protected JavaElementLabelProvider fJavaLabelProvider= new
	// JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
	protected Button fEnabledButton;
	protected Button fHitValueButton;
	protected Text fHitValueText;
	protected Combo fSuspendPolicy;
	protected List<String> fErrorMessages = new ArrayList<String>();

	/**
	 * Attribute used to indicate that a breakpoint should be deleted when cancel is pressed.
	 */
	public static final String ATTR_DELETE_ON_CANCEL = IDebugConstants.PLUGIN_ID
			+ ".ATTR_DELETE_ON_CANCEL"; //$NON-NLS-1$

	/**
	 * Constant for the empty string
	 */
	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * the hit count error message
	 */
	private static final String fgHitCountErrorMessage = "Hit count must be a positive integer";// PropertyPageMessages.JavaBreakpointPage_0;

	/**
	 * Store the breakpoint properties.
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		IWorkspaceRunnable wr = new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor) throws CoreException
			{
				IVdmBreakpoint breakpoint = getBreakpoint();
				boolean delOnCancel = breakpoint.getMarker().getAttribute(ATTR_DELETE_ON_CANCEL) != null;
				if (delOnCancel)
				{
					// if this breakpoint is being created, remove the "delete on cancel" attribute
					// and register with the breakpoint manager
					breakpoint.getMarker().setAttribute(ATTR_DELETE_ON_CANCEL, (String) null);
					breakpoint.setRegistered(true);
				}
				doStore();
			}
		};
		try
		{
			ResourcesPlugin.getWorkspace().run(wr, null, 0, null);
		} catch (CoreException e)
		{
			// JDIDebugUIPlugin.statusDialog(e.getStatus());
			// JDIDebugUIPlugin.log(e);
		}
		return super.performOk();
	}

	/**
	 * Adds the given error message to the errors currently displayed on this page. The page displays the most recently
	 * added error message. Clients should retain messages that are passed into this method as the message should later
	 * be passed into removeErrorMessage(String) to clear the error. This method should be used instead of
	 * setErrorMessage(String).
	 * 
	 * @param message
	 *            the error message to display on this page.
	 */
	protected void addErrorMessage(String message)
	{
		fErrorMessages.remove(message);
		fErrorMessages.add(message);
		setErrorMessage(message);
		setValid(message == null);
	}

	/**
	 * Removes the given error message from the errors currently displayed on this page. When an error message is
	 * removed, the page displays the error that was added before the given message. This is akin to popping the message
	 * from a stack. Clients should call this method instead of setErrorMessage(null).
	 * 
	 * @param message
	 *            the error message to clear
	 */
	protected void removeErrorMessage(String message)
	{
		fErrorMessages.remove(message);
		if (fErrorMessages.isEmpty())
		{
			addErrorMessage(null);
		} else
		{
			addErrorMessage((String) fErrorMessages.get(fErrorMessages.size() - 1));
		}
	}

	/**
	 * Stores the values configured in this page. This method should be called from within a workspace runnable to
	 * reduce the number of resource deltas.
	 */
	protected void doStore() throws CoreException
	{
		IVdmBreakpoint breakpoint = getBreakpoint();
		// storeSuspendPolicy(breakpoint);
		storeHitCount(breakpoint);
		storeEnabled(breakpoint);
	}

	/**
	 * Stores the value of the enabled state in the breakpoint.
	 * 
	 * @param breakpoint
	 *            the breakpoint to update
	 * @throws CoreException
	 *             if an exception occurs while setting the enabled state
	 */
	private void storeEnabled(IVdmBreakpoint breakpoint) throws CoreException
	{
		breakpoint.setEnabled(fEnabledButton.getSelection());
	}

	/**
	 * Stores the value of the hit count in the breakpoint.
	 * 
	 * @param breakpoint
	 *            the breakpoint to update
	 * @throws CoreException
	 *             if an exception occurs while setting the hit count
	 */
	private void storeHitCount(IVdmBreakpoint breakpoint) throws CoreException
	{
		int hitCount = -1;
		// if (fHitCountButton.getSelection())
		// {
		try
		{
			hitCount = Integer.parseInt(fHitValueText.getText());
		} catch (NumberFormatException e)
		{
			//				JDIDebugUIPlugin.log(new Status(IStatus.ERROR, JDIDebugUIPlugin.getUniqueIdentifier(), IStatus.ERROR, MessageFormat.format("JavaBreakpointPage allowed input of invalid string for hit count value: {0}.", new String[] { fHitCountText.getText() }), e));  //$NON-NLS-1$
		}
		// }
		breakpoint.setHitValue(hitCount);
		breakpoint.setHitCondition(fHitValueButton.getSelection() ? IVdmBreakpoint.HIT_CONDITION_GREATER_OR_EQUAL
				: -1);
	}

	/**
	 * Stores the value of the suspend policy in the breakpoint.
	 * 
	 * @param breakpoint
	 *            the breakpoint to update
	 * @throws CoreException
	 *             if an exception occurs while setting the suspend policy
	 */
	// private void storeSuspendPolicy(IVdmBreakpoint breakpoint) throws CoreException {
	// int suspendPolicy = IVdmBreakpoint.SUSPEND_VM;
	// if(fSuspendPolicy.getSelectionIndex() == 0) {
	// suspendPolicy = IVdmBreakpoint.SUSPEND_THREAD;
	// }
	// breakpoint.setSuspendPolicy(suspendPolicy);
	// }

	/**
	 * Creates the labels and editors displayed for the breakpoint.
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		setTitle("TODO: property title (VdmBreakpointPropertyPage)");
		noDefaultAndApplyButton();
		Composite mainComposite = createComposite(parent, 1);
		createLabels(mainComposite);
		try
		{
			createEnabledButton(mainComposite);
			createHitValueEditor(mainComposite);
			createTypeSpecificEditors(mainComposite);
			// createSuspendPolicyEditor(mainComposite); // Suspend policy is considered uncommon. Add it last.
		} catch (CoreException e)
		{
			;
		}
		setValid(true);
		// if this breakpoint is being created, change the shell title to indicate 'creation'
		try
		{
			if (getBreakpoint().getMarker().getAttribute(ATTR_DELETE_ON_CANCEL) != null)
			{
				getShell().addShellListener(new ShellListener()
				{
					public void shellActivated(ShellEvent e)
					{
						Shell shell = (Shell) e.getSource();
						shell.setText("TEXT HERE (VdmBreakpointPropertyPage)");// MessageFormat.format("TODO: property page 10 (VdmBreakpointPropertyPage)",
																				// new
																				// String[]{getName(getBreakpoint())}));
						shell.removeShellListener(this);
					}

					public void shellClosed(ShellEvent e)
					{
					}

					public void shellDeactivated(ShellEvent e)
					{
					}

					public void shellDeiconified(ShellEvent e)
					{
					}

					public void shellIconified(ShellEvent e)
					{
					}
				});
			}
		} catch (CoreException e)
		{
		}
		return mainComposite;
	}

	// /**
	// * Returns the name of the given element.
	// *
	// * @param element the element
	// * @return the name of the element
	// */
	// private String getName(IAdaptable element) {
	// IWorkbenchAdapter adapter = (IWorkbenchAdapter) element.getAdapter(IWorkbenchAdapter.class);
	// if (adapter != null) {
	// return adapter.getLabel(element);
	// }
	// return EMPTY_STRING;
	// }
	//
	/**
	 * Creates the labels displayed for the breakpoint.
	 * 
	 * @param parent
	 */
	protected void createLabels(Composite parent)
	{
		Composite labelComposite = createComposite(parent, 2);
		try
		{
			String typeName = ((IVdmBreakpoint) getElement()).getMessage();
			if (typeName != null)
			{
				String s = getTypeName(typeName);
				createLabel(labelComposite, "Filename:");
				Text text = SWTFactory.createText(labelComposite, SWT.READ_ONLY, 1, 1);
				text.setText(s);
				text.setBackground(parent.getBackground());
			}
			createTypeSpecificLabels(labelComposite);
		} catch (CoreException ce)
		{
			// JDIDebugUIPlugin.log(ce);
		}
	}

	/**
	 * Creates the editor for configuring the suspend policy (suspend VM or suspend thread) of the breakpoint.
	 * 
	 * @param parent
	 *            the composite in which the suspend policy editor will be created.
	 */
	// private void createSuspendPolicyEditor(Composite parent) throws CoreException {
	// Composite comp = createComposite(parent, 2);
	// createLabel(comp, "Suspend Policy");
	// boolean suspendThread= getBreakpoint().getSuspendPolicy() == IVdmBreakpoint.SUSPEND_THREAD;
	// fSuspendPolicy = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
	// fSuspendPolicy.add("Suspend Thread");
	// fSuspendPolicy.add("Suspend VM");
	// fSuspendPolicy.select(1);
	// if(suspendThread) {
	// fSuspendPolicy.select(0);
	// }
	// }

	private String getTypeName(String typeName)
	{
		String[] split = typeName.split(":");
		String res = "";

		if (split.length == 3)
		{
			String[] split2 = split[1].split("\\[");
			if (split2.length == 2)
			{
				res = split2[0];
			}
		}

		return res;
	}

	/**
	 * @param parent
	 *            the composite in which the hit count editor will be created
	 */
	private void createHitValueEditor(Composite parent) throws CoreException
	{
		Composite hitCountComposite = createComposite(parent, 2);
		fHitValueButton = createCheckButton(hitCountComposite, "Hit Counter:");
		fHitValueButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				fHitValueText.setEnabled(fHitValueButton.getSelection());
				hitCountChanged();
			}
		});
		int hitValue = getBreakpoint().getHitValue();
		String hitValueString = EMPTY_STRING;
		if (hitValue > 0)
		{
			hitValueString = new Integer(hitValue).toString();
		}
		if (getBreakpoint().getHitCondition() != -1)
		{

			fHitValueButton.setSelection(true);
		} else
		{
			fHitValueButton.setSelection(false);
		}
		fHitValueText = createText(hitCountComposite, hitValueString);
		// if (hitCount <= 0)
		if (!fHitValueButton.getSelection())
		{
			fHitValueText.setEnabled(false);
		}
		fHitValueText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				hitCountChanged();
			}
		});
	}

	/**
	 * Validates the current state of the hit count editor. Hit count value must be a positive integer.
	 */
	private void hitCountChanged()
	{
		if (!fHitValueButton.getSelection())
		{
			removeErrorMessage(fgHitCountErrorMessage);
			return;
		}
		String hitCountText = fHitValueText.getText();
		int hitCount = -1;
		try
		{
			hitCount = Integer.parseInt(hitCountText);
		} catch (NumberFormatException e1)
		{
			addErrorMessage(fgHitCountErrorMessage);
			return;
		}
		if (hitCount < 1)
		{
			addErrorMessage(fgHitCountErrorMessage);
		} else
		{
			removeErrorMessage(fgHitCountErrorMessage);
		}
	}

	/**
	 * Creates the button to toggle enablement of the breakpoint
	 * 
	 * @param parent
	 * @throws CoreException
	 */
	protected void createEnabledButton(Composite parent) throws CoreException
	{
		fEnabledButton = createCheckButton(parent, "Enable");
		fEnabledButton.setSelection(getBreakpoint().isEnabled());
	}

	/**
	 * Returns the breakpoint that this preference page configures
	 * 
	 * @return the breakpoint this page configures
	 */
	protected IVdmBreakpoint getBreakpoint()
	{
		return (IVdmBreakpoint) getElement();
	}

	/**
	 * Allows subclasses to add type specific labels to the common Java breakpoint page.
	 * 
	 * @param parent
	 */
	protected void createTypeSpecificLabels(Composite parent)
	{
	}

	/**
	 * Allows subclasses to add type specific editors to the common Java breakpoint page.
	 * 
	 * @param parent
	 */
	protected void createTypeSpecificEditors(Composite parent)
			throws CoreException
	{
	}

	/**
	 * Creates a fully configured text editor with the given initial value
	 * 
	 * @param parent
	 * @param initialValue
	 * @return the configured text editor
	 */
	protected Text createText(Composite parent, String initialValue)
	{
		Text t = SWTFactory.createText(parent, SWT.SINGLE | SWT.BORDER, 1);
		t.setText(initialValue);
		return t;
	}

	/**
	 * Creates a fully configured composite with the given number of columns
	 * 
	 * @param parent
	 * @param numColumns
	 * @return the configured composite
	 */
	protected Composite createComposite(Composite parent, int numColumns)
	{
		return SWTFactory.createComposite(parent, parent.getFont(), numColumns, 1, GridData.FILL_HORIZONTAL, 0, 0);
	}

	/**
	 * Creates a fully configured check button with the given text.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the label of the returned check button
	 * @return a fully configured check button
	 */
	protected Button createCheckButton(Composite parent, String text)
	{
		return SWTFactory.createCheckButton(parent, text, null, false, 1);
	}

	/**
	 * Creates a fully configured label with the given text.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the test of the returned label
	 * @return a fully configured label
	 */
	protected Label createLabel(Composite parent, String text)
	{
		return SWTFactory.createLabel(parent, text, 1);
	}

	/**
	 * Creates a fully configured radio button with the given text.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the label of the returned radio button
	 * @return a fully configured radio button
	 */

	protected Button createRadioButton(Composite parent, String text)
	{
		return SWTFactory.createRadioButton(parent, text);
	}

	/**
	 * Check to see if the breakpoint should be deleted.
	 */
	public boolean performCancel()
	{
		try
		{
			if (getBreakpoint().getMarker().getAttribute(ATTR_DELETE_ON_CANCEL) != null)
			{
				// if this breakpoint is being created, delete on cancel
				getBreakpoint().delete();
			}
		} catch (CoreException e)
		{
			// JDIDebugUIPlugin.statusDialog(PropertyPageMessages.JavaBreakpointPage_9, e.getStatus());
		}
		return super.performCancel();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		// TODO:PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IJavaDebugHelpContextIds.JAVA_BREAKPOINT_PROPERTY_PAGE);
	}

}
