package org.overture.ide.debug.ui.preferences;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.overture.ide.ui.VdmUIPlugin;

public class OvertureInterpreterPreferencePage extends AbstractConfigurationBlockPreferencePage {

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new OvertureInterpretersBlock(overlayPreferenceStore);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage#getHelpId()
	 */
	@Override
	protected String getHelpId() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage#setDescription()
	 */
	@Override
	protected void setDescription() {
		setDescription("Interpreter Settings");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage#setPreferenceStore()
	 */
	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(VdmUIPlugin.getDefault().getPreferenceStore());		
	}

}
