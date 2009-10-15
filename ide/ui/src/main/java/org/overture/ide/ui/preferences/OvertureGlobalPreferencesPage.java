package org.overture.ide.ui.preferences;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.overture.ide.ui.VdmUIPlugin;

public class OvertureGlobalPreferencesPage extends AbstractConfigurationBlockPreferencePage {

	public OvertureGlobalPreferencesPage() {}

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new OvertureGlobalConfigurationBlock(overlayPreferenceStore, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage#getHelpId()
	 */
	@Override
	protected String getHelpId() {
		return null;
	}

	@Override
	protected void setDescription() {
		setDescription("Global Overture page");
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(VdmUIPlugin.getDefault().getPreferenceStore());
	}

}
