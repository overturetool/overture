package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.interpreters;

import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;

public class OvertureInterpreterPreferencePage extends ScriptInterpreterPreferencePage {

	//public static final String PAGE_ID = "org.eclipse.dltk.debug.ui.TCLInterpreters";

	public InterpretersBlock createInterpretersBlock() {
		return new OvertureInterpretersBlock();
	}
}
