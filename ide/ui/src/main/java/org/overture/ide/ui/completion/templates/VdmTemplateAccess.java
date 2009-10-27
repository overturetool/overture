package org.overture.ide.ui.completion.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.ui.VdmUIPlugin;

public abstract class VdmTemplateAccess extends ScriptTemplateAccess {

	@Override
	abstract protected String getContextTypeId();

	@Override
	protected abstract String getCustomTemplatesKey();

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return VdmUIPlugin.getDefault().getPreferenceStore();
	}

}
