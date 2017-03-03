/*
 * #%~
 * Combinatorial Testing
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.combinatorialtesting.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ast.lex.Dialect;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.ui.launchconfigurations.LauncherMessages;
import org.overture.ide.plugins.combinatorialtesting.ITracesConstants;
import org.overture.ide.plugins.combinatorialtesting.OvertureTracesPlugin;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader.TraceInfo;
import org.overture.ide.vdmpp.debug.IVdmPpDebugConstants;
import org.overture.ide.vdmrt.debug.IVdmRtDebugConstants;
import org.overture.ide.vdmsl.debug.IVdmSlDebugConstants;

public class TraceDebugLauncher
{

	public void Launch(IVdmProject project, TraceInfo traceInfo,
			Integer traceNumber)
	{
		ILaunchConfiguration config = createConfiguration(project, traceInfo, traceNumber);
		if (config != null)
		{
			DebugUITools.launch(config, "debug");
		}

	}

	protected ILaunchConfiguration createConfiguration(IVdmProject project,
			TraceInfo traceInfo, Integer traceNumber)
	{
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		String expression = traceInfo.getTraceName() + " " + traceNumber + " {"
				+ traceInfo.getSubset() + ","
				+ traceInfo.getTraceReductionType() + "," + traceInfo.getSeed()
				+ "}";
		
		IPreferenceStore preferences = OvertureTracesPlugin.getDefault().getPreferenceStore();
		
		try
		{

			ILaunchConfigurationType configType = getConfigurationType(project.getDialect());
			wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(project.getName()
					+ " CT"));
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, project.getName());
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE, true);

			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT, traceInfo.getClassName());
			// wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OPERATION, "NOT Specified");

			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MODULE, traceInfo.getClassName());
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, expression);
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_STATIC_OPERATION, true);

			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_IS_TRACE, true);

			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false);

			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, preferences.getBoolean(ITracesConstants.REMOTE_DEBUG_PREFERENCE));
			
			wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, preferences.getString(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION));
			
			return wc;
			// config = wc.doSave();
		} catch (CoreException exception)
		{

			MessageDialog.openError(VdmDebugPlugin.getActiveWorkbenchShell(), LauncherMessages.VdmLaunchShortcut_3, exception.getStatus().getMessage());
		}
		return config;
	}

	private ILaunchConfigurationType getConfigurationType(Dialect dialect)
	{

		switch (dialect)
		{
			case VDM_PP:
				return getLaunchManager().getLaunchConfigurationType(IVdmPpDebugConstants.ATTR_VDM_PROGRAM);
			case VDM_RT:
				return getLaunchManager().getLaunchConfigurationType(IVdmRtDebugConstants.ATTR_VDM_PROGRAM);

			case VDM_SL:
				return getLaunchManager().getLaunchConfigurationType(IVdmSlDebugConstants.ATTR_VDM_PROGRAM);

			default:
				break;
		}

		return null;

	}

	protected ILaunchManager getLaunchManager()
	{
		return DebugPlugin.getDefault().getLaunchManager();
	}
}
