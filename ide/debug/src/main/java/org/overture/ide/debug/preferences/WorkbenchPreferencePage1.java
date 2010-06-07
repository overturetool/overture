package org.overture.ide.debug.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.overture.ide.debug.core.IDebugPreferenceConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;


public class WorkbenchPreferencePage1 extends
		org.eclipse.jface.preference.FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	@Override
	protected void createFieldEditors()
	{
		IntegerFieldEditor portFiled = new IntegerFieldEditor(IDebugPreferenceConstants.PREF_DBGP_PORT, "Debug port", getFieldEditorParent());
		portFiled.setValidRange(-1, Integer.MAX_VALUE);
		addField(portFiled);
		
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return VdmDebugPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(IDebugPreferenceConstants.PREF_DBGP_PORT, IDebugPreferenceConstants.DBGP_DEFAULT_PORT);
		super.performDefaults();
	}

	public void init(IWorkbench workbench)
	{

	}

}
