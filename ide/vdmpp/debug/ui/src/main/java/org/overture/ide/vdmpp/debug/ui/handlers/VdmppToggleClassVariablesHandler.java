package org.overture.ide.vdmpp.debug.ui.handlers;

import org.overture.ide.debug.ui.handlers.VdmAbstractToggleClassVariablesHandler;
import org.overture.ide.vdmpp.debug.core.VDMPPDebugConstants;


public class VdmppToggleClassVariablesHandler extends
		VdmAbstractToggleClassVariablesHandler {

	@Override
	protected String getModelId() {
		return VDMPPDebugConstants.VDMPP_DEBUG_MODEL;
	}

}
