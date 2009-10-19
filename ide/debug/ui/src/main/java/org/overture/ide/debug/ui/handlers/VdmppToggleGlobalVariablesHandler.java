package org.overture.ide.debug.ui.handlers;

import org.eclipse.dltk.debug.ui.handlers.AbstractToggleGlobalVariableHandler;
import org.eclipse.dltk.ui.PreferencesAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.debug.core.DebugCorePlugin;
import org.overture.ide.vdmpp.debug.VDMPPDebugConstants;

public class VdmppToggleGlobalVariablesHandler extends
		AbstractToggleGlobalVariableHandler {

	@Override
	protected String getModelId() {
		return VDMPPDebugConstants.VDMPP_DEBUG_MODEL;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return new PreferencesAdapter(DebugCorePlugin.getDefault().getPluginPreferences());
	}

}
