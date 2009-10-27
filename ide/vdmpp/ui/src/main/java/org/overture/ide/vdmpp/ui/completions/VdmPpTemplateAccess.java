package org.overture.ide.vdmpp.ui.completions;

import org.overture.ide.ui.completion.templates.VdmTemplateAccess;
import org.overture.ide.vdmpp.ui.VdmPpUiPluginConstants;

public class VdmPpTemplateAccess extends VdmTemplateAccess {

	protected static VdmPpTemplateAccess instance = null;	
	
	@Override
	protected String getContextTypeId() {
		return VdmPpUiPluginConstants.CONTEXT_TYPE_ID;
	}

	@Override
	protected String getCustomTemplatesKey() {
		return VdmPpUiPluginConstants.CONTEXT_TYPE_ID + ".TEMPLATE";
	}
	
	public static VdmPpTemplateAccess getInstance()
	{
		if (instance == null)
		{
			instance = new VdmPpTemplateAccess();
		}
		return instance;
	}

}
