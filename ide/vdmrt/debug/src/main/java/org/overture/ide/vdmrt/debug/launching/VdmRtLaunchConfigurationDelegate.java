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

public class VdmRtLaunchConfigurationDelegate extends
		VdmLaunchConfigurationDelegate
{
	@Override
	protected Collection<? extends String> getExtendedCommands(
			IVdmProject project, ILaunchConfiguration configuration) throws CoreException
	{
		// log
		List<String> arguments = new ArrayList<String>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		File logDir = new File(new File(getOutputFolder(project,configuration), "logs"), configuration.getName());
		logDir.mkdirs();
		String logFilename = dateFormat.format(date) + ".logrt";
		System.out.println(logFilename);
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

		return arguments;
	}
}
