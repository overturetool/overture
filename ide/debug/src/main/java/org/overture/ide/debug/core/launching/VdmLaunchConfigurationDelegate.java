/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.debug.core.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.ClasspathUtils;
import org.overture.ide.debug.core.IDbgpService;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.internal.VdmDebugTarget;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overturetool.vdmj.util.Base64;

/**
 * @see http://www.eclipse.org/articles/Article-Debugger/how-to.html
 * @author ari
 */
public class VdmLaunchConfigurationDelegate extends LaunchConfigurationDelegate

{

	static int sessionId = 0;;

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{

		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("Debugger launching", 4);
		if (monitor.isCanceled())
		{
			return;
		}
		try
		{
			// set launch encoding to UTF-8. Mainly used to set console encoding.
			launch.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING, "UTF-8");

			List<String> commandList = initializeLaunch(launch, configuration, mode, monitor);

			final VdmDebugTarget target = (VdmDebugTarget) launch.getDebugTarget();

			final DebugSessionAcceptor acceptor = new DebugSessionAcceptor(target, monitor);
			try
			{
				if (!useRemoteDebug(configuration))
				{
					target.setProcess(launchExternalProcess(launch, commandList, getVdmProject(configuration), configuration));
				}
				monitor.worked(1);

				// Waiting for debugging engine to connect
				waitDebuggerConnected(launch, acceptor);
			} finally
			{
				acceptor.disposeStatusHandler();
			}
		} catch (CoreException e)
		{
			launch.terminate();
			throw e;
		} finally
		{
			monitor.done();
		}

	}

	/**
	 * Waiting debugging process to connect to current launch
	 * 
	 * @param launch
	 *            launch to connect to
	 * @param acceptor
	 * @param monitor
	 *            progress monitor
	 * @throws CoreException
	 *             if debuggingProcess terminated, monitor is canceled or // * timeout
	 */
	protected void waitDebuggerConnected(ILaunch launch,
			DebugSessionAcceptor acceptor) throws CoreException
	{
		int timeout = VdmDebugPlugin.getConnectionTimeout();
		if (!acceptor.waitConnection(timeout))
		{
			launch.terminate();
			return;
			// abort(InterpreterMessages.errDebuggingEngineNotConnected, null);
		}
		if (!acceptor.waitInitialized(60 * 60 * 1000))
		{
			launch.terminate();
			abort("errDebuggingEngineNotInitialized", null);
		}
	}

	private List<String> initializeLaunch(ILaunch launch,
			ILaunchConfiguration configuration, String mode,
			IProgressMonitor monitor) throws CoreException
	{
		List<String> commandList = null;
		
		Integer debugSessionId = Integer.valueOf(getSessionId());
		if (useRemoteDebug(configuration))
		{
			debugSessionId = 1;
			// debugComm.removeSession(debugSessionId.toString());
		}

		commandList = new ArrayList<String>();

		IVdmProject vdmProject = getVdmProject(configuration);

		Assert.isNotNull(vdmProject, " Project not found: "
				+ configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, ""));

		if (vdmProject == null
				|| !VdmTypeCheckerUi.typeCheck(vdmProject, monitor))
		{
			abort("Cannot launch a project (" + vdmProject
					+ ") with type errors, please check the problems view", null);
		}

		String charSet = getProject(configuration).getDefaultCharset();

		commandList.add("-h");
		commandList.add("localhost");
		commandList.add("-p");
		int port = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OVERRIDE_PORT, -1);
		// Start debug service with the override port or just start the service and get the port
		if (port > 0)
		{
			port = VdmDebugPlugin.getDefault().getDbgpService(port).getPort();
		} else
		{
			port = VdmDebugPlugin.getDefault().getDbgpService().getPort();
		}
		commandList.add(Integer.valueOf(port).toString());

		commandList.add("-k");
		commandList.add(debugSessionId.toString());
		commandList.add("-w");
		commandList.add("-q");
		commandList.add(vdmProject.getDialect().getArgstring());
		commandList.add("-r");
		commandList.add(vdmProject.getLanguageVersionName());
		// set disable interpreter settings
		if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true))// vdmProject.hasPrechecks())
		{
			commandList.add("-pre");
		}
		if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true))// vdmProject.hasPostchecks())
		{
			commandList.add("-post");
		}
		if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true))// vdmProject.hasInvchecks())
		{
			commandList.add("-inv");
		}
		if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true))// vdmProject.hasDynamictypechecks())
		{
			commandList.add("-dtc");
		}
		if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true))// vdmProject.hasMeasurechecks())
		{
			commandList.add("-measures");
		}

		commandList.add("-c");
		commandList.add(charSet);
		if (!isRemoteControllerEnabled(configuration))
		{
			commandList.add("-e64");
			commandList.add(getExpressionBase64(configuration, charSet));
			String default64 = getDefaultBase64(configuration, charSet);
			if (default64.trim().length() > 0)
			{
				commandList.add("-default64");
				commandList.add(getDefaultBase64(configuration, charSet));
			}
		} else
		{
			// temp fix for commanline args of dbgreader
			commandList.add("-e64");
			commandList.add(Base64.encode("A".getBytes()).toString());
		}

		// if (isCoverageEnabled(configuration))
		// {
		// commandList.add("-coverage");
		// commandList.add(getCoverageDir(project));
		// }

		if (isRemoteControllerEnabled(configuration))
		{
			commandList.add("-remote");
			commandList.add(getRemoteControllerName(configuration));
		}

		if (hasTrace(configuration))
		{
			commandList.add("-t");
		}

		commandList.add("-consoleName");
		commandList.add("LaunchConfigurationExpression");

		commandList.addAll(getExtendedCommands(vdmProject, configuration));

		commandList.add("-baseDir");
		commandList.add(getProject(configuration).getLocationURI().toASCIIString());

		commandList.addAll(getSpecFiles(vdmProject));
		if (useRemoteDebug(configuration))
		{
			System.out.println("Debugger Arguments:\n"
					+ getArgumentString(commandList));
		}
		commandList.add(0, "java");

		File vdmjPropertiesFile =prepareCustomDebuggerProperties(vdmProject, configuration);
		commandList.addAll(1, getClassPath(vdmProject, configuration,vdmjPropertiesFile));
		commandList.add(3, IDebugConstants.DEBUG_ENGINE_CLASS);
		commandList.addAll(1, getVmArguments(configuration));

		VdmDebugTarget target = null;
		// Debug mode
		if (mode.equals(ILaunchManager.DEBUG_MODE))
		{
			IDbgpService service = VdmDebugPlugin.getDefault().getDbgpService();

			if (!service.available())
			{
				abort("Could not create DBGP Service", null);
			}

			DebugPlugin.getDefault().getBreakpointManager().setEnabled(true);

			target = new VdmDebugTarget(IDebugConstants.ID_VDM_DEBUG_MODEL, service, debugSessionId.toString(), launch, null);
			target.setVdmProject(vdmProject);
			launch.addDebugTarget(target);
			target.toggleClassVariables(true);
			target.toggleGlobalVariables(true);
			target.toggleLocalVariables(true);

		}
		// Run mode
		else if (mode.equals(ILaunchManager.RUN_MODE))
		{
			IDbgpService service = VdmDebugPlugin.getDefault().getDbgpService();

			if (!service.available())
			{
				abort("Could not create DBGP Service", null);
			}

			DebugPlugin.getDefault().getBreakpointManager().setEnabled(false);

			target = new VdmDebugTarget(IDebugConstants.ID_VDM_DEBUG_MODEL, service, debugSessionId.toString(), launch, null);
			target.setVdmProject(vdmProject);
			launch.addDebugTarget(target);

			target.toggleClassVariables(true);
			target.toggleGlobalVariables(true);
			target.toggleLocalVariables(true);

		}
		return commandList;
	}

	private File prepareCustomDebuggerProperties(IVdmProject vdmProject,
			ILaunchConfiguration configuration) throws CoreException
	{
		try
		{
			String properties = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CUSTOM_DEBUGGER_PROPERTIES, "");
		
			File outputDir = vdmProject.getModelBuildPath().getOutput().getLocation().toFile();
			if (outputDir.mkdirs())
			{
				// ignore
			}
			File vdmjProperties = new File(outputDir,"vdmj.properties");
			
			PrintWriter out=null;
			try{
				FileWriter outFile = new FileWriter(vdmjProperties);
				out = new PrintWriter(outFile);
				for (String p : properties.split(";"))
				{
					out.println(p);
				}
				
				return vdmjProperties;
			}catch(IOException e)
			{
				abort("Faild to create custom properties file while writing properties", e);
			}finally
			{
				out.close();
			}
		} catch (CoreException e)
		{
			abort("Faild to create custom properties file", e);
		}
		return null;
	}

	private synchronized int getSessionId()
	{
		return (sessionId++);
	}

	private Collection<? extends String> getVmArguments(
			ILaunchConfiguration configuration) throws CoreException
	{
		List<String> options = new Vector<String>();
		String opt = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, "");
		if (opt.trim().length() != 0)
		{
			String[] opts = opt.split(" ");
			for (String o : opts)
			{
				o = o.trim();
				if (o.startsWith("-"))
				{
					options.add(o);
				}
			}
		}
		return options;
	}

	/**
	 * Intended to be used when sub classing the delegate to add additional parameters to the launch of VDMJ
	 * 
	 * @param project
	 *            the project launched
	 * @param configuration
	 *            the launch configuration
	 * @return a list of parameters to be added to the command line just before the files
	 * @throws CoreException
	 */
	protected Collection<? extends String> getExtendedCommands(
			IVdmProject project, ILaunchConfiguration configuration)
			throws CoreException
	{
		return new Vector<String>();
	}

	private String getRemoteControllerName(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, "");
	}

	private String getArgumentString(List<String> args)
	{
		StringBuffer executeString = new StringBuffer();
		for (String string : args)
		{
			executeString.append(string);
			executeString.append(" ");
		}
		return executeString.toString().trim();

	}

	private boolean useRemoteDebug(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false);
	}

	private boolean isRemoteControllerEnabled(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, "").length() > 0;
	}

	private boolean hasTrace(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_IS_TRACE, false);
	}

	protected File getOutputFolder(IVdmProject project,
			ILaunchConfiguration configuration) throws CoreException
	{
		File outputDir = project.getModelBuildPath().getOutput().getLocation().toFile();// new
																						// File(getProject(configuration).getLocation().toFile(),
																						// "generated");
		outputDir.mkdirs();
		return outputDir;
	}

	private IProcess launchExternalProcess(ILaunch launch,
			List<String> commandList, IVdmProject project,
			ILaunchConfiguration configuration) throws CoreException
	{
		//String executeString = getArgumentString(commandList);

		ProcessBuilder procBuilder = new ProcessBuilder(commandList);
		//System.out.println(executeString);
		Process process = null;
		// System.out.println(executeString);
		try
		{
			if (!useRemoteDebug(launch.getLaunchConfiguration()))
			{
				process = procBuilder.start();
				//process = Runtime.getRuntime().exec(executeString, null, getProject(configuration).getLocation().toFile());

			} else
			{
				process = Runtime.getRuntime().exec("java -version");
			}
		} catch (IOException e)
		{
			abort("Could not launch debug process", e);
		}

		return DebugPlugin.newProcess(launch, process, "VDMJ debugger");
	}

	private String getDefaultBase64(ILaunchConfiguration configuration,
			String charset) throws CoreException
	{
		String defaultModule;
		try
		{
			defaultModule = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT, "");

			return Base64.encode(defaultModule.getBytes(charset)).toString();
		} catch (UnsupportedEncodingException e)
		{
			abort("Unsuported encoding used for expression", e);
		}
		return "";
	}

	private String getExpressionBase64(ILaunchConfiguration configuration,
			String charset) throws CoreException
	{
		String expression;
		try
		{
			if (configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CONSOLE_ENTRY, false))
			{
				expression = "###CONSOLE###";
			} else
			{
				expression = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, "");
			}

			return Base64.encode(expression.getBytes(charset)).toString();
		} catch (UnsupportedEncodingException e)
		{
			abort("Unsuported encoding used for expression", e);
		}
		return "";
	}

	private List<String> getClassPath(IVdmProject project,
			ILaunchConfiguration configuration, File vdmjPropertiesFile) throws CoreException
	{
		List<String> commandList = new Vector<String>();
		List<String> entries = new Vector<String>();
		// get the bundled class path of the debugger
		ClasspathUtils.collectClasspath(new String[] { IDebugConstants.DEBUG_ENGINE_BUNDLE_ID }, entries);
		// get the class path for all jars in the project lib folder
		File lib = new File(getProject(configuration).getLocation().toFile(), "lib");
		if (lib.exists() && lib.isDirectory())
		{
			for (File f : getAllDirectories(lib))
			{
				entries.add(f.getAbsolutePath());
			}

			for (File f : getAllFiles(lib, new HashSet<String>(Arrays.asList(new String[] { ".jar" }))))
			{
				entries.add(f.getAbsolutePath());
			}
		}
		
		//add custom properties file vdmj.properties
		entries.add(vdmjPropertiesFile.getParentFile().getAbsolutePath());

		if (entries.size() > 0)
		{
			commandList.add("-cp");
			StringBuffer classPath = new StringBuffer(" ");
			for (String cp : new HashSet<String>(entries))// remove dublicates
			{
				classPath.append(toPlatformPath(cp));
				classPath.append(getCpSeperator());
			}
			classPath.deleteCharAt(classPath.length() - 1);
			commandList.add(classPath.toString().trim());

		}
		return commandList;
	}

	private static List<File> getAllDirectories(File file)
	{
		List<File> files = new Vector<File>();
		if (file.isDirectory())
		{
			files.add(file);
			for (File f : file.listFiles())
			{
				files.addAll(getAllDirectories(f));
			}

		}
		return files;
	}

	private static List<File> getAllFiles(File file, Set<String> extensionFilter)
	{
		List<File> files = new Vector<File>();
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				files.addAll(getAllFiles(f, extensionFilter));
			}

		} else
		{
			for (String filter : extensionFilter)
			{
				if (file.getAbsolutePath().endsWith(filter))
				{
					files.add(file);
				}
			}

		}
		return files;
	}

	private String getCpSeperator()
	{
		if (isWindowsPlatform())
			return ";";
		else
			return ":";
	}

	public static boolean isWindowsPlatform()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	public static boolean isMacPlatform()
	{
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}

	protected static String toPlatformPath(String path)
	{
		if (isWindowsPlatform())
		{
			return "\"" + path + "\"";
		} else
		{
			return path.replace(" ", "\\ ");
		}
	}

	/**
	 * Throws an exception with a new status containing the given message and optional exception.
	 * 
	 * @param message
	 *            error message
	 * @param e
	 *            underlying exception
	 * @throws CoreException
	 */
	private void abort(String message, Throwable e) throws CoreException
	{
		// TODO: the plug-in code should be the example plug-in, not Perl debug
		// model id
		throw new CoreException((IStatus) new Status(IStatus.ERROR, IDebugConstants.ID_VDM_DEBUG_MODEL, 0, message, e));
	}

	private List<String> getSpecFiles(IVdmProject project) throws CoreException
	{
		List<String> files = new Vector<String>();

		for (IVdmSourceUnit unit : project.getSpecFiles())
		{
			files.add(unit.getSystemFile().toURI().toASCIIString());
		}

		return files;
	}

	static public IVdmProject getVdmProject(ILaunchConfiguration configuration)
			throws CoreException
	{

		IProject project = getProject(configuration);

		if (project != null)
		{
			IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
			return vdmProject;
		}
		return null;
	}

	static private IProject getProject(ILaunchConfiguration configuration)
			throws CoreException
	{
		return ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, ""));
	}

}
