package org.overturetool.eclipse.plugins.editor.ui;

import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;

public interface EditorCoreUIConstants {
public static String EDITOR_ID ="org.overturetool.eclipse.plugins.editor.internal.ui.editor.OvertureEditor";
public static String INTERPRETER_CONTAINER_ID ="org.overturetool.eclipse.plugins.editor.launching.INTERPRETER_CONTAINER";
public static String INTERPRETER_PREFERENCE_PAGE ="org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.interpreters.OvertureInterpreterPreferencePage";
public static String SCRIPT_COLOR_PROVIDER = "org.overturetool.eclipse.plugins.editor.ui.scriptcolor.provider";

public static String DEBUG_PREFERENCE_PAGE ="org.overturetool.eclipse.plugins.editor.preferences.debug";

public static final String[] EDITOR_PREFERENCE_PAGES_IDS = {
	"org.overturetool.eclipse.plugins.editor.ui.EditorPreferences", 
	"org.overturetool.eclipse.plugins.editor.ui.editor.SyntaxColoring", 
	"org.overturetool.eclipse.plugins.editor.ui.editor.SmartTyping", 
	"org.overturetool.eclipse.plugins.editor.ui.editor.OvertureFolding", 
	"overtureTemplatePreferencePage" 
};

public static final String PLUGIN_ID = "org.overturetool.eclipse.plugins.editor.ui";

public static final String ACTION_MESSAGES_BUNDLE_NAME= "org.overturetool.eclipse.plugins.editor.internal.ui.editor.ActionMessages";//$NON-NLS-1$

public static String CALL_HIERARCHY_ID="org.eclipse.dltk.callhierarchy.view";

public static String OVERTURE_EDITOR_SCOPE="org.overturetool.eclipse.plugins.editor.ui.overtureEditorScope";

public static final String FORMATTER_LINE_SPLIT = UIPlugin.PLUGIN_ID + ".formatter.lineSplit"; //$NON-NLS-1$

public final static String FORMATTER_COMMENT_LINE_LENGTH = UIPlugin.PLUGIN_ID + ".formatter.comment.line_length"; //$NON-NLS-1$

public final static String FORMATTER_COMMENT_CLEAR_BLANK_LINES = UIPlugin.PLUGIN_ID +  ".formatter.comment.clear_blank_lines"; //$NON-NLS-1$

public static final String OVERTURE_PREFERENCE_MESSAGES_BUNDLE_NAME = "org.overturetool.eclipse.plugins.editor.internal.ui.preferences.OverturePreferenceMessages";//$NON-NLS-1$

public static String TODO_PREFERENCE_PAGE_ID ="org.overturetool.eclipse.plugins.editor.preferences.todo";

public static final String CUSTOM_TEMPLATES_KEY = "org.overturetool.eclipse.plugins.editor.Templates";

public static final String CONTEXT_TYPE_ID = "overtureUniversalTemplateContextType";
																  
public static final String OVERTURE_WIZARD_MESSAGES_BUNDLE_NAME= "org.overturetool.eclipse.plugins.editor.internal.ui.wizards.OvertureWizardMessages";//$NON-NLS-1$

public static String NAVIGATOR ="org.eclipse.dltk.ui.ScriptExplorer";

public static String OVERTURE_PROJECT_WIZARD ="org.overturetool.eclipse.plugins.editor.internal.ui.wizards.OvertureProjectWizard";
public static String OVERTURE_FILE_CREATION_WIZARD ="org.overturetool.eclipse.plugins.editor.internal.ui.wizards.OvertureFileCreationWizard";
public static String OVERTURE_NEW_FOLDER_WIZARD ="org.eclipse.ui.wizards.new.folder";
public static String OVERTURE_NEW_FILE_WIZARD ="org.eclipse.ui.wizards.new.file";
public static String OVERTURE_UNTITLED_TEXT_FILE_WIZARD ="org.eclipse.ui.editors.wizards.UntitledTextFileWizard";




}
