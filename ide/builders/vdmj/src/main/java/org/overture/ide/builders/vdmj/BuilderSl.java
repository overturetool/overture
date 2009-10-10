package org.overture.ide.builders.vdmj;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.modules.ModuleList;



public class BuilderSl extends VdmjBuilder
{
	@Override
	public IStatus buileModelElements(IProject project, List modelElements)
	{
		ModuleList modules = (ModuleList) modelElements;
		IEclipseVdmj eclipseType = new EclipseVdmjSl(modules );
		return buileModelElements(project, eclipseType);
	}



	@Override
	public String getNatureId()
	{
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

}
