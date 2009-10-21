package org.overture.ide.vdmrt.debug.ui.tabs;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.IMainLaunchConfigurationTabListenerManager;
import org.eclipse.dltk.debug.ui.launchConfigurations.InterpreterTab;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.overture.ide.debug.launching.VDMLaunchingConstants;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class VdmRtTabGroup extends AbstractLaunchConfigurationTabGroup {

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
					showPrefPage(VDMLaunchingConstants.VDMRT_DEBUG_INTERPRETER_TAB);
				}
				
				@Override
				protected String getCurrentLanguageNature() {
					return VdmRtProjectNature.VDM_RT_NATURE;
				}
			};
		}

		@Override
		protected String getNature() {
			return VdmRtProjectNature.VDM_RT_NATURE;
		}
		
	}
	
	public VdmRtTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new VdmRtMainLaunchConfigurationTab(mode),
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
