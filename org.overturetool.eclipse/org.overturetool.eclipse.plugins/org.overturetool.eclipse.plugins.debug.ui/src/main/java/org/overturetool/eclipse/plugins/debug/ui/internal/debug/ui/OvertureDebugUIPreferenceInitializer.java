package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui;

import org.eclipse.dltk.debug.ui.DLTKDebugUIPluginPreferenceInitializer;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;


public class OvertureDebugUIPreferenceInitializer extends
		DLTKDebugUIPluginPreferenceInitializer {

	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}

}
