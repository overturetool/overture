package org.overture.ide.vdmpp.debug.ui;

import org.overture.ide.debug.ui.VdmEditorDebugAdapterFactory;
import org.overture.ide.vdmpp.debug.core.VDMPPDebugConstants;

public class VdmppEditorAdapter extends VdmEditorDebugAdapterFactory {

	@Override
	protected String getDebugModelId() {
		return VDMPPDebugConstants.VDMPP_DEBUG_MODEL;
	}

}
