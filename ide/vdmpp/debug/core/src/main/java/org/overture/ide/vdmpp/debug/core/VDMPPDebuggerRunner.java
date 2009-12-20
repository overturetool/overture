package org.overture.ide.vdmpp.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;
import org.eclipse.dltk.launching.DebugSessionAcceptor;
import org.eclipse.dltk.launching.DebuggingEngineRunner;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.overture.ide.debug.interpreter.VdmjVMInterpreterRunner;
import org.overture.ide.debug.launching.ClasspathUtils;
import org.overture.ide.debug.launching.IOvertureInterpreterRunnerConfig;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;

public class VDMPPDebuggerRunner extends DebuggingEngineRunner
{

	private IOvertureInterpreterRunnerConfig runnerconfig = null;

	public VDMPPDebuggerRunner(IInterpreterInstall install) {
		super(install);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.launching.DebuggingEngineRunner#addEngineConfig(org.
	 * eclipse.dltk.launching.InterpreterConfig,
	 * org.eclipse.dltk.core.PreferencesLookupDelegate,
	 * org.eclipse.debug.core.ILaunch)
	 */
	@Override
	protected InterpreterConfig addEngineConfig(InterpreterConfig config,
			PreferencesLookupDelegate delegate, ILaunch launch)
			throws CoreException
	{
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.launching.DebuggingEngineRunner#getDebugPreferenceQualifier
	 * ()
	 */
	@Override
	protected String getDebugPreferenceQualifier()
	{
		return VDMPPDebugConstants.PLUGIN_ID;
	}

	@Override
	protected String getDebuggingEngineId()
	{
		return VDMPPDebugConstants.VDMPP_DEBUGGING_ENGINE_ID_KEY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.dltk.launching.DebuggingEngineRunner#
	 * getDebuggingEnginePreferenceQualifier()
	 */
	@Override
	protected String getDebuggingEnginePreferenceQualifier()
	{

		return "org.overture.ide.debug.launching.OvertureDebuggerRunnerFactory";
		// TODO fix this constant
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.launching.DebuggingEngineRunner#getLogFileNamePreferenceKey
	 * ()
	 */
	@Override
	protected String getLogFileNamePreferenceKey()
	{
		// 
		return null;
	}

	@Override
	public void run(InterpreterConfig config, ILaunch launch,
			IProgressMonitor monitor) throws CoreException
	{

		initializeLaunch(launch,
				config,
				createPreferencesLookupDelegate(launch));
		// IScriptProject proj =
		// AbstractScriptLaunchConfigurationDelegate.getScriptProject(launch.getLaunchConfiguration());
		final ScriptDebugTarget target = (ScriptDebugTarget) launch.getDebugTarget();
		final DebugSessionAcceptor acceptor = new DebugSessionAcceptor(target,
				monitor);

		// TODO ( VDMTools / VDMJ) ??? set config?
		setVDMJRunnerConfig();

		new VdmjVMInterpreterRunner().doRunImpl(config,
				launch,
				this.runnerconfig,
				VdmPpCorePluginConstants.CONTENT_TYPE,
				VDMPPDebugConstants.VDMPP_VDMJ_DIALECT);

		waitDebuggerConnected(launch, acceptor);

	}

	public void setVDMJRunnerConfig()
	{
		runnerconfig = new IOvertureInterpreterRunnerConfig() {

			public String getRunnerOperationName(InterpreterConfig config,
					ILaunch launch, IJavaProject project)
			{
				return null;
			}

			public String getRunnerClassName(InterpreterConfig config,
					ILaunch launch, IJavaProject project)
			{
				return "org.overturetool.vdmj.debug.DBGPReader";
			}

			public String[] getProgramArguments(InterpreterConfig config,
					ILaunch launch, IJavaProject project)
			{
				return new String[0];
			}

			public String[] computeClassPath(InterpreterConfig config,
					ILaunch launch, IJavaProject project) throws Exception
			{
				return ClasspathUtils.getClassPath(project);
			}

			public void adjustRunnerConfiguration(
					VMRunnerConfiguration vconfig, InterpreterConfig iconfig,
					ILaunch launch, IJavaProject project)
			{

			}
		};
	}
}
