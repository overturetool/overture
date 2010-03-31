package org.overture.ide.vdmsl.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class VdmSlProjectNature implements IProjectNature
{
	public static final String VDM_SL_NATURE = IVdmSlCoreConstants.PLUGIN_ID
	+ ".nature";
	public void configure() throws CoreException
	{
		// TODO Auto-generated method stub

	}

	public void deconfigure() throws CoreException
	{
		// TODO Auto-generated method stub

	}

	public IProject getProject()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setProject(IProject project)
	{
		// TODO Auto-generated method stub

	}

}
