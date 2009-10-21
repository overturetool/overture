package org.overture.ide.vdmsl.debug.core;

import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class VdmjVdmSlInterpreterInstall extends AbstractInterpreterInstall {

	public VdmjVdmSlInterpreterInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	public String getNatureId() {
		return VdmSlProjectNature.VDM_SL_NATURE;
	}
	
	//TODO test if the method getInterpreterRunner is needed
//	@Override
//	public IInterpreterRunner getInterpreterRunner(String mode) {
//		IInterpreterRunner runner = super.getInterpreterRunner(mode);
//		if (runner != null) {
//			return (IConfigurableRunner)runner;
//			//return new VDMPPInterpreterRunner(this);
//		}
//
//		if (mode.equals(ILaunchManager.RUN_MODE)) {
//			return new VDMPPInterpreterRunner(this);
//		}
//		/*
//		 * else if (mode.equals(ILaunchManager.DEBUG_MODE)) { return new
//		 * OvertureInterpreterDebugger(this); }
//		 */
//		return null;
//	}
}
