package org.overture.ide.ui.templates;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;


public class VdmTemplateManager {

	private static final String VDM_TEMPLATES_KEY = IVdmUiConstants.PLUGIN_ID
			+ ".vdmtemplates";
	private static VdmTemplateManager instance;
	private TemplateStore fStore = null;
	private ContributionContextTypeRegistry fRegistry;
	private TemplatePersistenceData[] templateData;

	private VdmTemplateManager() {
	}

	public static VdmTemplateManager getInstance() {
		if (instance == null) {
			instance = new VdmTemplateManager();
		}
		return instance;
	}

	public TemplateStore getTemplateStore() {

		if (fStore == null) {
			fStore = new ContributionTemplateStore(getContextTypeRegistry(),
					VdmUIPlugin.getDefault().getPreferenceStore(),
					VDM_TEMPLATES_KEY);
			try {
				fStore.load();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		return fStore;
	}

	public ContextTypeRegistry getContextTypeRegistry() {
		if (fRegistry == null) {
			fRegistry = new ContributionContextTypeRegistry();
		}
		fRegistry.addContextType(VdmUniversalTemplateContextType.CONTEXT_TYPE);
		return fRegistry;
	}

	public IPreferenceStore getPreferenceStore() {
		return VdmUIPlugin.getDefault().getPreferenceStore();
	}

	public void savePluginPreferences() {
		VdmUIPlugin.getDefault().savePluginPreferences();
	}

}
