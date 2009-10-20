package org.overture.ide.vdmpp.debug.core;

import org.eclipse.dltk.core.DLTKIdContributionSelector;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

public class VDMPPDebuggingEngineSelector extends DLTKIdContributionSelector {

	@Override
	protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
		return delegate.getString(VDMPPDebugConstants.VDMPP_DEBUG_PLUGIN_ID, VDMPPDebugConstants.VDMPP_DEBUGGING_ENGINE_ID_KEY);
	}
}
