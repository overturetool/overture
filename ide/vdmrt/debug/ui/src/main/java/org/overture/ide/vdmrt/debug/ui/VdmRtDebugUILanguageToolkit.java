package org.overture.ide.vdmrt.debug.ui;

import org.eclipse.dltk.debug.ui.AbstractDebugUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.debug.ui.DebugUIConstants;
import org.overture.ide.debug.ui.DebugUIPlugin;
import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;

/**
 * extension point: org.eclipse.dltk.debug.ui.language
 * @author kedde
 *
 */
public class VdmRtDebugUILanguageToolkit extends AbstractDebugUILanguageToolkit {

	public String getDebugModelId() {
		return VdmRtDebugConstants.VDMRT_DEBUG_MODEL;
	}

	public IPreferenceStore getPreferenceStore() {
		return DebugUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String[] getVariablesViewPreferencePages() {
		return new String[] { DebugUIConstants.VDM_DETAIL_FORMATTER };
	}
}
