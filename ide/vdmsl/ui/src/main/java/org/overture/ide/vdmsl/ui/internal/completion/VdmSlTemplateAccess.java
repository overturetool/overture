package org.overture.ide.vdmsl.ui.internal.completion;

import org.overture.ide.ui.completion.templates.VdmTemplateAccess;
import org.overture.ide.vdmsl.ui.VdmSlUiPluginConstants;

public class VdmSlTemplateAccess extends VdmTemplateAccess {

	protected static VdmSlTemplateAccess instance = null;	
	
	@Override
	protected String getContextTypeId() {
		return VdmSlUiPluginConstants.CONTEXT_TYPE_ID;
	}

	@Override
	protected String getCustomTemplatesKey() {
		return VdmSlUiPluginConstants.CONTEXT_TYPE_ID + ".TEMPLATE";
	}
	
	public static VdmSlTemplateAccess getInstance()
	{
		if (instance == null)
		{
			instance = new VdmSlTemplateAccess();
		}
		return instance;
	}

}
