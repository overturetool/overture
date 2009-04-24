package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.interpreters;

import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;


public class OvertureInterpretersBlock extends InterpretersBlock {
	protected AddScriptInterpreterDialog createInterpreterDialog(IInterpreterInstall standin) {
		AddOvertureInterpreterDialog dialog = new AddOvertureInterpreterDialog(this, 
				getShell(), ScriptRuntime.getInterpreterInstallTypes(getCurrentNature()), 
				standin);
		return dialog;
	}

	protected String getCurrentNature() {
		return OvertureNature.NATURE_ID;
	}
}
