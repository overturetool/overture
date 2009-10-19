package org.overture.ide.debug.ui.vdmpp;

import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.dltk.internal.debug.ui.ScriptDebugHover;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * extension point: org.eclipse.dltk.ui.editorTextHovers
 * @author kedde
 *
 */
public class VDMPPDebugHover extends ScriptDebugHover {

	@Override
	protected ScriptDebugModelPresentation getModelPresentation() {
		return new VDMPPDebugModelPresentation();
	}

	public void setPreferenceStore(IPreferenceStore store) {
		// TODO Auto-generated method stub
		System.out.println("// TODO Auto-generated method stub");
	}

	//protected String getFieldProperty(IField field) {
		//if( field instanceof FakeField ) {
		//	return ((FakeField)field).getSnippet();
		//}
		//TODO use to get field?? 
	//	return super.getFieldProperty(field);
	//}
}
