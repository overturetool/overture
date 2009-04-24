package org.overturetool.eclipse.plugins.editor.core;

import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;

public class OvertureLanguageToolkit extends AbstractLanguageToolkit {

	private static OvertureLanguageToolkit languageToolkit = new OvertureLanguageToolkit();
	
	public String getLanguageContentType() {
		return EditorCoreConstants.LANGUAGE_CONTENT_TYPE;//"org.overturetool.eclipse.plugins.editor.core.overtureContentType";
	}

	public String getLanguageName() {
		return "Overture/VDM language";
	}

	public String getNatureId() {
		return OvertureNature.NATURE_ID;
	}
	
	@Override
	public String getPreferenceQualifier() {
		return OverturePlugin.PLUGIN_ID;
	}
	
	public static IDLTKLanguageToolkit getDefault(){
		return languageToolkit;
	}

}
