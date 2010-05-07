package org.overture.ide.plugins.csk.internal;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.overture.ide.plugins.csk.Activator;
import org.overture.ide.plugins.csk.ICskConstants;

public class WorkbenchPreferencePage1 extends
		org.eclipse.jface.preference.FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	@Override
	protected void createFieldEditors()
	{
		addField(new FileFieldEditor(ICskConstants.VPPGDE_PATH, "VDM Tools vppgde path", getFieldEditorParent()));
		addField(new FileFieldEditor(ICskConstants.VRTGDE_PATH, "VDM Tools vrtgde path", getFieldEditorParent()));
		addField(new FileFieldEditor(ICskConstants.VSLGDE_PATH, "VDM Tools vslgde path", getFieldEditorParent()));

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
		store.setDefault(ICskConstants.VPPGDE_PATH, ICskConstants.DEFAULT_VPPGDE_PATH);
		super.performDefaults();
	}

	public void init(IWorkbench workbench)
	{
		// TODO Auto-generated method stub

	}

}
