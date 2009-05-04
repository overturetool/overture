package org.overturetool.eclipse.plugins.editor.internal.ui;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureLanguageToolkit;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.SimpleOvertureSourceViewerConfiguration;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;


public class OvertureUILanguageToolkit implements IDLTKUILanguageToolkit {
	private static ScriptElementLabels sInstance = new ScriptElementLabels() {};

	public ScriptElementLabels getScriptElementLabels() {
		return sInstance;
	}

	public IPreferenceStore getPreferenceStore() {
		return UIPlugin.getDefault().getPreferenceStore();
	}

	public IDLTKLanguageToolkit getCoreToolkit() {
		return OvertureLanguageToolkit.getDefault();
	}

	public IDialogSettings getDialogSettings() {
		return UIPlugin.getDefault().getDialogSettings();
	}
	public String getEditorId(Object inputElement) {
		return EditorCoreUIConstants.EDITOR_ID;//"org.overturetool.ui.editor.OvertureEditor";
	}
	public String getPartitioningId() {		
		return OvertureConstants.OVERTURE_PARTITIONING;
	}

	public String getInterpreterContainerId() {
		return EditorCoreUIConstants.INTERPRETER_CONTAINER_ID;//"org.overturetool.eclipse.plugins.launching.INTERPRETER_CONTAINER";
	}

	public ScriptUILabelProvider createScriptUILabelProvider() {
		return null;
	}

	public boolean getProvideMembers(ISourceModule element) {
		return true;
	}
	
	public ScriptTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
	
	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleOvertureSourceViewerConfiguration(
					getTextTools().getColorManager(),
					getPreferenceStore(),
					null,
					getPartitioningId(),
					false);
	}

	public String getInterpreterPreferencePage() {
		return EditorCoreUIConstants.INTERPRETER_PREFERENCE_PAGE;//"org.overturetool.debug.ui.OvertureInterpreters";
	}

	public String getDebugPreferencePage() {
		return EditorCoreUIConstants.DEBUG_PREFERENCE_PAGE;//"org.overturetool.preferences.debug";
	}

	private static final String[] EDITOR_PREFERENCE_PAGES_IDS =EditorCoreUIConstants.EDITOR_PREFERENCE_PAGES_IDS; /*{
		"org.overturetool.ui.EditorPreferences", 
		"org.overturetool.ui.editor.SyntaxColoring", 
		"org.overturetool.ui.editor.SmartTyping", 
		"org.overturetool.ui.editor.OvertureFolding", 
		"overtureTemplatePreferencePage" 
	};*/
	
	public String[] getEditorPreferencePages() {
		return EDITOR_PREFERENCE_PAGES_IDS;
	}

	public IPreferenceStore getCombinedPreferenceStore() {
		// TODO Auto-generated method stub
		return null;
	}
}
