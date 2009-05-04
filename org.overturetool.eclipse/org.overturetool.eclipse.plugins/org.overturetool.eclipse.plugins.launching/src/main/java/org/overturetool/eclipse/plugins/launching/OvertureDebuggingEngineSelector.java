package org.overturetool.eclipse.plugins.launching;

import org.eclipse.dltk.core.DLTKIdContributionSelector;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
//import org.overturetool.eclipse.debug.internal.debug.DebugPlugin;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;

/**
 * Overture debugging engine id based selector
 */
public class OvertureDebuggingEngineSelector extends DLTKIdContributionSelector {
	/*
	 * @see org.eclipse.dltk.core.DLTKIdContributionSelector#getSavedContributionId(org.eclipse.dltk.core.PreferencesLookupDelegate)
	 */
	protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
		return delegate.getString(OvertureDebugConstants.PLUGIN_ID, OvertureDebugConstants.DEBUGGING_ENGINE_ID_KEY);
	}
}
