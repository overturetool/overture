package org.overture.ide.vdmrt.ui.internal.editor;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.overture.ide.vdmrt.ui.UIPlugin;


public class VdmRtContentAssistPreference extends
		ContentAssistPreference {
 
	private static VdmRtContentAssistPreference instance;
 
	public static ContentAssistPreference getDefault() {
		if (instance == null) {
			instance = new VdmRtContentAssistPreference();
		}
		return instance;
	}
 
	@Override
	protected ScriptTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
	
}
