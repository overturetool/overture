package org.overture.ide.vdmpp.core;

import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;


public class VdmPpLanguageToolkit extends AbstractLanguageToolkit implements
		IDLTKLanguageToolkit
{

	private static VdmPpLanguageToolkit toolkit;
	 
	public static IDLTKLanguageToolkit getDefault() {
		if (toolkit == null) {
			toolkit = new VdmPpLanguageToolkit();
		}
		return toolkit;
	}
	
	public String getLanguageContentType()
	{
		
		return VdmPpCorePluginConstants.CONTENT_TYPE;
	}

	public String getLanguageName()
	{
		return VdmPpCorePluginConstants.LANGUAGE_NAME;
	}

	public String getNatureId()
	{
		return VdmPpProjectNature.VDM_PP_NATURE;
	}
}
