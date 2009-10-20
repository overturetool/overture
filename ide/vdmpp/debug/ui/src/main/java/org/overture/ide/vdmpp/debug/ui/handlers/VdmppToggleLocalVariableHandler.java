package org.overture.ide.vdmpp.debug.ui.handlers;
import org.overture.ide.debug.ui.handlers.VdmAbstractToggleLocalVariablesHandler;
import org.overture.ide.vdmpp.debug.core.VDMPPDebugConstants;


public class VdmppToggleLocalVariableHandler extends VdmAbstractToggleLocalVariablesHandler {

	@Override
	protected String getModelId() {
		return VDMPPDebugConstants.VDMPP_DEBUG_MODEL;
	}

}
