package org.overturetool.eclipse.plugins.editor.internal.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.overturetool.eclipse.plugins.editor.ui.OverturePreferenceConstants;

public class OvertureUIPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = UIPlugin.getDefault().getPreferenceStore();

		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		OverturePreferenceConstants.initializeDefaultValues(store);
	}

}
