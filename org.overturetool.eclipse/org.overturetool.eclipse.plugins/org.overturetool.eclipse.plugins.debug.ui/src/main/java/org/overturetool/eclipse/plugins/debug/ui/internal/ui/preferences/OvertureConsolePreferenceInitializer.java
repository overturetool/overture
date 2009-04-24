package org.overturetool.eclipse.plugins.debug.ui.internal.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.OvertureDebugUIPlugin;
import org.overturetool.eclipse.plugins.launching.console.OvertureConsoleConstants;


public class OvertureConsolePreferenceInitializer extends AbstractPreferenceInitializer {

	public OvertureConsolePreferenceInitializer() {
	}

	public void initializeDefaultPreferences() {
		IPreferenceStore store = OvertureDebugUIPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(OvertureConsoleConstants.PREF_NEW_PROMPT,
				OvertureConsoleConstants.DEFAULT_NEW_PROMPT);
		store.setDefault(OvertureConsoleConstants.PREF_CONTINUE_PROMPT,
				OvertureConsoleConstants.DEFAULT_CONTINUE_PROMPT);
	}

}
