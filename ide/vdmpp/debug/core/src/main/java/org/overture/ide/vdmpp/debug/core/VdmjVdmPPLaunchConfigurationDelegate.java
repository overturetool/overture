package org.overture.ide.vdmpp.debug.core;

import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;

public class VdmjVdmPPLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {

	@Override
	public String getLanguageId() {
		return VdmPpCorePluginConstants.LANGUAGE_NAME;
	}
	
	
}
