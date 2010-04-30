package org.overture.ide.vdmrt.debug.ui.launchconfigurations;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.operation.IRunnableContext;
import org.overture.ide.debug.ui.launchconfigurations.MethodSearchEngine;
import org.overture.ide.vdmpp.debug.ui.launchconfigurations.VdmPpApplicationLaunchShortcut;
import org.overture.ide.vdmrt.debug.IVdmRtDebugConstants;
import org.overturetool.vdmj.ast.IAstNode;

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

	@Override
	protected IAstNode[] filterTypes(Object[] elements, IRunnableContext context)
	{
		return new MethodSearchEngine().searchMainMethods(context, elements, MethodSearchEngine.WORLD_CLASS|MethodSearchEngine.RUN
				| MethodSearchEngine.EXPLICIT_OPERATION
				| MethodSearchEngine.PUBLIC);

	}

}
