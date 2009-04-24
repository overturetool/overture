package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.debug.ui.preferences.AbstractDebuggingEngineOptionsBlock;
import org.eclipse.dltk.ui.PreferencesAdapter;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPropertyAndPreferencePage;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.overturetool.eclipse.debug.internal.debug.DebugPlugin;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;
import org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.OvertureDebugUIConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;


public class OvertureDebuggingEnginePreferencePage extends
		AbstractConfigurationBlockPropertyAndPreferencePage {

	private static PreferenceKey DEBUGGING_ENGINE = new PreferenceKey(
			OvertureDebugConstants.PLUGIN_ID,
			OvertureDebugConstants.DEBUGGING_ENGINE_ID_KEY);

	protected AbstractOptionsBlock createOptionsBlock(
			IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {
		return new AbstractDebuggingEngineOptionsBlock(
				newStatusChangedListener, project, getKeys(), container) {

			protected String getNatureId() {
				return OvertureNature.NATURE_ID;
			}

			protected PreferenceKey getSavedContributionKey() {
				return DEBUGGING_ENGINE;
			}
		};
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#getHelpId()
	 */
	protected String getHelpId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @seeorg.eclipse.dltk.internal.ui.preferences.PropertyAndPreferencePage#
	 * getPreferencePageId()
	 */
	protected String getPreferencePageId() {
		return OvertureDebugUIConstants.PREFERENCE_ENGINE_PAGE_ID;
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#getProjectHelpId()
	 */
	protected String getProjectHelpId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @seeorg.eclipse.dltk.internal.ui.preferences.PropertyAndPreferencePage#
	 * getPropertyPageId()
	 */
	protected String getPropertyPageId() {
		return OvertureDebugUIConstants.PROPERTY_ENGINES_PAGE_ID;
	}

	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#setDescription()
	 */
	protected void setDescription() {
		setDescription(OvertureDebugPreferenceMessages.OvertureDebugEnginePreferencePage_description);
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#setPreferenceStore()
	 */
	protected void setPreferenceStore() {
		setPreferenceStore(new PreferencesAdapter(DebugPlugin
				.getDefault().getPluginPreferences()));
	}

	private PreferenceKey[] getKeys() {
		return new PreferenceKey[] { DEBUGGING_ENGINE };
	}
}
