package org.overture.ide.debug.ui;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VdmVdmjLaunchShortcut extends AbstractScriptLaunchShortcut {

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(VdmPpCorePluginConstants.LAUNCH_CONFIGURATION_TYPE_VDMPP_VDMJ);
	}

	@Override
	protected String getNatureId() {
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

}
