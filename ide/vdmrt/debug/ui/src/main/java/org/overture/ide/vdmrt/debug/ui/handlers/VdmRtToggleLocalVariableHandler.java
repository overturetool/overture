package org.overture.ide.vdmrt.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleLocalVariablesHandler;
import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;


public class VdmRtToggleLocalVariableHandler extends VdmAbstractToggleLocalVariablesHandler {

	@Override
	protected String getModelId() {
		return VdmRtDebugConstants.VDMRT_DEBUG_MODEL;
	}

}
