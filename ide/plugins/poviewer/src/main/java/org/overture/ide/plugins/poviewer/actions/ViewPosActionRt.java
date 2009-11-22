package org.overture.ide.plugins.poviewer.actions;

import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class ViewPosActionRt extends ViewPosActionPp {

	@Override
	protected String getNature() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}
	
	
}
