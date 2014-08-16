/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.templates;

import java.io.IOException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
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
//	private TemplatePersistenceData[] templateData;

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
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(IVdmUiConstants.TEMPLATE_EXTENTION_POINT_ID);
		
		for (IConfigurationElement iConfigurationElement : extensions) {
			String id = iConfigurationElement.getAttribute(IVdmUiConstants.TEMPLATE_EXTENTION_POINT_ID_ATTRIBUTE);
			fRegistry.addContextType(id);
		}
		
		//fRegistry.addContextType(VdmUniversalTemplateContextType.CONTEXT_TYPE);
		return fRegistry;
	}

	public IPreferenceStore getPreferenceStore() {
		return VdmUIPlugin.getDefault().getPreferenceStore();
	}

	public void savePluginPreferences() {
		VdmUIPlugin.getDefault().savePluginPreferences();
	}

}
