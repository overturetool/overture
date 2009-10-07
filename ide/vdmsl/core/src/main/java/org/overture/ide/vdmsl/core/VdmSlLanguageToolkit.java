package org.overture.ide.vdmsl.core;

import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;

public class VdmSlLanguageToolkit extends AbstractLanguageToolkit implements
		IDLTKLanguageToolkit
{

	private static VdmSlLanguageToolkit toolkit;
	 
	public static IDLTKLanguageToolkit getDefault() {
		if (toolkit == null) {
			toolkit = new VdmSlLanguageToolkit();
		}
		return toolkit;
	}
	
	public String getLanguageContentType()
	{
		
		return VdmSlCorePluginConstants.CONTENT_TYPE;
	}

	public String getLanguageName()
	{
		return VdmSlCorePluginConstants.LANGUAGE_NAME;
	}

	public String getNatureId()
	{
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

}
