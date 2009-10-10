package org.overture.ide.vdmrt.ui.internal.editor;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.overture.ide.ui.VdmPreferenceConstants;
import org.overture.ide.vdmrt.ui.UIPlugin;



public class VdmRtUIPreferenceInitializer extends
		AbstractPreferenceInitializer {
 
	public void initializeDefaultPreferences() {
		IPreferenceStore store = UIPlugin.getDefault()
				.getPreferenceStore();
 
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
 
		VdmPreferenceConstants.initializeDefaultValues(store);
	}
}


