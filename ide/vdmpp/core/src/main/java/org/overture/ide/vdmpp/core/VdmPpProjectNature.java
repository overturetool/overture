package org.overture.ide.vdmpp.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;


public class VdmPpProjectNature implements IProjectNature
{
	public static final String VDM_PP_NATURE = VdmPpCorePluginConstants.PLUGIN_ID
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
