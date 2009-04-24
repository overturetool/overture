/**
 * 
 */
package org.overturetool.eclipse.plugins.editor.ui.actions;

import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.overturetool.eclipse.plugins.editor.internal.ui.OvertureUILanguageToolkit;

/**
 * @author jcompagner
 * 
 */
public class OpenMethodAction extends
		org.eclipse.dltk.ui.actions.OpenMethodAction {

	/**
	 * @see org.eclipse.dltk.ui.actions.OpenMethodAction#getUILanguageToolkit()
	 */
	protected IDLTKUILanguageToolkit getUILanguageToolkit() {
		return new OvertureUILanguageToolkit();
	}

}
