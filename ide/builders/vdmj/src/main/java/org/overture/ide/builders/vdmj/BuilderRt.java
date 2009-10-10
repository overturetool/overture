package org.overture.ide.builders.vdmj;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overturetool.vdmj.definitions.ClassList;
import org.overture.ide.vdmrt.core.*;


public class BuilderRt extends VdmjBuilder
{
	@Override
	public IStatus buileModelElements(IProject project, List modelElements)
	{
		ClassList modules = (ClassList) modelElements;
		IEclipseVdmj eclipseType = new EclipseVdmjRt(modules );
		
		return buileModelElements(project, eclipseType);
		
	}



	@Override
	public String getNatureId()
	{
		return VdmRtProjectNature.VDM_RT_NATURE;
	}

}
