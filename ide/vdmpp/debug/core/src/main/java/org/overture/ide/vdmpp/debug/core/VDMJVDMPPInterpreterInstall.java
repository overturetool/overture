package org.overture.ide.vdmpp.debug.core;

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

}
