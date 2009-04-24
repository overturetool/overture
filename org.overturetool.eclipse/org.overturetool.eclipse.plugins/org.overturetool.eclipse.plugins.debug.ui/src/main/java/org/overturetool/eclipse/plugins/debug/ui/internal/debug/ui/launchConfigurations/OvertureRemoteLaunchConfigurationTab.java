package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.launchConfigurations;

import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
import org.eclipse.dltk.debug.ui.launchConfigurations.RemoteLaunchConfigurationTab;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;


public class OvertureRemoteLaunchConfigurationTab extends RemoteLaunchConfigurationTab {

	public OvertureRemoteLaunchConfigurationTab(String mode) {
		super(mode);
	}

	@Override
	protected boolean breakOnFirstLinePrefEnabled(
			PreferencesLookupDelegate delegate) {
		return delegate.getBoolean(OvertureDebugConstants.PLUGIN_ID,
				DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE);
	}

	@Override
	protected boolean dbpgLoggingPrefEnabled(PreferencesLookupDelegate delegate) {
		return delegate.getBoolean(OvertureDebugConstants.PLUGIN_ID, DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING);
		
	}

	@Override
	protected String getNatureID() {
		return OvertureNature.NATURE_ID;
	}
}
