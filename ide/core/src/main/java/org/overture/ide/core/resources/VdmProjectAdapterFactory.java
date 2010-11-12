package org.overture.ide.core.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.overture.ide.internal.core.resources.VdmProject;

public class VdmProjectAdapterFactory implements IAdapterFactory
{

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if(adapterType == IVdmProject.class)
		{
			if(adaptableObject instanceof IProject){
				IProject project = (IProject) adaptableObject;
				if(VdmProject.isVdmProject(project))
				{
					return VdmProject.createProject(project);
				}
			}
		}
		
		if(adapterType == IProject.class)
		{
			if(adaptableObject instanceof VdmProject){
				VdmProject project = (VdmProject) adaptableObject;
				return project.project;
			}
		}
		
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList()
	{		
		return new Class[]{IVdmProject.class, IProject.class};
	}

}
