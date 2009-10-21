package org.overture.ide.vdmsl.debug.core;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.IInterpreterRunnerFactory;

public class VdmSlDebuggerRunnerFactory implements IInterpreterRunnerFactory {

	public VdmSlDebuggerRunnerFactory() {
	}

	public IInterpreterRunner createRunner(IInterpreterInstall install) {
		return new VdmSlDebuggerRunner(install);
	}
}
