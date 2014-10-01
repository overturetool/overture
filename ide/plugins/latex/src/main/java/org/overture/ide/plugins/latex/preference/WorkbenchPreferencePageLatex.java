/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex.preference;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.overture.ide.plugins.latex.ILatexConstants;
import org.overture.ide.plugins.latex.LatexPlugin;

public class WorkbenchPreferencePageLatex extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{
	class MacFieldEditor extends FileFieldEditor
	{
		public MacFieldEditor(String osxLatexPathPreference, String string,
				Composite fieldEditorParent)
		{
			super(osxLatexPathPreference, string, fieldEditorParent);
		}

		@Override
		protected boolean checkState()
		{
			if(Platform.getOS().equalsIgnoreCase(Platform.OS_MACOSX))
				return super.checkState();
			
			return true;
		}
	}
	
	@Override
	protected void createFieldEditors()
	{
		addField(new MacFieldEditor(ILatexConstants.OSX_LATEX_PATH_PREFERENCE, "MacOS Latex Path", getFieldEditorParent()));
		addField(new ComboFieldEditor(ILatexConstants.PDF_BUILDER, "PDF Builder", new String[][] {
				new String[] { "PdfLaTex", ILatexConstants.DEFAULT_PDF_BUILDER },
				new String[] { "XeTex", "xetex" } }, getFieldEditorParent()));
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return LatexPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(ILatexConstants.OSX_LATEX_PATH_PREFERENCE, ILatexConstants.DEFAULT_OSX_LATEX_PATH);
		store.setDefault(ILatexConstants.PDF_BUILDER, ILatexConstants.DEFAULT_PDF_BUILDER);
		
		super.performDefaults();
	}

	public void init(IWorkbench workbench)
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(ILatexConstants.OSX_LATEX_PATH_PREFERENCE, ILatexConstants.DEFAULT_OSX_LATEX_PATH);
		store.setDefault(ILatexConstants.PDF_BUILDER, ILatexConstants.DEFAULT_PDF_BUILDER);
	}
}
