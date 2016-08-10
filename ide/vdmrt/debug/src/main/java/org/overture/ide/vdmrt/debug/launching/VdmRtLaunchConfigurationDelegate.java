/*
 * #%~
 * org.overture.ide.vdmrt.debug
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
package org.overture.ide.vdmrt.debug.launching;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.core.launching.VdmLaunchConfigurationDelegate;
import org.overture.ide.vdmrt.debug.IVdmRtDebugConstants;

public class VdmRtLaunchConfigurationDelegate extends
		VdmLaunchConfigurationDelegate
{
	@Override
	protected Collection<? extends String> getExtendedCommands(
			IVdmProject project, ILaunchConfiguration configuration)
			throws CoreException
	{
		List<String> arguments = new ArrayList<String>();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		File logDir = new File(getOutputFolder(project, configuration), "logs");
		String dateString = dateFormat.format(date);
		
		if (configuration.getAttribute(IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING, IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING_DEFAULT))
		{
			// log
			logDir.mkdirs();
			String logFilename = dateString + ".rt";
//			System.out.println(logFilename);
			File f = new File(logDir, logFilename);
			if (!f.exists())
			{
				f.getParentFile().mkdirs();
				try
				{
					f.createNewFile();
				} catch (IOException e)
				{

					e.printStackTrace();
				}
			}

			arguments.add("-log");
			arguments.add(logDir.toURI().toASCIIString() + logFilename);
		}
		
		if (configuration.getAttribute(IVdmRtDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_REALTIME_TIME_INV_CHECKS, false))
		{
			logDir.mkdirs();
			String logFilename = dateString + ".rt.rtbin.vtc";
			File f = new File(logDir, logFilename);
			if (!f.exists())
			{
				f.getParentFile().mkdirs();
				try
				{
					f.createNewFile();
				} catch (IOException e)
				{

					e.printStackTrace();
				}
			}

			arguments.add("-timeinv");
			arguments.add(logDir.toURI().toASCIIString() + logFilename);
		}
		return arguments;
	}
}
