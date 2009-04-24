package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.dltk.internal.debug.ui.ScriptDebugHover;
import org.eclipse.jface.preference.IPreferenceStore;

public class OvertureDebugHover extends ScriptDebugHover{

	protected ScriptDebugModelPresentation getModelPresentation() {
		return new OvertureDebugModelPresentation();
	}

	public void setPreferenceStore(IPreferenceStore store) {
		
	}
	protected String getFieldProperty(IField field) {
		//if( field instanceof FakeField ) {
		//	return ((FakeField)field).getSnippet();
		//}
		//TODO
		return super.getFieldProperty(field);
	}
}
