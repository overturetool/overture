package org.overture.ide.vdmsl.debug.ui.preferences;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;
import org.overture.ide.debug.ui.preferences.AbstractInterpreterBlock;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class VdmSlInterpreterPreferencePage extends ScriptInterpreterPreferencePage {

	public VdmSlInterpreterPreferencePage() {
		
	}
	
	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new AbstractInterpreterBlock(){
			@Override
			protected String getCurrentNature() {
				return VdmSlProjectNature.VDM_SL_NATURE;
			}
		};
	}
}
