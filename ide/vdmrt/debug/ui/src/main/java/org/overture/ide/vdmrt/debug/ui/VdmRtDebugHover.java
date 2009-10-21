package org.overture.ide.vdmrt.debug.ui;

import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.dltk.internal.debug.ui.ScriptDebugHover;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * extension point: org.eclipse.dltk.ui.editorTextHovers
 * @author kedde
 *
 */
public class VdmRtDebugHover extends ScriptDebugHover {

	@Override
	protected ScriptDebugModelPresentation getModelPresentation() {
		return new VdmRtDebugModelPresentation();
	}

	public void setPreferenceStore(IPreferenceStore store) {
		// TODO Auto-generated method stub
		System.out.println("// TODO Auto-generated method stub");
	}

	//protected String getFieldProperty(IField field) {
	// TODO
	//}
}
