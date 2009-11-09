package org.overture.ide.vdmsl.debug.ui.preferences;

import org.eclipse.dltk.debug.ui.DLTKDebugUIPluginPreferenceInitializer;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class VdmSlDebugUIPreferenceInitializer extends DLTKDebugUIPluginPreferenceInitializer {

	@Override
	protected String getNatureId() {
		return VdmSlProjectNature.VDM_SL_NATURE;
	}
}
