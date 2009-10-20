package org.overture.ide.vdmpp.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleGlobalVariablesHandler;
import org.overture.ide.vdmpp.debug.core.VDMPPDebugConstants;


public class VdmppToggleGlobalVariableHandler extends VdmAbstractToggleGlobalVariablesHandler {

	@Override
	protected String getModelId() {
		return VDMPPDebugConstants.VDMPP_DEBUG_MODEL;
	}

}
