package org.overture.ide.vdmsl.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleClassVariablesHandler;
import org.overture.ide.vdmsl.debug.core.VdmSlDebugConstants;

public class VdmSlToggleClassVariablesHandler extends VdmAbstractToggleClassVariablesHandler {

	@Override
	protected String getModelId() {
		return VdmSlDebugConstants.VDMSL_DEBUG_MODEL;
	}

}
