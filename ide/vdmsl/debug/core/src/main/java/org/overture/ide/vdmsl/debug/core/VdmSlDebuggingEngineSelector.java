package org.overture.ide.vdmsl.debug.core;

import org.eclipse.dltk.core.DLTKIdContributionSelector;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

public class VdmSlDebuggingEngineSelector extends DLTKIdContributionSelector {

	@Override
	protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
		return delegate.getString(VdmSlDebugConstants.VDMSL_DEBUG_PLUGIN_ID, VdmSlDebugConstants.VDMSL_DEBUGGING_ENGINE_ID_KEY);
	}
}
