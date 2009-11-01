package org.overture.ide.vdmrt.ui.completions;

import org.overture.ide.ui.completion.templates.VdmTemplateAccess;
import org.overture.ide.vdmrt.ui.VdmRtUiPluginConstants;

public class VdmRtTemplateAccess extends VdmTemplateAccess {

	protected static VdmRtTemplateAccess instance = null;	
	
	@Override
	protected String getContextTypeId() {
		return VdmRtUiPluginConstants.CONTEXT_TYPE_ID;
	}

	@Override
	protected String getCustomTemplatesKey() {
		return VdmRtUiPluginConstants.CONTEXT_TYPE_ID + ".TEMPLATE";
	}
	
	public static VdmRtTemplateAccess getInstance()
	{
		if (instance == null)
		{
			instance = new VdmRtTemplateAccess();
		}
		return instance;
	}

}
