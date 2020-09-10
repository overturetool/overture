/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.osgi.framework.Bundle;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.debug.core.IDbgpService;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.IDebugPreferenceConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.internal.VdmDebugTarget;
import org.overture.ide.debug.ui.DebugConsoleManager;
import org.overture.ide.debug.utils.VdmProjectClassPathCollector;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.util.Base64;

/**
 * @see http://www.eclipse.org/articles/Article-Debugger/how-to.html
 * @author ari and kel
 */
@SuppressWarnings("javadoc")
public class VdmLaunchConfigurationDelegate extends LaunchConfigurationDelegate

{

	public static final String ORG_OVERTURE_IDE_PLUGINS_PROBRUNTIME = "org.overture.ide.plugins.probruntime.core";
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
		int timeout = VdmDebugPlugin.getDefault().getConnectionTimeout();
		if (!acceptor.waitConnection(timeout))
		{
			launch.terminate();
			return;
		}
		if (!acceptor.waitInitialized(60 * 60 * 1000))
		{
			launch.terminate();
			abort("errDebuggingEngineNotInitialized", null);
		}
	}

	/**
	 * generate commandline argments for the debugger and create the debug target
	 * @param launch
	 * @param configuration
	 * @param mode
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private List<String> initializeLaunch(ILaunch launch,
			ILaunchConfiguration configuration, String mode,
			IProgressMonitor monitor) throws CoreException
	{
		List<String> commandList = null;

		Integer debugSessionId = Integer.valueOf(getSessionId());
		if (useRemoteDebug(configuration))
		{
			debugSessionId = 1;
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
		int port = VdmDebugPlugin.getDefault().getDbgpService().getPort();

		// Hook for external tools to direct the debugger to listen on a specific port
		int overridePort = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_OVERRIDE_PORT, IDebugPreferenceConstants.DBGP_AVAILABLE_PORT);
		if (overridePort != IDebugPreferenceConstants.DBGP_AVAILABLE_PORT)
		{
			port = VdmDebugPlugin.getDefault().getDbgpService(overridePort).getPort();
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
		if (vdmProject.hasUseStrictLetDef())
		{
			commandList.add("-strict");
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
		commandList.add(1, IDebugConstants.DEBUG_ENGINE_CLASS);

		if (configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_SHOW_VM_SETTINGS, false))
		{
			commandList.addAll(1, Arrays.asList(new String[] { "-XshowSettings:all" }));
		}
		
		commandList.addAll(1, getVmArguments(configuration));

		if (useRemoteDebug(configuration))
		{
			System.out.println("Full Debugger Arguments:\n"
					+ getArgumentString(commandList));
		}

		VdmDebugTarget target = null;
		// Debug mode
		if (mode.equals(ILaunchManager.DEBUG_MODE))
		{
			IDbgpService service = VdmDebugPlugin.getDefault().getDbgpService();

			if (!service.available())
			{
				abort("Could not create DBGP Service", null);
			}

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

			target = new VdmDebugTarget(IDebugConstants.ID_VDM_DEBUG_MODEL, service, debugSessionId.toString(), launch, null);
			target.setVdmProject(vdmProject);
			launch.addDebugTarget(target);

			target.toggleClassVariables(true);
			target.toggleGlobalVariables(true);
			target.toggleLocalVariables(true);

		}
		return commandList;
	}

	protected String[] getDebugEngineBundleIds()
	{
		List<String> ids = new ArrayList<String>(Arrays.asList(IDebugConstants.DEBUG_ENGINE_BUNDLE_IDS));

		if (VdmDebugPlugin.getDefault().getPreferenceStore().getBoolean(IDebugPreferenceConstants.PREF_DBGP_ENABLE_EXPERIMENTAL_MODELCHECKER))
		{
			ids.add(ORG_OVERTURE_IDE_PLUGINS_PROBRUNTIME);
		}
		return ids.toArray(new String[] {});
	}

	private File prepareCustomDebuggerProperties(IVdmProject project,
			ILaunchConfiguration configuration) throws CoreException
	{
		List<String> properties = new Vector<String>();

		String propertyCfg = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CUSTOM_DEBUGGER_PROPERTIES, "");

		for (String p : propertyCfg.split(";"))
		{
			properties.add(p);
		}
		return writePropertyFile(project, "vdmj.properties", properties);
	}

	/**
	 * Create the custom overture.properties file loaded by the debugger
	 * @param project
	 * @param configuration a configuration or null
	 * @return
	 * @throws CoreException
	 */
	public static File prepareCustomOvertureProperties(IVdmProject project,
			ILaunchConfiguration configuration) throws CoreException
	{
		List<String> properties = new Vector<String>();

		if (VdmDebugPlugin.getDefault().getPreferenceStore().getBoolean(IDebugPreferenceConstants.PREF_DBGP_ENABLE_EXPERIMENTAL_MODELCHECKER))
		{
			properties.add(getProbHomeProperty());
		}

		return writePropertyFile(project, "overture.properties", properties);
	}

	public static File writePropertyFile(IVdmProject project, String filename,
			List<String> properties) throws CoreException
	{
		try
		{

			File outputDir = project.getModelBuildPath().getOutput().getLocation().toFile();
			if (outputDir.mkdirs())
			{
				// ignore
			}
			File vdmjProperties = new File(outputDir, filename);

			PrintWriter out = null;
			try
			{
				FileWriter outFile = new FileWriter(vdmjProperties);
				out = new PrintWriter(outFile);

				for (String property : properties)
				{
					out.println(property);
				}

				return vdmjProperties;
			} catch (IOException e)
			{
				abort("Faild to create custom properties file while writing properties", e);
			} finally
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
		return sessionId++;
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
	 * Obtains the prob bundled executable and returns the prob.home property set to that location
	 * @return the prob.home property if available or empty string
	 */
	public static String getProbHomeProperty()
	{
		final Bundle bundle = Platform.getBundle(ORG_OVERTURE_IDE_PLUGINS_PROBRUNTIME);
		if (bundle != null)
		{
			URL buildInfoUrl = FileLocator.find(bundle, new Path("prob/build_info.txt"), null);

			try
			{
				if (buildInfoUrl != null)
				{
					URL buildInfofileUrl = FileLocator.toFileURL(buildInfoUrl);
					if (buildInfofileUrl != null)
					{
						File file = new File(buildInfofileUrl.getFile());

						return "system."+"prob.home=" + file.getParentFile().getPath().replace('\\', '/');
					}

				}
			} catch (IOException e)
			{
			}
		}
		return "";
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
		outputDir.mkdirs();
		return outputDir;
	}

	private IProcess launchExternalProcess(ILaunch launch,
			List<String> commandList, IVdmProject project,
			ILaunchConfiguration configuration) throws CoreException
	{
		ProcessBuilder procBuilder = new ProcessBuilder(commandList);

		File vdmjPropertiesFile = prepareCustomDebuggerProperties(project, configuration);
		File overturePropertiesFile = prepareCustomOvertureProperties(project, configuration);
		String classpath = VdmProjectClassPathCollector.toCpEnvString(VdmProjectClassPathCollector.getClassPath(getProject(configuration), getDebugEngineBundleIds(), vdmjPropertiesFile,overturePropertiesFile));

		Map<String, String> env = procBuilder.environment();
		env.put("CLASSPATH", classpath);

		Process process = null;
		try
		{
			procBuilder.directory(getProject(configuration).getLocation().toFile());
			if (!useRemoteDebug(launch.getLaunchConfiguration()))
			{
				process = procBuilder.start();

			} else
			{
				System.out.println("CLASSPATH = " + classpath);
				process = Runtime.getRuntime().exec("java -version");
			}
		} catch (IOException e)
		{
			abort("Could not launch debug process", e);
		}

		return DebugPlugin.newProcess(launch, process, "Overture debugger");
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

	/**
	 * Throws an exception with a new status containing the given message and optional exception.
	 * 
	 * @param message
	 *            error message
	 * @param e
	 *            underlying exception
	 * @throws CoreException
	 */
	private static void abort(String message, Throwable e) throws CoreException
	{
		throw new CoreException((IStatus) new Status(IStatus.ERROR, IDebugConstants.PLUGIN_ID, 0, message, e));
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
