package org.overture.ide.vdmsl.debug.ui;

import org.overture.ide.debug.ui.VdmEditorDebugAdapterFactory;
import org.overture.ide.vdmsl.debug.core.VdmSlDebugConstants;

public class VdmSlEditorAdapter extends VdmEditorDebugAdapterFactory {

	@Override
	protected String getDebugModelId() {
		return VdmSlDebugConstants.VDMSL_DEBUG_MODEL;
	}

}
