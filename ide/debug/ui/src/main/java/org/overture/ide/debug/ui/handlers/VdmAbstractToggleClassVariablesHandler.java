package org.overture.ide.debug.ui.handlers;

import org.eclipse.dltk.debug.ui.handlers.AbstractToggleClassVariableHandler;
import org.eclipse.dltk.ui.PreferencesAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.debug.core.DebugCorePlugin;

public abstract class VdmAbstractToggleClassVariablesHandler extends AbstractToggleClassVariableHandler {

	@Override
	abstract protected String getModelId();

	@SuppressWarnings("deprecation")
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return new PreferencesAdapter(DebugCorePlugin.getDefault().getPluginPreferences());
	}

}
