package org.overture.ide.vdmsl.debug.ui.tabs;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.IMainLaunchConfigurationTabListenerManager;
import org.eclipse.dltk.debug.ui.launchConfigurations.InterpreterTab;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.overture.ide.debug.launching.VDMLaunchingConstants;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class VdmSlTabGroup extends AbstractLaunchConfigurationTabGroup {

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
					showPrefPage(VDMLaunchingConstants.VDMSL_DEBUG_INTERPRETER_TAB);
				}
				
				@Override
				protected String getCurrentLanguageNature() {
					return VdmSlProjectNature.VDM_SL_NATURE;
				}
			};
		}

		@Override
		protected String getNature() {
			return VdmSlProjectNature.VDM_SL_NATURE;
		}
		
	}
	
	public VdmSlTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new VdmSlMainLaunchConfigurationTab(mode),
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
