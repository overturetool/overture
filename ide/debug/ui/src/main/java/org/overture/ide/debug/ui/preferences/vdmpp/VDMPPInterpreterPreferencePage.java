package org.overture.ide.debug.ui.preferences.vdmpp;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;

public class VDMPPInterpreterPreferencePage extends ScriptInterpreterPreferencePage {

	public VDMPPInterpreterPreferencePage() {
	}

	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new VDMPPInterpreterBlock();
	}
}
