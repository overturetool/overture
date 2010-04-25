package org.overture.ide.vdmpp.debug.ui.launchconfigurations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.ui.launchconfigurations.LauncherMessages;
import org.overture.ide.debug.ui.launchconfigurations.MethodSearchEngine;
import org.overture.ide.debug.ui.launchconfigurations.VdmLaunchShortcut;
import org.overture.ide.vdmpp.debug.IVdmPpDebugConstants;
import org.overturetool.vdmj.ast.IAstNode;

public class VdmPpApplicationLaunchShortcut extends VdmLaunchShortcut
{

	/**
	 * Returns a title for a type selection dialog used to prompt the user when there is more than one type that can be
	 * launched.
	 * 
	 * @return type selection dialog title
	 */
	@Override
	protected String getTypeSelectionTitle()
	{
		return "Select VDM-PP Application";
	}

	/**
	 * Returns an error message to use when the editor does not contain a type that can be launched.
	 * 
	 * @return error message when editor cannot be launched
	 */
	@Override
	protected String getEditorEmptyMessage()
	{
		return "Editor does not contain a main type";
	}

	/**
	 * Returns an error message to use when the selection does not contain a type that can be launched.
	 * 
	 * @return error message when selection cannot be launched
	 */
	@Override
	protected String getSelectionEmptyMessage()
	{
		return "Selection does not contain a main type";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut#createConfiguration(org.eclipse.jdt
	 * .core.IType)
	 */
	protected ILaunchConfiguration createConfiguration(IAstNode type,
			String projectName)
	{
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try
		{
			
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(type.getName()));
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, projectName);
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, true);

			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT, getModuleName( type));
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OPERATION, getOperationName( type)
					+ "()");
			if (isStaticAccessRequired(type))
			{
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE, getModuleName(type));
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, getModuleName(type)
						+ "`" + getOperationName(type) + "()");
			} else
			{
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE, getModuleNameLaunch(type));
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, "new "
						+ getModuleNameLaunch(type)
						+ "."
						+ getOperationName(type) + "()");
			}
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STATIC_OPERATION, isStaticAccessRequired(type));

			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false);

			config = wc.doSave();
		} catch (CoreException exception)
		{

			MessageDialog.openError(Activator.getActiveWorkbenchShell(), LauncherMessages.VdmLaunchShortcut_3, exception.getStatus().getMessage());
		}
		return config;
	}

	@Override
	protected IAstNode[] filterTypes(Object[] elements, IRunnableContext context)
	{
		return new MethodSearchEngine().searchMainMethods(context, elements, MethodSearchEngine.EXPLICIT_FUNCTION
				| MethodSearchEngine.EXPLICIT_OPERATION
				| MethodSearchEngine.PUBLIC);

	}

	@Override
	protected ILaunchConfigurationType getConfigurationType()
	{
		return getLaunchManager().getLaunchConfigurationType(IVdmPpDebugConstants.ATTR_VDM_PROGRAM);
	}

}
