/*
 * #%~
 * Code Generator Plugin
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
package org.overture.ide.plugins.codegen.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.ide.plugins.codegen.Activator;
import org.overture.ide.plugins.codegen.ICodeGenConstants;

public class WorkbenchPreferencePageJavaCodeGen extends PreferencePage implements
		IWorkbenchPreferencePage
{
	private Button disableCloningCheckBox;
	private Button genAsStrCheckBox;
	private Button genConcMechanismsCheckBox;
	private Text classesToSkipField;
	private Text packageField;
	private Button genJmlCheckBox;
	private Button jmlUseInvForCheckBox;
	
	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		disableCloningCheckBox = new Button(composite, SWT.CHECK);
		disableCloningCheckBox.setText("Disable cloning");

		genAsStrCheckBox = new Button(composite, SWT.CHECK);
		genAsStrCheckBox.setText("Generate character sequences as strings");
		
		genConcMechanismsCheckBox = new Button(composite, SWT.CHECK);
		genConcMechanismsCheckBox.setText("Generate concurrency mechanisms (VDM++ only)");
		
		genJmlCheckBox = new Button(composite, SWT.CHECK);
		genJmlCheckBox.setText("Generate JML (Java Modeling Language) annotations (VDM-SL only)");
		
		jmlUseInvForCheckBox = new Button(composite, SWT.CHECK);
		jmlUseInvForCheckBox.setText("Use JML \\invariant_for to explicitly check record invariants");

		Label packageLabel = new Label(composite, SWT.NULL);
		packageLabel.setText("Output package of the generated Java code (e.g. my.pack)");
		final GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.FILL;
		packageField = new Text(composite, SWT.BORDER);
		packageField.setLayoutData(gridData2);
		packageField.setText("");
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		Label label = new Label(parent, SWT.NULL);
		label.setText("Classes/modules that should not be code generated. Separate by ';' (e.g. World; Env)");
		classesToSkipField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		classesToSkipField.setLayoutData(gridData);

		refreshControls();

		return composite;
	}

	@Override
	protected void performApply()
	{
		apply();
		super.performApply();
	}
	
	@Override
	public boolean performOk()
	{
		apply();
		return super.performOk();
	}

	private void apply()
	{
		IPreferenceStore store = doGetPreferenceStore();
		
		boolean disableCloning = disableCloningCheckBox.getSelection();
		store.setDefault(ICodeGenConstants.DISABLE_CLONING, disableCloning);
		
		boolean genAsStrings = genAsStrCheckBox.getSelection();
		store.setDefault(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRINGS, genAsStrings);
		
		boolean genConcMechanisms = genConcMechanismsCheckBox.getSelection();
		store.setDefault(ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS, genConcMechanisms);
		
		boolean genJml = genJmlCheckBox.getSelection();
		store.setDefault(ICodeGenConstants.GENERATE_JML, genJml);
		
		boolean jmlUseInvFor = jmlUseInvForCheckBox.getSelection();
		store.setDefault(ICodeGenConstants.JML_USE_INVARIANT_FOR, jmlUseInvFor);
		
		String userSpecifiedClassesToSkip = classesToSkipField.getText();
		store.setDefault(ICodeGenConstants.CLASSES_TO_SKIP, userSpecifiedClassesToSkip);
		
		String javaPackage = packageField.getText().trim();
		
		if(javaPackage.isEmpty())
		{
			// The project name will be used as the package
		}
		else if(JavaCodeGenUtil.isValidJavaPackage(javaPackage))
		{
			store.setDefault(ICodeGenConstants.JAVA_PACKAGE, javaPackage);
		}
		else
		{
			Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
					| SWT.OK);

			messageBox.setText("Not a valid Java package!");
			messageBox.setMessage("Please specify a valid java package (e.g. my.pack).");
			messageBox.open();
			
			// To indicate that we do not want the user specified package to be saved
			javaPackage = null;
		}

		Activator.savePluginSettings(disableCloning, genAsStrings, genConcMechanisms, genJml, jmlUseInvFor, userSpecifiedClassesToSkip, javaPackage);
		
		refreshControls();
	}

	@Override
	protected void performDefaults()
	{
		super.performDefaults();
		
		if(disableCloningCheckBox != null)
		{
			disableCloningCheckBox.setSelection(ICodeGenConstants.DISABLE_CLONING_DEFAULT);
		}
		
		if(genAsStrCheckBox != null)
		{
			genAsStrCheckBox.setSelection(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRING_DEFAULT);
		}
		
		if(genConcMechanismsCheckBox != null)
		{
			genConcMechanismsCheckBox.setSelection(ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS_DEFAULT);
		}
		
		if(genJmlCheckBox != null)
		{
			genJmlCheckBox.setSelection(ICodeGenConstants.GENERATE_JML_DEFAULT);
		}
		
		if(jmlUseInvForCheckBox != null)
		{
			jmlUseInvForCheckBox.setSelection(ICodeGenConstants.JML_USE_INVARIANT_FOR_DEFAULT);
		}
		
		if(classesToSkipField != null)
		{
			classesToSkipField.setText(ICodeGenConstants.CLASSES_TO_SKIP_DEFAULT);
		}
		
		if(packageField != null)
		{
			packageField.setText(ICodeGenConstants.JAVA_PACKAGE_DEFAULT);
		}
	}
	
	@Override
	public void init(IWorkbench workbench)
	{
		refreshControls();
	}

	private void refreshControls()
	{
		Preferences preferences = InstanceScope.INSTANCE.getNode(ICodeGenConstants.PLUGIN_ID);

		if (disableCloningCheckBox != null)
		{
			disableCloningCheckBox.setSelection(preferences.getBoolean(ICodeGenConstants.DISABLE_CLONING, ICodeGenConstants.DISABLE_CLONING_DEFAULT));
		}

		if (genAsStrCheckBox != null)
		{
			genAsStrCheckBox.setSelection(preferences.getBoolean(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRINGS, ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRING_DEFAULT));
		}
		
		if(genConcMechanismsCheckBox != null)
		{
			genConcMechanismsCheckBox.setSelection(preferences.getBoolean(ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS, ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS_DEFAULT));
		}
		
		if(genJmlCheckBox != null)
		{
			genJmlCheckBox.setSelection(preferences.getBoolean(ICodeGenConstants.GENERATE_JML, ICodeGenConstants.GENERATE_JML_DEFAULT));
		}
		
		if(jmlUseInvForCheckBox != null)
		{
			jmlUseInvForCheckBox.setSelection(preferences.getBoolean(ICodeGenConstants.JML_USE_INVARIANT_FOR, ICodeGenConstants.JML_USE_INVARIANT_FOR_DEFAULT));
		}
		
		if (classesToSkipField != null)
		{
			classesToSkipField.setText(preferences.get(ICodeGenConstants.CLASSES_TO_SKIP, ICodeGenConstants.CLASSES_TO_SKIP_DEFAULT));
		}
		
		if(packageField != null)
		{
			packageField.setText(preferences.get(ICodeGenConstants.JAVA_PACKAGE, ICodeGenConstants.JAVA_PACKAGE_DEFAULT));
		}
	}
}