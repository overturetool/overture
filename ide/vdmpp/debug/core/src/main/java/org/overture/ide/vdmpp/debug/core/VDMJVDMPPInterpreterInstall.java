package org.overture.ide.vdmpp.debug;

import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VDMJVDMPPInterpreterInstall extends AbstractInterpreterInstall {

	public VDMJVDMPPInterpreterInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	public String getNatureId() {
		return VdmPpProjectNature.VDM_PP_NATURE;
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
