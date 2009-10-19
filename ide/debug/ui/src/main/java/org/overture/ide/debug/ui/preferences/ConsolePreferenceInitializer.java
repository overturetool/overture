package org.overture.ide.debug.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.debug.ui.DebugUIConstants;
import org.overture.ide.debug.ui.DebugUIPlugin;


/***
 * extension point: org.eclipse.core.runtime.preferences
 * @author kedde
 *
 */
public class ConsolePreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore();
		store.setDefault(DebugUIConstants.PREF_NEW_PROMPT, DebugUIConstants.DEFAULT_NEW_PROMPT);
		store.setDefault(DebugUIConstants.PREF_CONTINUE_PROMPT, DebugUIConstants.DEFAULT_CONTINUE_PROMPT);
	}

}
