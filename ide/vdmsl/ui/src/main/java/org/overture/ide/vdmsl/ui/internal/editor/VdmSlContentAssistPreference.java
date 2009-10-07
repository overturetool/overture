package org.overture.ide.vdmsl.ui.internal.editor;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.overture.ide.vdmsl.ui.UIPlugin;

public class VdmSlContentAssistPreference extends
		ContentAssistPreference {
 
	private static VdmSlContentAssistPreference instance;
 
	public static ContentAssistPreference getDefault() {
		if (instance == null) {
			instance = new VdmSlContentAssistPreference();
		}
		return instance;
	}
 
	@Override
	protected ScriptTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
	
}
