package org.overturetool.eclipse.plugins.editor.overturedebugger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.DebugSessionAcceptor;
import org.eclipse.dltk.launching.DebuggingEngineRunner;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;
import org.overturetool.eclipse.plugins.launching.IConfigurableRunner;
import org.overturetool.eclipse.plugins.launching.IOvertureInterpreterRunnerConfig;
import org.overturetool.eclipse.plugins.launching.internal.launching.OvertureInterpreterRunner;

public class OvertureDebuggerRunner extends DebuggingEngineRunner implements IConfigurableRunner {

	public static String ENGINE_ID = "org.overturetool.overturedebugger";

	private IOvertureInterpreterRunnerConfig runnerconfig = OvertureInterpreterRunner.VDMJ_CONFIG;
	private IOvertureInterpreterRunnerConfig vdmToolsConfig = OvertureInterpreterRunner.VDMTOOLS_CONFIG;

	public OvertureDebuggerRunner(IInterpreterInstall install) {
		super(install);
	}

	public String getDebugModelId() {
		return OvertureDebugConstants.DEBUG_MODEL_ID;
	}

	public void run(InterpreterConfig config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		initializeLaunch(launch, config,createPreferencesLookupDelegate(launch));
		IScriptProject proj = AbstractScriptLaunchConfigurationDelegate.getScriptProject(launch.getLaunchConfiguration());
		final ScriptDebugTarget target = (ScriptDebugTarget) launch.getDebugTarget();
		final DebugSessionAcceptor acceptor = new DebugSessionAcceptor(target, monitor);
		try {
			IInterpreterInstall interpreterInstall = ScriptRuntime.getInterpreterInstall(proj);
			if (!interpreterInstall.getInterpreterInstallType().getId().equals("org.overturetool.eclipse.plugins.launching.internal.launching.VDMJInstallType")) {
				OvertureInterpreterRunner.doRunImpl(config, launch, this.vdmToolsConfig);
			}
			else {
				OvertureInterpreterRunner.doRunImpl(config, launch, this.runnerconfig);
			}
			waitDebuggerConnected(launch, acceptor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
//		OvertureInterpreterRunner.doRunImpl(config, launch, this.runnerconfig);
	}

	public void setRunnerConfig(IOvertureInterpreterRunnerConfig config) {
		this.runnerconfig = config;
	}

	protected String getDebuggingEngineId() {
		return ENGINE_ID;
	}

	/**
	 * @deprecated Use {@link #addEngineConfig(InterpreterConfig,PreferencesLookupDelegate,ILaunch)} instead
	 */
//	protected InterpreterConfig addEngineConfig(InterpreterConfig config,
//			PreferencesLookupDelegate delegate) throws CoreException {
//				return addEngineConfig(config, delegate, null);
//			}

	protected InterpreterConfig addEngineConfig(InterpreterConfig config,
			PreferencesLookupDelegate delegate, ILaunch launch) throws CoreException {
		return config;
	}

	protected String getDebugPreferenceQualifier() {
		return OvertureDebugConstants.PLUGIN_ID;
	}

	protected String getDebuggingEnginePreferenceQualifier() {
		return OvertureDebuggerPlugin.PLUGIN_ID;
	}

	protected String getLoggingEnabledPreferenceKey() {
		// not yet supported...
		return null;
	}

	protected String getLogFileNamePreferenceKey() {
		// not yet supported...
		return null;
	}

	protected String getLogFilePathPreferenceKey() {
		// not yet supported...
		return null;
	}
}
