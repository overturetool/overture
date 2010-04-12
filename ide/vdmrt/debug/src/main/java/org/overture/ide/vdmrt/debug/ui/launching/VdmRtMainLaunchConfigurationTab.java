package org.overture.ide.vdmrt.debug.ui.launching;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;

public class VdmRtMainLaunchConfigurationTab extends
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
		return project.hasNature(IVdmRtCoreConstants.NATURE);
	}


//	protected Collection<? extends String> getOptionalArguments(
//			IProject project, InterpreterConfig config, ILaunch launch)
//	{
//		// log
//		List<String> arguments = new ArrayList<String>();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
//		Date date = new Date();
//		File logDir = new File(new File(getOutputFolder(config),"logs"), launch.getLaunchConfiguration().getName());
//		logDir.mkdirs();
//		String logFilename = dateFormat.format(date) + ".logrt";
//		System.out.println(logFilename);
//		File f = new File(logDir, logFilename);
//		if (!f.exists())
//		{
//			f.getParentFile().mkdirs();
//			try
//			{
//				f.createNewFile();
//			} catch (IOException e)
//			{
//
//				e.printStackTrace();
//			}
//		}
//
//		arguments.add("-log");
//		arguments.add(logDir.toURI().toASCIIString() + logFilename);
//		try
//		{
//			project.refreshLocal(IProject.DEPTH_INFINITE, null);
//		} catch (CoreException e)
//		{
//
//			e.printStackTrace();
//		}
//		return arguments;
//	}

}
