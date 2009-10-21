package org.overture.ide.vdmrt.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleGlobalVariablesHandler;
import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;


public class VdmRtToggleGlobalVariableHandler extends VdmAbstractToggleGlobalVariablesHandler {

	@Override
	protected String getModelId() {
		return VdmRtDebugConstants.VDMRT_DEBUG_MODEL;
	}

}
