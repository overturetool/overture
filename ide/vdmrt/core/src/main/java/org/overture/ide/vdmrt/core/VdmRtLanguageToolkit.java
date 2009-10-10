package org.overture.ide.vdmrt.core;

import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;


public class VdmRtLanguageToolkit extends AbstractLanguageToolkit implements
		IDLTKLanguageToolkit
{

	private static VdmRtLanguageToolkit toolkit;
	 
	public static IDLTKLanguageToolkit getDefault() {
		if (toolkit == null) {
			toolkit = new VdmRtLanguageToolkit();
		}
		return toolkit;
	}
	
	public String getLanguageContentType()
	{
		
		return VdmRtCorePluginConstants.CONTENT_TYPE;
	}

	public String getLanguageName()
	{
		return VdmRtCorePluginConstants.LANGUAGE_NAME;
	}

	public String getNatureId()
	{
		return VdmRtProjectNature.VDM_RT_NATURE;
	}
}
