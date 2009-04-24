package org.overturetool.eclipse.plugins.launching.internal.launching;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.RemoteDebuggingEngineRunner;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;

public class OvertureRemoteDebuggerRunnner extends RemoteDebuggingEngineRunner {

	public OvertureRemoteDebuggerRunnner(IInterpreterInstall install) {
		super(install);
	}

	@Override
	protected String getDebugPreferenceQualifier() {
		return OvertureDebugConstants.PLUGIN_ID;
	}

}
