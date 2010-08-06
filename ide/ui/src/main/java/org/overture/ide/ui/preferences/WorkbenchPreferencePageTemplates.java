package org.overture.ide.ui.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.templates.VdmTemplateManager;

public class WorkbenchPreferencePageTemplates extends TemplatePreferencePage implements IWorkbenchPreferencePage
		
{

	public WorkbenchPreferencePageTemplates()
	{
		try {
			setPreferenceStore(VdmUIPlugin.getDefault().getPreferenceStore());
			setTemplateStore(VdmTemplateManager.getInstance()
					.getTemplateStore());
			setContextTypeRegistry(VdmTemplateManager.getInstance()
					.getContextTypeRegistry());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected boolean isShowFormatterSetting() {
		return false;
	}

	public boolean performOk() {
		boolean ok = super.performOk();
		VdmUIPlugin.getDefault().savePluginPreferences();
		return ok;
	}

}
