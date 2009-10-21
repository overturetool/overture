package org.overture.ide.vdmsl.debug.ui;

import org.eclipse.dltk.debug.ui.AbstractDebugUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.debug.ui.DebugUIConstants;
import org.overture.ide.debug.ui.DebugUIPlugin;
import org.overture.ide.vdmsl.debug.core.VdmSlDebugConstants;

/**
 * extension point: org.eclipse.dltk.debug.ui.language
 * @author kedde
 *
 */
public class VdmSlDebugUILanguageToolkit extends AbstractDebugUILanguageToolkit {

	public String getDebugModelId() {
		return VdmSlDebugConstants.VDMSL_DEBUG_MODEL;
	}

	public IPreferenceStore getPreferenceStore() {
		return DebugUIPlugin.getDefault().getPreferenceStore();
	}

	public String[] getVariablesViewPreferencePages() {
		return new String[] { DebugUIConstants.VDM_DETAIL_FORMATTER };
	}
}
