package org.overturetool.eclipse.plugins.launching;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

public interface IOvertureInterpreterRunnerConfig {

	/**
	 * 
	 * @param config
	 * @param launch
	 * @param project
	 * @return full name of class that runs overture file and participates in initiating debugging session.
	 */
	public String getRunnerClassName(InterpreterConfig config, ILaunch launch,IJavaProject project);

	/**
	 * 
	 * @param config
	 * @param launch
	 * @param project
	 * @return computes class path for given arguments 
	 * @throws Exception
	 */
	public String[] computeClassPath(InterpreterConfig config, ILaunch launch,IJavaProject project) throws Exception;

	/**
	 * resolves program arguments
	 * @param config
	 * @param launch
	 * @param project
	 * @return
	 */
	public String[] getProgramArguments(InterpreterConfig config, ILaunch launch,IJavaProject project);

	
	/**
	 * any extra configuration of VMRunnerConfiguration can be done here
	 * @param vconfig
	 * @param iconfig
	 * @param launch
	 * @param project
	 */
	public void adjustRunnerConfiguration(VMRunnerConfiguration vconfig,
			InterpreterConfig iconfig, ILaunch launch,IJavaProject project);
	
	/**
	 * 
	 * @param config
	 * @param launch
	 * @param project
	 * @return full name of class that runs overture file and participates in initiating debugging session.
	 */
	public String getRunnerOperationName(InterpreterConfig config, ILaunch launch,IJavaProject project);
}
