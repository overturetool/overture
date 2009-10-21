package org.overture.ide.vdmsl.debug.ui.shortcuts;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overture.ide.vdmsl.debug.core.VdmSlDebugConstants;

public class VdmSlVdmjLaunchShortcut extends AbstractScriptLaunchShortcut {

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(VdmSlDebugConstants.LAUNCH_CONFIGURATION_TYPE_VDMSL_VDMJ);
	}

	@Override
	protected String getNatureId() {
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

}
