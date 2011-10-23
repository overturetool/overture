/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
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
