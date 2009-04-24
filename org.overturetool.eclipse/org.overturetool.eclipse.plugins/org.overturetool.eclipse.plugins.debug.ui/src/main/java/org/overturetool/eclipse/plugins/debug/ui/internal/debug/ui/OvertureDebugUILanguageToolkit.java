package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui;

import org.eclipse.dltk.debug.ui.AbstractDebugUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;


public class OvertureDebugUILanguageToolkit extends
		AbstractDebugUILanguageToolkit {

	/*
	 * @see
	 * org.eclipse.dltk.debug.ui.IDLTKDebugUILanguageToolkit#getDebugModelId()
	 */
	public String getDebugModelId() {
		return OvertureDebugConstants.DEBUG_MODEL_ID;
	}

	/*
	 * @see
	 * org.eclipse.dltk.debug.ui.IDLTKDebugUILanguageToolkit#getPreferenceStore
	 * ()
	 */
	public IPreferenceStore getPreferenceStore() {
		return OvertureDebugUIPlugin.getDefault().getPreferenceStore();
	}

	public String[] getVariablesViewPreferencePages() {
		return new String[] { "org.overturetool.preferences.debug.detailFormatters" };
	}

}
