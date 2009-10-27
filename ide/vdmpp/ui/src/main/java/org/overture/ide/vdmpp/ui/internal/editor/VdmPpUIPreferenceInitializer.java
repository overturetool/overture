package org.overture.ide.vdmpp.ui.internal.editor;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.overture.ide.ui.VdmPreferenceConstants;
import org.overture.ide.vdmpp.ui.UIPlugin;



public class VdmPpUIPreferenceInitializer extends
		AbstractPreferenceInitializer {
 
	public void initializeDefaultPreferences() {
		IPreferenceStore store = UIPlugin.getDefault().getPreferenceStore();
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		VdmPreferenceConstants.initializeDefaultValues(store);
	}
}
