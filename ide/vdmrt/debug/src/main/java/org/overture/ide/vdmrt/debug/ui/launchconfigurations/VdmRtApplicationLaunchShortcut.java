package org.overture.ide.vdmrt.debug.ui.launchconfigurations;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.overture.ide.vdmpp.debug.ui.launchconfigurations.VdmPpApplicationLaunchShortcut;
import org.overture.ide.vdmrt.debug.IVdmRtDebugConstants;

public class VdmRtApplicationLaunchShortcut extends VdmPpApplicationLaunchShortcut
{

	
	/**
	 * Returns a title for a type selection dialog used to prompt the user when there is more than one type
	 * that can be launched.
	 * 
	 * @return type selection dialog title
	 */
	@Override
	protected String getTypeSelectionTitle()
	{
		return "Select VDM-RT Application";
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut#getConfigurationType()
	 */
	@Override
	protected ILaunchConfigurationType getConfigurationType()
	{
		return getLaunchManager().getLaunchConfigurationType(IVdmRtDebugConstants.ATTR_VDM_PROGRAM);
	}

	

}
