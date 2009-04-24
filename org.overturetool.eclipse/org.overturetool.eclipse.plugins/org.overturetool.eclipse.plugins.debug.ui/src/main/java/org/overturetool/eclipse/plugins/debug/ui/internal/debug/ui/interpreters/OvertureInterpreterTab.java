package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.interpreters;

import org.eclipse.dltk.debug.ui.launchConfigurations.InterpreterTab;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;


public class OvertureInterpreterTab extends InterpreterTab {

	protected AbstractInterpreterComboBlock getInterpreterBlock() {
		return new OvertureInterpreterComboBlock(getMainTab());
	}

	protected String getNature() {
		return OvertureNature.NATURE_ID;
	}

}
