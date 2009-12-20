package org.overture.ide.vdmpp.debug.ui.tabs;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.IMainLaunchConfigurationTabListenerManager;
import org.eclipse.dltk.debug.ui.launchConfigurations.InterpreterTab;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.overture.ide.debug.launching.VDMLaunchingConstants;
import org.overture.ide.debug.ui.tabs.VdmEnvironmentTab;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VdmppTabGroup extends AbstractLaunchConfigurationTabGroup {

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
	
	public VdmppTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		
		VdmppMainLaunchConfigurationTab launchTab= new VdmppMainLaunchConfigurationTab(mode);
		
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				launchTab,
				new VdmEnvironmentTab(launchTab),
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
