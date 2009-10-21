package org.overture.ide.vdmrt.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleClassVariablesHandler;
import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;

public class VdmRtToggleClassVariablesHandler extends VdmAbstractToggleClassVariablesHandler {

	@Override
	protected String getModelId() {
		return VdmRtDebugConstants.VDMRT_DEBUG_MODEL;
	}

}
