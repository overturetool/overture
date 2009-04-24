package org.overturetool.eclipse.plugins.editor.internal.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Preferences;

import org.eclipse.dltk.ui.PreferencesAdapter;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPropertyAndPreferencePage;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.AbstractTodoTaskOptionsBlock;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;

public class OvertureTodoTaskPreferencePage extends
		AbstractConfigurationBlockPropertyAndPreferencePage {

	static final PreferenceKey CASE_SENSITIVE = AbstractTodoTaskOptionsBlock.createCaseSensitiveKey(UIPlugin.PLUGIN_ID);

	static final PreferenceKey ENABLED = AbstractTodoTaskOptionsBlock.createEnabledKey(UIPlugin.PLUGIN_ID);

	static final PreferenceKey TAGS = AbstractTodoTaskOptionsBlock.createTagKey(UIPlugin.PLUGIN_ID);

	protected String getHelpId() {
		return null;
	}

	protected void setDescription() {
		setDescription(OverturePreferenceMessages.TodoTaskDescription);
	}

	protected Preferences getPluginPreferences() {
		return UIPlugin.getDefault().getPluginPreferences();
	}

	protected AbstractOptionsBlock createOptionsBlock(
			IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {
		return new AbstractTodoTaskOptionsBlock(newStatusChangedListener,
				project, getPreferenceKeys(), container) {
			protected PreferenceKey getTags() {
				return TAGS;
			}

			protected PreferenceKey getEnabledKey() {
				return ENABLED;
			}

			protected PreferenceKey getCaseSensitiveKey() {
				return CASE_SENSITIVE;
			}
		};
	}

	protected String getProjectHelpId() {
		return null;
	}

	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}

	protected void setPreferenceStore() {
		setPreferenceStore(new PreferencesAdapter(UIPlugin.getDefault()
				.getPluginPreferences()));
	}

	protected String getPreferencePageId() {
		return EditorCoreUIConstants.TODO_PREFERENCE_PAGE_ID;//"org.overturetool.preferences.todo";
	}

	protected String getPropertyPageId() {
		return EditorCoreUIConstants.TODO_PREFERENCE_PAGE_ID;//"org.overturetool.propertyPage.todo";
	}

	protected PreferenceKey[] getPreferenceKeys() {
		return new PreferenceKey[] { TAGS, ENABLED, CASE_SENSITIVE };
	}
}