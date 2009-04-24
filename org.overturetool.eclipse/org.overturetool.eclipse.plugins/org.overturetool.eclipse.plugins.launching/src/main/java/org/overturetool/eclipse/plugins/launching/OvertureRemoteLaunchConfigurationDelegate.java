package org.overturetool.eclipse.plugins.launching;

import org.eclipse.dltk.launching.AbstractRemoteLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.RemoteDebuggingEngineRunner;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.launching.internal.launching.OvertureRemoteDebuggerRunnner;

public class OvertureRemoteLaunchConfigurationDelegate extends AbstractRemoteLaunchConfigurationDelegate {

	protected RemoteDebuggingEngineRunner getDebuggingRunner(
			IInterpreterInstall install) {
		// TODO Auto-generated method stub
		return new OvertureRemoteDebuggerRunnner(install);
	}

	public String getLanguageId() {
		return OvertureNature.NATURE_ID;
	}

}
