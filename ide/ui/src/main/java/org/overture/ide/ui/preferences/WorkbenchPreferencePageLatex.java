package org.overture.ide.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;

public class WorkbenchPreferencePageLatex extends FieldEditorPreferencePage implements
IWorkbenchPreferencePage {

	public void init(IWorkbench workbench) {
		
		
	}

	@Override
	protected void createFieldEditors() {
		
		
		addField(new FileFieldEditor(IVdmUiConstants.OSX_LATEX_PATH_PREFERENCE, "MacOS Latex Path", getFieldEditorParent()));
		
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return VdmUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(IVdmUiConstants.OSX_LATEX_PATH_PREFERENCE, IVdmUiConstants.DEFAULT_OSX_LATEX_PATH);
		super.performDefaults();
	}

}
