package org.overture.ide.debug.core;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;


@SuppressWarnings("deprecation")
public class OvertureDebugPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		Preferences store = DebugCorePlugin.getDefault().getPluginPreferences();

		if (!store.getDefaultString(DebugCoreConstants.VDMPP_DEBUGGING_ENGINE_ID_KEY).equals(""))
		{
			store.setDefault(DebugCoreConstants.VDMPP_DEBUGGING_ENGINE_ID_KEY, "org.overturetool.overturedebugger");
		}
		// org.overturetool.overturedebugger

		store.setDefault(DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE, false);
		store.setDefault(DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING,	false);

		store.setDefault(DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_GLOBAL, true);
		store.setDefault(DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_CLASS, true);
		store.setDefault(DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_LOCAL, true);
	}
}
