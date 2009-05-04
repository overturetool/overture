package org.overturetool.eclipse.plugins.editor.overturedebugger.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPropertyAndPreferencePage;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.overturedebugger.OvertureDebuggerPlugin;

public class OvertureDebuggerPreferencePage extends
		AbstractConfigurationBlockPropertyAndPreferencePage {

	private static String PREFERENCE_PAGE_ID = "org.overturetool.preferences.debug.engines.overturedebugger";
	private static String PROPERTY_PAGE_ID = "org.overturetool.propertyPage.debug.engines.overturedebugger";

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage
	 * #createOptionsBlock(org.eclipse.dltk.ui.util.IStatusChangeListener,
	 * org.eclipse.core.resources.IProject,
	 * org.eclipse.ui.preferences.IWorkbenchPreferenceContainer)
	 */
	protected AbstractOptionsBlock createOptionsBlock(
			IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {
		return new AbstractOptionsBlock(newStatusChangedListener, project,
				new PreferenceKey[] {}, container) {

			protected Control createOptionsBlock(Composite parent) {
				Composite composite = SWTFactory.createComposite(parent, parent
						.getFont(), 1, 1, GridData.FILL);
				SWTFactory.createLabel(composite,
						PreferenceMessages.NoSettingsAvailable, 1);
				return composite;
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
		return PREFERENCE_PAGE_ID;
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
		return PROPERTY_PAGE_ID;
	}

	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#setDescription()
	 */
	protected void setDescription() {
		setDescription(PreferenceMessages.PreferencesDescription);
	}

	/*
	 * @seeorg.eclipse.dltk.ui.preferences.
	 * AbstractConfigurationBlockPropertyAndPreferencePage#setPreferenceStore()
	 */
	protected void setPreferenceStore() {
		setPreferenceStore(OvertureDebuggerPlugin.getDefault().getPreferenceStore());
	}
}
