package org.overture.ide.vdmpp.debug.launching;

import org.eclipse.dltk.core.DLTKIdContributionSelector;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

public class VdmppDebuggingEngineSelector extends DLTKIdContributionSelector {

	public VdmppDebuggingEngineSelector() {
	}

	@Override
	protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
		return delegate.getString(VDMLaunchingConstants.VDMPP_DEBUG_PLUGIN_ID, VDMLaunchingConstants.VDMPP_DEBUGGING_ENGINE_ID_KEY);
	}
	

}
