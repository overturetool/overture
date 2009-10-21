package org.overture.ide.vdmsl.debug.core;

import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.overture.ide.vdmsl.core.VdmSlCorePluginConstants;

public class VdmjVdmSlLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {
	@Override
	public String getLanguageId() {
		return VdmSlCorePluginConstants.LANGUAGE_NAME;
	}
}
