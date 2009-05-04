package org.overturetool.eclipse.plugins.editor.overturedebugger.handler;

import org.eclipse.core.runtime.Preferences;
import org.overturetool.eclipse.plugins.editor.overturedebugger.OvertureDebuggerPlugin;
import org.overturetool.eclipse.plugins.editor.overturedebugger.preferences.IOvertureDebuggerPreferenceConstants;

public class ToggleSuspendOnMethodExit extends
		AbstractTogglePreferenceKeyHandler {

	protected Preferences getPreferences() {
		return OvertureDebuggerPlugin.getDefault().getPluginPreferences();
	}

	protected String getKey() {
		return IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_METHOD_EXIT;
	}

}