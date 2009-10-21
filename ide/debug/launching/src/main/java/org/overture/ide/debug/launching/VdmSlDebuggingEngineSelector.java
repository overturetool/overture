package org.overture.ide.debug.launching;

import org.eclipse.dltk.core.DLTKIdContributionSelector;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

public class VdmSlDebuggingEngineSelector extends DLTKIdContributionSelector {

	public VdmSlDebuggingEngineSelector() {
	}

	@Override
	protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
		return delegate.getString(VDMLaunchingConstants.VDMSL_DEBUG_PLUGIN_ID, VDMLaunchingConstants.VDMSL_DEBUGGING_ENGINE_ID_KEY);
	}
	

}
