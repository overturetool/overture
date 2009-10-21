package org.overture.ide.vdmrt.debug.core;

import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;

public class VdmjVdmRtLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {

	@Override
	public String getLanguageId() {
		return VdmRtCorePluginConstants.LANGUAGE_NAME;
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
