package org.overture.ide.debug.core.launching;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.ClasspathUtils;
import org.overture.ide.debug.core.IDbgpService;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.IDebugPreferenceConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.internal.VdmDebugTarget;
import org.overture.ide.debug.ui.log.VdmDebugLogManager;
import org.overture.ide.debug.utils.ProcessConsolePrinter;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overturetool.vdmj.util.Base64;

/**
 * @see http://www.eclipse.org/articles/Article-Debugger/how-to.html
 * @author ari
 */
public class VdmLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate
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
			//set launch encoding to UTF-8. Mainly used to set console encoding.
			launch.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING, "UTF-8");
			
			List<String> commandList = initializeLaunch(launch, configuration,
					mode);

			final VdmDebugTarget target = (VdmDebugTarget) launch
					.getDebugTarget();

			final DebugSessionAcceptor acceptor = new DebugSessionAcceptor(
					target, monitor);
			try
			{
				monitor.worked(1);
				target.setProcess(launchExternalProcess(launch, commandList,
						getVdmProject(configuration),configuration));
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
	 *             if debuggingProcess terminated, monitor is canceled or // *
	 *             timeout
	 */
	protected void waitDebuggerConnected(ILaunch launch,
			DebugSessionAcceptor acceptor) throws CoreException
	{
		int timeout =  VdmDebugPlugin.getConnectionTimeout();
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
			ILaunchConfiguration configuration, String mode)
			throws CoreException
	{
			List<String> commandList = null;
		Integer debugSessionId = new Integer(getSessionId());
		if (useRemoteDebug(configuration))
		{
			debugSessionId = 1;
			// debugComm.removeSession(debugSessionId.toString());
		}

		// Storing the debug session id
		ILaunchConfigurationWorkingCopy lcwc = configuration.getWorkingCopy();
		lcwc.setAttribute(IDebugConstants.VDM_DEBUG_SESSION_ID, debugSessionId);
		lcwc.doSave();

		commandList = new ArrayList<String>();

		IVdmProject vdmProject = getVdmProject(configuration);

		Assert.isNotNull(vdmProject, " Project not found: "
				+ configuration.getAttribute(
						IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, ""));

		String charSet = getProject(configuration).getDefaultCharset();

		commandList.add("-h");
		commandList.add("localhost");
		commandList.add("-p");
		commandList.add(new Integer(VdmDebugPlugin.getDefault()
				.getDbgpService().getPort()).toString());
		commandList.add("-k");
			commandList.add(debugSessionId.toString());
		commandList.add("-w");
		commandList.add("-q");
		commandList.add(vdmProject.getDialect().getArgstring());
		commandList.add("-r");
		commandList.add(vdmProject.getLanguageVersionName());
		commandList.add("-c");
		commandList.add(charSet);
		if (!isRemoteControllerEnabled(configuration))
		{
			commandList.add("-e64");
			commandList.add(getExpressionBase64(configuration, charSet));
			commandList.add("-default64");
			commandList.add(getDefaultBase64(configuration, charSet));
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

		commandList.addAll(getSpecFiles(vdmProject));
		if (useRemoteDebug(configuration))
		{
			System.out.println("Debugger Arguments:\n"
					+ getArgumentString(commandList));
		}
		commandList.add(0, "java");

		commandList.addAll(1, getClassPath(vdmProject, configuration));
		commandList.add(3, IDebugConstants.DEBUG_ENGINE_CLASS);
		commandList.addAll(1, getVmArguments(configuration));

		VdmDebugTarget target = null;
		if (mode.equals(ILaunchManager.DEBUG_MODE))
		{
			IDbgpService service = VdmDebugPlugin.getDefault().getDbgpService();

			if (!service.available())
			{
				abort("Could not create DBGP Service", null);
			}

			target = new VdmDebugTarget(IDebugConstants.ID_VDM_DEBUG_MODEL,
					service, debugSessionId.toString(), launch, null);
			target.setVdmProject(vdmProject);
			launch.addDebugTarget(target);
			target.toggleClassVariables(true);
			target.toggleGlobalVariables(true);
			target.toggleLocalVariables(true);

		}
		return commandList;
	}

	private synchronized int getSessionId()
	{

		return (sessionId++);
	}

	private Collection<? extends String> getVmArguments(
			ILaunchConfiguration configuration) throws CoreException
	{
		List<String> options = new Vector<String>();
		String opt = configuration.getAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, "");
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
	 * Intended to be used when sub classing the delegate to add additional
	 * parameters to the launch of VDMJ
	 * 
	 * @param project
	 *            the project launched
	 * @param configuration
	 *            the launch configuration
	 * @return a list of parameters to be added to the command line just before
	 *         the files
	 * @throws CoreException 
	 */
	protected Collection<? extends String> getExtendedCommands(
			IVdmProject project, ILaunchConfiguration configuration) throws CoreException
	{
		return new Vector<String>();
	}

	private String getRemoteControllerName(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, "");
	}

	private String getArgumentString(List<String> args)
	{
		String executeString = "";
		for (String string : args)
		{
			executeString += string + " ";
		}
		return executeString.trim();

	}

	private boolean useRemoteDebug(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false);
	}

	private boolean isRemoteControllerEnabled(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL, "").length() > 0;
	}

	private boolean hasTrace(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(
				IDebugConstants.VDM_LAUNCH_CONFIG_IS_TRACE, false);
	}

	protected File getOutputFolder(IVdmProject project, ILaunchConfiguration configuration) throws CoreException
	{
		File outputDir = new File(getProject(configuration).getLocation().toFile(), "generated");
		outputDir.mkdirs();
		return outputDir;
	}

	private IProcess launchExternalProcess(ILaunch launch,
			List<String> commandList, IVdmProject project, ILaunchConfiguration configuration) throws CoreException
	{

		String executeString = getArgumentString(commandList);

		Process process = null;
		// System.out.println(executeString);
		try
		{
			if (!useRemoteDebug(launch.getLaunchConfiguration()))
			{
				process = Runtime.getRuntime().exec(executeString, null,
						getProject(configuration).getLocation().toFile());
				
			} else
			{
				process = Runtime.getRuntime().exec("help");
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
			defaultModule = configuration.getAttribute(
					IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT, "");

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

			expression = configuration.getAttribute(
					IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, "");
			return Base64.encode(expression.getBytes(charset)).toString();
		} catch (UnsupportedEncodingException e)
		{
			abort("Unsuported encoding used for expression", e);
		}
		return "";
	}

	private List<String> getClassPath(IVdmProject project,
			ILaunchConfiguration configuration) throws CoreException
	{
		List<String> commandList = new Vector<String>();
		List<String> entries = new Vector<String>();
		// get the bundled class path of the debugger
		ClasspathUtils.collectClasspath(
				new String[] { IDebugConstants.DEBUG_ENGINE_BUNDLE_ID },
				entries);
		// get the class path for all jars in the project lib folder
		File lib = new File(getProject(configuration).getLocation().toFile(),
				"lib");
		if (lib.exists() && lib.isDirectory())
		{
			for (File f : getAllFiles(lib))
			{
				if (f.getName().toLowerCase().endsWith(".jar"))
				{
					entries.add(toPlatformPath(f.getAbsolutePath()));
				}
			}
		}

		if (entries.size() > 0)
		{
			commandList.add("-cp");
			String classPath = " ";
			for (String cp : entries)
			{
				if (cp.toLowerCase().endsWith(".jar"))
				{
					classPath += toPlatformPath(cp) + getCpSeperator();
				}
			}
			classPath = classPath.substring(0, classPath.length() - 1);
			commandList.add(classPath.trim());

		}
		return commandList;
	}

	private static List<File> getAllFiles(File file)
	{
		List<File> files = new Vector<File>();
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				files.addAll(getAllFiles(f));
			}

		} else
		{
			files.add(file);
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
	
	protected static String toPlatformPath(String path)
	{
		if(isWindowsPlatform())
		{
			return "\""+path+"\"";
		}else
		{
			return path.replace(" ", "\\ ");
		}
	}

	/**
	 * Throws an exception with a new status containing the given message and
	 * optional exception.
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
		throw new CoreException((IStatus) new Status(IStatus.ERROR,
				IDebugConstants.ID_VDM_DEBUG_MODEL, 0, message, e));
	}

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free
	 * port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free
	 *         port
	 */
	public static int findFreePort()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e)
		{
		} finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				} catch (IOException e)
				{
				}
			}
		}
		return -1;
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
			IVdmProject vdmProject = (IVdmProject) project
					.getAdapter(IVdmProject.class);
			return vdmProject;
		}
		return null;
	}

	static private IProject getProject(ILaunchConfiguration configuration)
			throws CoreException
	{
		return ResourcesPlugin.getWorkspace().getRoot().getProject(
				configuration.getAttribute(
						IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, ""));
	}

}
