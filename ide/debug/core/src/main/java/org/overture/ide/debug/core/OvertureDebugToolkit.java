package org.overture.ide.debug.core;

import org.eclipse.dltk.debug.core.AbstractDLTKDebugToolkit;

public class OvertureDebugToolkit extends AbstractDLTKDebugToolkit {

	public boolean isAccessWatchpointSupported() {
		return true;
	}

}
