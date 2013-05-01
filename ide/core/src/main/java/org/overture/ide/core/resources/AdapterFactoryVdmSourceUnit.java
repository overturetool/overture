package org.overture.ide.core.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.overture.ide.core.VdmCore;

public class AdapterFactoryVdmSourceUnit implements IAdapterFactory
{

	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType)
	{
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
						VdmCore.log("Failed to adapt ifile to ivdmsourceunit", e);
					}
				}
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList()
	{
		return new Class[] { IFile.class };
	}

}
