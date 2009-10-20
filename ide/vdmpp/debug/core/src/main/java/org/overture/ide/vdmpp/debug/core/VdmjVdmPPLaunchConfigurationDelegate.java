package org.overture.ide.vdmpp.debug.core;

import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;

public class VdmjVdmPPLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {

	@Override
	public String getLanguageId() {
		return VdmPpCorePluginConstants.LANGUAGE_NAME;
	}

//	@Override
//	protected void runRunner(ILaunchConfiguration configuration,
//			IInterpreterRunner runner, InterpreterConfig config,
//			ILaunch launch, IProgressMonitor monitor) throws CoreException {
//		if (runner instanceof IConfigurableRunner){
//			IOvertureInterpreterRunnerConfig runnerConfig = getConfig();
//			if (runnerConfig!=null){
//				IConfigurableRunner rc=(IConfigurableRunner) runner;
//				rc.setRunnerConfig(runnerConfig);
//			}
//		}
//		runner.run(config, launch, monitor);
//	}

//	private IOvertureInterpreterRunnerConfig getConfig() {
//		return null;
//	}	
}
