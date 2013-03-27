package org.overture.ide.plugins.uml2.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.overture.ide.plugins.uml2.Activator;
import org.overture.ide.plugins.uml2.IUml2Constants;

public class WorkbenchPreferencePageMain extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{

	@Override
	protected void createFieldEditors()
	{
		addField(new BooleanFieldEditor(IUml2Constants.PREFER_ASSOCIATIONS_PREFERENCE, "Prefer associations during translation", getFieldEditorParent()));
		addField(new BooleanFieldEditor(IUml2Constants.DISABLE_NESTED_ARTIFACTS_PREFERENCE, "Disable nested artifacts in Deployment Diagrams", getFieldEditorParent()));
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(IUml2Constants.PREFER_ASSOCIATIONS_PREFERENCE, true);
		store.setDefault(IUml2Constants.DISABLE_NESTED_ARTIFACTS_PREFERENCE, true);
		super.performDefaults();
	}

	public void init(IWorkbench workbench)
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(IUml2Constants.PREFER_ASSOCIATIONS_PREFERENCE, true);
		store.setDefault(IUml2Constants.DISABLE_NESTED_ARTIFACTS_PREFERENCE, true);
	}

}
