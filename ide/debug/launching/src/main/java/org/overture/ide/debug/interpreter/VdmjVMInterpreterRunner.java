package org.overture.ide.debug.interpreter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.console.ScriptConsoleServer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.launching.AbstractInterpreterRunner;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.debug.DbgpConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.overture.ide.debug.core.DebugCoreConstants;
import org.overture.ide.debug.launching.ClasspathUtils;
import org.overture.ide.debug.launching.IOvertureInterpreterRunnerConfig;
import org.overture.ide.debug.launching.VDMLaunchingConstants;
import org.overture.ide.utility.ProjectUtility;

public class VdmjVMInterpreterRunner extends AbstractInterpreterRunner
{

	protected VdmjVMInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

	public VdmjVMInterpreterRunner() {
		super(null);
	}

	static String debugVmMemoryOption = null;

	@SuppressWarnings("deprecation")
	public void doRunImpl(InterpreterConfig config, ILaunch launch,
			IOvertureInterpreterRunnerConfig iconfig, String contentType,
			String vdmjDialect) throws CoreException
	{

		IScriptProject proj = AbstractScriptLaunchConfigurationDelegate.getScriptProject(launch.getLaunchConfiguration());
		IJavaProject myJavaProject = JavaCore.create(proj.getProject());
		IVMInstall vmInstall = myJavaProject.exists() ? JavaRuntime.getVMInstall(myJavaProject)
				: JavaRuntime.getDefaultVMInstall();
		if (vmInstall == null)
			throw new CoreException(new Status(IStatus.ERROR, "", "Could not initialize VM for debug"));

		if (debugVmMemoryOption == null)
			debugVmMemoryOption = getDebugVmMemoryOption();
		if (debugVmMemoryOption != null)
			vmInstall.setVMArguments(new String[] { debugVmMemoryOption });// "-Xmx1024M"

		IVMRunner vmRunner = vmInstall.getVMRunner(ILaunchManager.DEBUG_MODE);
		if (vmRunner != null)
		{
			IProject project = proj.getProject();

			try
			{
				String[] newClassPath = getClassPath(myJavaProject);

				VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(iconfig.getRunnerClassName(config,
						launch,
						myJavaProject),
						newClassPath);
				// IPath scriptFilePath =
				// config.getScriptFilePath();
				// if (scriptFilePath == null)
				// {
				// throw new CoreException(new Status(IStatus.ERROR,
				// VDMLaunchingConstants.PLUGIN_ID,
				// "Script File name is not specified..."));
				// }

				// ********************
				// Create arguments:
				// Missing mandatory arguments
				// Usage: -h <host> -p <port> -k <ide key>
				// <-vdmpp|-vdmsl|-vdmrt> -e <expression> {<filename
				// URLs>}
				// VDMJ use these arguments:
				// 0: host
				// 1: port
				// 2: ideKey
				// 3: Dialect
				// 4: expression
				// 5..nFiles: files

				List<String> arguments = new ArrayList<String>();

				arguments.addAll(getConnectionArguments(config));
				arguments.add("-w"); // no warnings
				arguments.add("-q"); // no information

				// 3: dialect
				arguments.add("-" + vdmjDialect);

				Collection<? extends String> optionalArguments = getOptionalArguments(project,
						config,
						launch);
				if (optionalArguments != null)
					arguments.addAll(optionalArguments);

				arguments.add("-c");
				arguments.add(getCharSet(project, contentType));

				// 4: expression

				arguments.add("-e");
				arguments.add(buildLaunchExpression(launch));

				// 5-n: add files to the arguments

				arguments.addAll(getFiles(project, contentType));

				String args[] = new String[arguments.size()];
				arguments.toArray(args);

				vmConfig.setProgramArguments(args);
				vmConfig.setWorkingDirectory(config.getWorkingDirectoryPath()
						.toOSString());
				ILaunch launchr = new Launch(launch.getLaunchConfiguration(),
						ILaunchManager.DEBUG_MODE,
						null);
				iconfig.adjustRunnerConfiguration(vmConfig,
						config,
						launch,
						myJavaProject);
				vmRunner.run(vmConfig, launchr, null);
				IDebugTarget[] debugTargets = launchr.getDebugTargets();
				for (int a = 0; a < debugTargets.length; a++)
				{
					launch.addDebugTarget(debugTargets[a]);
				}
				IProcess[] processes = launchr.getProcesses();
				for (int a = 0; a < processes.length; a++)
					launch.addProcess(processes[a]);
				return;
			} catch (URISyntaxException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	private String getCharSet(IProject project, String contentType)
			throws CoreException
	{
		List<IFile> memberFilesList = ProjectUtility.getFiles(project,
				contentType);
		return memberFilesList.get(0).getCharset();
	}

	private Collection<? extends String> getFiles(IProject project,
			String contentType) throws CoreException
	{
		List<IFile> memberFilesList = ProjectUtility.getFiles(project,
				contentType);

		List<String> arguments = new ArrayList<String>();
		for (int a = 0; a < memberFilesList.size(); a++)
		{

			arguments.add(memberFilesList.get(a)
					.getLocationURI()
					.toASCIIString());
		}
		return arguments;
	}

	private Collection<? extends String> getConnectionArguments(
			InterpreterConfig config)
	{
		String host = (String) config.getProperty(DbgpConstants.HOST_PROP);
		if (host == null)
		{
			host = "";
		}

		String port = (String) config.getProperty(DbgpConstants.PORT_PROP);
		if (port == null)
		{
			port = "";
		}

		String sessionId = (String) config.getProperty(DbgpConstants.SESSION_ID_PROP);
		// System.out.println(sessionId);

		if (sessionId == null)
		{
			sessionId = "";
		}

		List<String> arguments = new ArrayList<String>();
		// 0: host
		// 1: port
		// 2: sessionID
		arguments.add("-h");
		arguments.add(host);
		arguments.add("-p");
		arguments.add(port);
		arguments.add("-k");
		arguments.add(sessionId);
		return arguments;
	}

	private String buildLaunchExpression(ILaunch launch) throws CoreException
	{
		String debugOperation = launch.getLaunchConfiguration()
				.getAttribute(DebugCoreConstants.DEBUGGING_OPERATION, "");

		String module = launch.getLaunchConfiguration()
				.getAttribute(DebugCoreConstants.DEBUGGING_MODULE, "");
		if (module.length() == 0)
			throw new CoreException(new Status(IStatus.ERROR,
					VDMLaunchingConstants.PLUGIN_ID,
					"Entry class not set in launch configuration"));

		return buildLaunchExpression(module, debugOperation);
	}

	protected String buildLaunchExpression(String module, String debugOperation)
	{
		return "new " + module + "." + debugOperation;
	}

	protected Collection<? extends String> getOptionalArguments(
			IProject project, InterpreterConfig config, ILaunch launch)
	{
		return null;
	}

	private static String getDebugVmMemoryOption()
	{
		String memoryOption = null;
		String eclipseCommands = System.getProperty("eclipse.commands");
		if (eclipseCommands != null)
		{
			StringTokenizer st = new StringTokenizer(eclipseCommands);
			boolean keyFound = false;
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();

				if (keyFound)
				{
					memoryOption = "-" + token;
					keyFound = false;
				}

				if (token.equals("-org.overture.ide.debug.memory"))
					keyFound = true;

			}
		}

		return memoryOption;
	}

	public static String[] getClassPath(IJavaProject myJavaProject)
			throws IOException, URISyntaxException
	{
		final List<String> result = new ArrayList<String>();
		// TODO ClasspathUtils.collectClasspath(new String[] {
		// GenericOvertureInstallType.EMBEDDED_VDMJ_BUNDLE_ID,
		// GenericOvertureInstallType.DBGP_FOR_VDMJ_BUNDLE_ID }, result);
		ClasspathUtils.collectClasspath(new String[] { "org.overture.ide.generated.vdmj", },
				result);
		try
		{
			final String[] classPath = computeBaseClassPath(myJavaProject);
			for (int i = 0; i < classPath.length; ++i)
			{
				result.add(classPath[i]);
			}
		} catch (CoreException e)
		{
		}
		return result.toArray(new String[result.size()]);
	}

	protected static String[] computeBaseClassPath(IJavaProject myJavaProject)
			throws CoreException
	{
		if (myJavaProject != null)
		{
			return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
		} else if (myJavaProject == null)
			return CharOperation.NO_STRINGS;
		return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
	}

	protected String constructProgramString(InterpreterConfig config)
			throws CoreException
	{

		return "";
	}

	protected String[] alterCommandLine(String[] cmdLine, String id)
	{
		ScriptConsoleServer server = ScriptConsoleServer.getInstance();
		String port = Integer.toString(server.getPort());
		String[] newCmdLine = new String[cmdLine.length + 4];

		newCmdLine[0] = cmdLine[0];
		newCmdLine[1] = DLTKCore.getDefault()
				.getStateLocation()
				.append("overture_proxy")
				.toOSString();

		newCmdLine[2] = "localhost";
		newCmdLine[3] = port;
		newCmdLine[4] = id;

		for (int i = 1; i < cmdLine.length; ++i)
		{
			newCmdLine[i + 4] = cmdLine[i];
		}

		return newCmdLine;
	}
}
