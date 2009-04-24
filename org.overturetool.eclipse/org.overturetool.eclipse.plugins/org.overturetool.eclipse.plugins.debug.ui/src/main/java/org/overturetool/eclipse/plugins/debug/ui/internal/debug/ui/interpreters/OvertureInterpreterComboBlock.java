package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.interpreters;

import org.eclipse.dltk.debug.ui.launchConfigurations.IMainLaunchConfigurationTabListenerManager;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.eclipse.jface.preference.IPreferencePage;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;

public class OvertureInterpreterComboBlock extends
		AbstractInterpreterComboBlock {

	protected OvertureInterpreterComboBlock(
			IMainLaunchConfigurationTabListenerManager tab) {
		super(tab);
	}

	protected void showInterpreterPreferencePage() {
		IPreferencePage page = new OvertureInterpreterPreferencePage();
		//showPrefPage("org.eclipse.dltk.tcl.debug.ui.interpreters.TclInterpreterPreferencePage", page); //$NON-NLS-1$
	}

	protected String getCurrentLanguageNature() {
		return OvertureNature.NATURE_ID;
	}
}
