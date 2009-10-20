package org.overture.ide.vdmpp.debug.ui.preferences;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;
import org.overture.ide.debug.ui.preferences.AbstractInterpreterBlock;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VDMPPInterpreterPreferencePage extends ScriptInterpreterPreferencePage {

	public VDMPPInterpreterPreferencePage() {
		
	}
	
	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new AbstractInterpreterBlock(){
			@Override
			protected String getCurrentNature() {
				return VdmPpProjectNature.VDM_PP_NATURE;
			}
		};
	}
}
