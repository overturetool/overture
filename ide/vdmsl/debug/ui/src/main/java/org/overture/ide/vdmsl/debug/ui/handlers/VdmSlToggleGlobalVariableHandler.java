package org.overture.ide.vdmsl.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleGlobalVariablesHandler;
import org.overture.ide.vdmsl.debug.core.VdmSlDebugConstants;


public class VdmSlToggleGlobalVariableHandler extends VdmAbstractToggleGlobalVariablesHandler {

	@Override
	protected String getModelId() {
		return VdmSlDebugConstants.VDMSL_DEBUG_MODEL;
	}

}
