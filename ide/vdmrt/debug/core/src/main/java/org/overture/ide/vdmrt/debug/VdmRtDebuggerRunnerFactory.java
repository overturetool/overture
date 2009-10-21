package org.overture.ide.vdmrt.debug;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.IInterpreterRunnerFactory;

public class VdmRtDebuggerRunnerFactory implements IInterpreterRunnerFactory {

	public VdmRtDebuggerRunnerFactory() {
	}

	public IInterpreterRunner createRunner(IInterpreterInstall install) {
		return new VdmRtDebuggerRunner(install);
	}
}
