package org.overture.ide.vdmrt.debug.core.interpreter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.overture.ide.debug.interpreter.VdmjVMInterpreterRunner;

public class VdmRtVdmjVMInterpreterRunner extends VdmjVMInterpreterRunner
{
	protected Collection<? extends String> getOptionalArguments(
			IProject project, InterpreterConfig config, ILaunch launch)
	{
		// log
		List<String> arguments = new ArrayList<String>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		File logDir = new File(new File(getOutputFolder(config),"logs"), launch.getLaunchConfiguration().getName());
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
		try
		{
			project.refreshLocal(IProject.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{

			e.printStackTrace();
		}
		return arguments;
	}
}

