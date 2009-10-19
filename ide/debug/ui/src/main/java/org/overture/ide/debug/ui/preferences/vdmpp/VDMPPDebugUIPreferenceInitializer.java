package org.overture.ide.debug.ui.preferences.vdmpp;

import org.eclipse.dltk.debug.ui.DLTKDebugUIPluginPreferenceInitializer;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VDMPPDebugUIPreferenceInitializer extends DLTKDebugUIPluginPreferenceInitializer {

	protected String getNatureId() {
		return VdmPpProjectNature.VDM_PP_NATURE;
	}
}
