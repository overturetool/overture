package org.overture.ide.vdmrt.debug.core;

import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class VdmjVdmRtInterpreterInstall extends AbstractInterpreterInstall {

	public VdmjVdmRtInterpreterInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	public String getNatureId() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}
	
	//TODO test if the method getInterpreterRunner is needed
//	@Override
//	public IInterpreterRunner getInterpreterRunner(String mode) {
//		IInterpreterRunner runner = super.getInterpreterRunner(mode);
//		if (runner != null) {
//			return (IConfigurableRunner)runner;
//			//return new vdmrtInterpreterRunner(this);
//		}
//
//		if (mode.equals(ILaunchManager.RUN_MODE)) {
//			return new vdmrtInterpreterRunner(this);
//		}
//		/*
//		 * else if (mode.equals(ILaunchManager.DEBUG_MODE)) { return new
//		 * OvertureInterpreterDebugger(this); }
//		 */
//		return null;
//	}
}
