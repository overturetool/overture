package org.overture.ide.debug.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.IMainLaunchConfigurationTabListenerManager;
import org.eclipse.dltk.debug.ui.launchConfigurations.InterpreterTab;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmpp.debug.launching.VDMLaunchingConstants;

public class VDMPPTabGroup extends AbstractLaunchConfigurationTabGroup {

	class VDMPPInterpreterTab extends InterpreterTab
	{
		public VDMPPInterpreterTab(IMainLaunchConfigurationTabListenerManager listenerManager) {
			super(listenerManager);
		}

		@Override
		protected AbstractInterpreterComboBlock getInterpreterBlock() {
			return new AbstractInterpreterComboBlock(getMainTab()) {
				
				@Override
				protected void showInterpreterPreferencePage() {
					showPrefPage(VDMLaunchingConstants.VDMPP_DEBUG_INTERPRETER_TAB);
				}
				
				@Override
				protected String getCurrentLanguageNature() {
					return VdmPpProjectNature.VDM_PP_NATURE;
				}
			};
		}

		@Override
		protected String getNature() {
			return VdmPpProjectNature.VDM_PP_NATURE;
		}
		
	}
	
	public VDMPPTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new VDMPPMainLaunchConfigurationTab(mode),
				new VDMPPInterpreterTab(null)
				//new OvertureArgumentsTab(),
				//new OvertureInterpreterTab(),
				//new EnvironmentTab(),
				//new SourceContainerLookupTab(),
				//new CommonTab()
				//new OvertureCommonTab()
		};
		setTabs(tabs);

	}

}
