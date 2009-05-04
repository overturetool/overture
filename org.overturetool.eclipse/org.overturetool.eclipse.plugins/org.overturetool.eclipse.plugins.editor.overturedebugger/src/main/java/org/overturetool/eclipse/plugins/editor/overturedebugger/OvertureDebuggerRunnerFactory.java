package org.overturetool.eclipse.plugins.editor.overturedebugger;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.IInterpreterRunnerFactory;

public class OvertureDebuggerRunnerFactory implements
		IInterpreterRunnerFactory {

	/*
	 * @see org.eclipse.dltk.launching.IInterpreterRunnerFactory#createRunner(org.eclipse.dltk.launching.IInterpreterInstall)
	 */
	public IInterpreterRunner createRunner(IInterpreterInstall install) {
		return new OvertureDebuggerRunner(install);
	}
}
