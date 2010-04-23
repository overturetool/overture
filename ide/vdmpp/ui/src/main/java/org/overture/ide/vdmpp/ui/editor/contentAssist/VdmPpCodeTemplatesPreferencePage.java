package org.overture.ide.vdmpp.ui.editor.contentAssist;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.templates.*;

public class VdmPpCodeTemplatesPreferencePage extends TemplatePreferencePage implements IWorkbenchPreferencePage {

	public VdmPpCodeTemplatesPreferencePage() {
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
