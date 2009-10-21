package org.overture.ide.vdmrt.debug.ui.preferences;

import org.eclipse.dltk.debug.ui.DLTKDebugUIPluginPreferenceInitializer;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class VdmRtDebugUIPreferenceInitializer extends DLTKDebugUIPluginPreferenceInitializer {

	protected String getNatureId() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}
}
