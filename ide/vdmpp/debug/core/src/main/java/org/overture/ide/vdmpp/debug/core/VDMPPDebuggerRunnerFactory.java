package org.overture.ide.vdmpp.debug.core;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.IInterpreterRunnerFactory;

public class VDMPPDebuggerRunnerFactory implements IInterpreterRunnerFactory {

	public VDMPPDebuggerRunnerFactory() {
	}

	public IInterpreterRunner createRunner(IInterpreterInstall install) {
		return new VDMPPDebuggerRunner(install);
	}
}
