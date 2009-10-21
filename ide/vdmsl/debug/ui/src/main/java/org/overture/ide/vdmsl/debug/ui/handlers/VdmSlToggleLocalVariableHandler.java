package org.overture.ide.vdmsl.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleLocalVariablesHandler;
import org.overture.ide.vdmsl.debug.core.VdmSlDebugConstants;


public class VdmSlToggleLocalVariableHandler extends VdmAbstractToggleLocalVariablesHandler {

	@Override
	protected String getModelId() {
		return VdmSlDebugConstants.VDMSL_DEBUG_MODEL;
	}

}
