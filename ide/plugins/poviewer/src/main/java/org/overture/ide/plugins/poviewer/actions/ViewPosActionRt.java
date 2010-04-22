package org.overture.ide.plugins.poviewer.actions;

import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;

public class ViewPosActionRt extends ViewPosActionPp {

	@Override
	protected String getNature() {
		return IVdmRtCoreConstants.NATURE;
	}
	
	
}
