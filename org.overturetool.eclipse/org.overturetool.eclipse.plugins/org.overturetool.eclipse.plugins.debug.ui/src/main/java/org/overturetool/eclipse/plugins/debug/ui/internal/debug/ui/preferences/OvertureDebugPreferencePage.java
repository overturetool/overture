package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
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


public class OvertureDebugPreferencePage extends
			AbstractConfigurationBlockPropertyAndPreferencePage  {

	private static PreferenceKey BREAK_ON_FIRST_LINE = new PreferenceKey(
			OvertureDebugConstants.PLUGIN_ID,
			DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE);

	private static PreferenceKey ENABLE_DBGP_LOGGING = new PreferenceKey(
			OvertureDebugConstants.PLUGIN_ID,
			DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING);
	
	private static PreferenceKey DEBUG_FROM_CONSOLE = new PreferenceKey(
			OvertureDebugConstants.PLUGIN_ID,
			OvertureDebugConstants.PREF_DEBUG_FROM_CONSOLE);
	
		
	protected AbstractOptionsBlock createOptionsBlock(
			IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {
		
		return new OvertureAbstractDebuggingOptionsBlock(newStatusChangedListener,
				project, getKeys(), container) {

			protected PreferenceKey getBreakOnFirstLineKey() {
				return BREAK_ON_FIRST_LINE;
			}

			protected PreferenceKey getDbgpLoggingEnabledKey() {
				return ENABLE_DBGP_LOGGING;
			}
			
			protected PreferenceKey getDebugFromConsoleKey() {
				return DEBUG_FROM_CONSOLE;
			}
		};
	}	
	
			
	
	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#getHelpId()
	 */
	protected String getHelpId() {
		return null;
	}

	/*
	 * @seeorg.eclipse.dltk.internal.ui.preferences.PropertyAndPreferencePage#
	 * getPreferencePageId()
	 */
	protected String getPreferencePageId() {
		return OvertureDebugUIConstants.PREFERENCE_PAGE_ID;
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
		return OvertureDebugUIConstants.PROPERTY_PAGE_ID;
	}

	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#setDescription()
	 */
	protected void setDescription() {
		setDescription(OvertureDebugPreferenceMessages.OvertureDebugPreferencePage_description);
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#setPreferenceStore()
	 */
	protected void setPreferenceStore() {
		setPreferenceStore(new PreferencesAdapter(DebugPlugin.getDefault().getPluginPreferences()));
	}

	private PreferenceKey[] getKeys() {
		return new PreferenceKey[] { BREAK_ON_FIRST_LINE, ENABLE_DBGP_LOGGING, DEBUG_FROM_CONSOLE };
	}
	
}
