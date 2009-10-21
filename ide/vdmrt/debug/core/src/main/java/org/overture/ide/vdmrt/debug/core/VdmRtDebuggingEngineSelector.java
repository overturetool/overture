package org.overture.ide.vdmrt.debug.core;

import org.eclipse.dltk.core.DLTKIdContributionSelector;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

public class VdmRtDebuggingEngineSelector extends DLTKIdContributionSelector {

	@Override
	protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
		return delegate.getString(VdmRtDebugConstants.VDMRT_DEBUG_PLUGIN_ID, VdmRtDebugConstants.vdmrt_DEBUGGING_ENGINE_ID_KEY);
	}
}
