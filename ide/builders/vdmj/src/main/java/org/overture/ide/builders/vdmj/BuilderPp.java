package org.overture.ide.builders.vdmj;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.definitions.ClassList;



public class BuilderPp extends VdmjBuilder
{
	@Override
	public IStatus buileModelElements(IProject project, List modelElements)
	{
		ClassList modules = (ClassList) modelElements;
		IEclipseVdmj eclipseType = new EclipseVdmjPp(modules );
		
		return buileModelElements(project, eclipseType);
		
	}



	@Override
	public String getNatureId()
	{
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

}
