package org.overture.ide.vdmpp.debug.ui.launching;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;

public class VdmPpMainLaunchConfigurationTab extends
		AbstractVdmMainLaunchConfigurationTab
{

	@Override
	protected String getExpression(String module, String operation,
			boolean isStatic)
	{
		if(isStatic)
		{
			return module + STATIC_CALL_SEPERATOR + operation;
		}
		
		return "new "+module+CALL_SEPERATOR + operation;
	}

	@Override
	protected boolean isSupported(IProject project) throws CoreException
	{
		return project.hasNature(IVdmPpCoreConstants.NATURE);
	}




}
