package org.overturetool.eclipse.plugins.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;

public class OvertureLaunchConfigurationDelegate extends
		AbstractScriptLaunchConfigurationDelegate {

	protected void runRunner(ILaunchConfiguration configuration,
			IInterpreterRunner runner, InterpreterConfig config,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (runner instanceof IConfigurableRunner){
			IOvertureInterpreterRunnerConfig runnerConfig = getConfig();
			if (runnerConfig!=null){
				IConfigurableRunner rc=(IConfigurableRunner) runner;
				rc.setRunnerConfig(runnerConfig);
			}
		}
		runner.run(config, launch, monitor);
	}
	
	public IOvertureInterpreterRunnerConfig getConfig(){
		return null;
	}

	

	public String getLanguageId() {
		return OvertureNature.NATURE_ID;
	}
}
