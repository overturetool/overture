package org.overturetool.eclipse.plugins.launching.internal.launching;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;

public class GenericOvertureInstall extends AbstractInterpreterInstall {


	public String getBuiltinModuleContent(String name) {
		return null;
	}

	public String[] getBuiltinModules() {
		return null; //$NON-NLS-1$
	}

	public long lastModified() {		
		return 0;		
	}

	public GenericOvertureInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	public IInterpreterRunner getInterpreterRunner(String mode) {
		IInterpreterRunner runner = super.getInterpreterRunner(mode);
		if (runner != null) {
			return runner;
		}

		if (mode.equals(ILaunchManager.RUN_MODE)) {
			return new OvertureInterpreterRunner(this);
		}
		/*
		 * else if (mode.equals(ILaunchManager.DEBUG_MODE)) { return new
		 * OvertureInterpreterDebugger(this); }
		 */
		return null;
	}

	public String getNatureId() {
		return OvertureNature.NATURE_ID;
	}
}
