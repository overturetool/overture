package org.overturetool.eclipse.plugins.editor.core.internal;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.compiler.task.TaskTagUtils;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;

public class OvertureCorePreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		Preferences store = OverturePlugin.getDefault().getPluginPreferences();
		TaskTagUtils.initializeDefaultValues(store);
	}

}
