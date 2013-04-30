package org.overture.ide.core.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.overture.ide.internal.core.resources.VdmProject;

public class AdapterFactory1 implements IAdapterFactory
{

	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType)
	{
		if (adapterType == IVdmProject.class)
		{
			if (adaptableObject instanceof IProject)
			{
				IProject project = (IProject) adaptableObject;
				if (VdmProject.isVdmProject(project))
				{
					return VdmProject.createProject(project);
				}
			}
		}

		if (adapterType == IProject.class)
		{
			if (adaptableObject instanceof VdmProject)
			{
				VdmProject project = (VdmProject) adaptableObject;
				return project.project;
			}
		}

		if (adapterType == IVdmSourceUnit.class)
		{
			if (adaptableObject instanceof IFile)
			{
				IFile f = (IFile) adaptableObject;
				IProject project = f.getProject();
				IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);
				if (p != null)
				{
					try
					{
						for (IVdmSourceUnit source : p.getSpecFiles())
						{
							if (source.getFile().getProjectRelativePath().equals(f.getProjectRelativePath()))
							{
								return source;
							}
						}
					} catch (CoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList()
	{
		return new Class[] { IVdmProject.class, IProject.class,
				IFile.class };
	}

}
