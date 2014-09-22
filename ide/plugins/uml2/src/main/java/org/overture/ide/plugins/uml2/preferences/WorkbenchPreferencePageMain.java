/*
 * #%~
 * UML2 Translator
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
