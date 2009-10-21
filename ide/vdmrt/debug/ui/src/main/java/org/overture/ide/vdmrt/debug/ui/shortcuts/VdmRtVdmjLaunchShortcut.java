package org.overture.ide.vdmrt.debug.ui.shortcuts;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;

public class VdmRtVdmjLaunchShortcut extends AbstractScriptLaunchShortcut {

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(VdmRtDebugConstants.LAUNCH_CONFIGURATION_TYPE_VDMSL_VDMJ);
	}

	@Override
	protected String getNatureId() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}

}
