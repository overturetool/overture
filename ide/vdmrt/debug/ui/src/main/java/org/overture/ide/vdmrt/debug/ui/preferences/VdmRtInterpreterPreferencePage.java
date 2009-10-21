package org.overture.ide.vdmrt.debug.ui.preferences;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;
import org.overture.ide.debug.ui.preferences.AbstractInterpreterBlock;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class VdmRtInterpreterPreferencePage extends ScriptInterpreterPreferencePage {

	public VdmRtInterpreterPreferencePage() {
		
	}
	
	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new AbstractInterpreterBlock(){
			@Override
			protected String getCurrentNature() {
				return VdmRtProjectNature.VDM_RT_NATURE;
			}
		};
	}
}
