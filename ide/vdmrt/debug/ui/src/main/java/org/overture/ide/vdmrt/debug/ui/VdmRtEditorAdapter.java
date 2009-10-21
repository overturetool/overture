package org.overture.ide.vdmrt.debug.ui;

import org.overture.ide.debug.ui.VdmEditorDebugAdapterFactory;
import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;

public class VdmRtEditorAdapter extends VdmEditorDebugAdapterFactory {

	@Override
	protected String getDebugModelId() {
		return VdmRtDebugConstants.VDMRT_DEBUG_MODEL;
	}

}
