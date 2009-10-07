package org.overture.ide.vdmpp.ui.internal.editor;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.overture.ide.vdmpp.ui.UIPlugin;


public class VdmPpContentAssistPreference extends
		ContentAssistPreference {
 
	private static VdmPpContentAssistPreference instance;
 
	public static ContentAssistPreference getDefault() {
		if (instance == null) {
			instance = new VdmPpContentAssistPreference();
		}
		return instance;
	}
 
	@Override
	protected ScriptTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
	
}
