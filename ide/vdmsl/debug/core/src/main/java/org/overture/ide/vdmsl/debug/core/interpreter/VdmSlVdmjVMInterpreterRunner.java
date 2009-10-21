package org.overture.ide.vdmsl.debug.core.interpreter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
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
import org.overture.ide.debug.launching.ClasspathUtils;
import org.overture.ide.debug.launching.IOvertureInterpreterRunnerConfig;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.vdmsl.debug.core.VdmSlDebugConstants;

public class VdmSlVdmjVMInterpreterRunner extends AbstractInterpreterRunner {

	protected VdmSlVdmjVMInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

	// private static String[] exts = new String[] { "vpp", "tex", "vdm",
	// "vdmpp", "vdmsl", "vdmrt" };

	/**
	 * This method returns a list of files under the given directory or its
	 * subdirectories. The directories themselves are not returned.
	 * 
	 * @param dir
	 *            a directory
	 * @return list of IResource objects representing the files under the given
	 *         directory and its subdirectories
	 */
//	private static ArrayList<String> getAllMemberFilesString(IContainer dir,
//			String[] exts) {
//		ArrayList<String> list = new ArrayList<String>();
//		IResource[] arr = null;
//		try {
//			arr = dir.members();
//		} catch (CoreException e) {
//		}
//
//		for (int i = 0; arr != null && i < arr.length; i++) {
//			if (arr[i].getType() == IResource.FOLDER) {
//				list.addAll(getAllMemberFilesString((IFolder) arr[i], exts));
//			} else {
//
//				for (int j = 0; j < exts.length; j++) {
//					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension())) {
//						list.add(arr[i].getLocation().toOSString());
//						break;
//					}
//				}
//			}
//		}
//		return list;
//	}
	// TODO remove if this works ok 

	public static void doRunImpl(InterpreterConfig config, ILaunch launch,
			IOvertureInterpreterRunnerConfig iconfig) throws CoreException {

		String host = (String) config.getProperty(DbgpConstants.HOST_PROP);
		if (host == null) {
			host = "";
		}

		String port = (String) config.getProperty(DbgpConstants.PORT_PROP);
		if (port == null) {
			port = "";
		}

		String sessionId = (String) config.getProperty(DbgpConstants.SESSION_ID_PROP);
		System.out.println(sessionId);

		if (sessionId == null) {
			sessionId = "";
		}

		IScriptProject proj = AbstractScriptLaunchConfigurationDelegate.getScriptProject(launch.getLaunchConfiguration());
		IJavaProject myJavaProject = JavaCore.create(proj.getProject());
		IVMInstall vmInstall = myJavaProject.exists() ? JavaRuntime.getVMInstall(myJavaProject)
				: JavaRuntime.getDefaultVMInstall();
		if (vmInstall != null) {
			IVMRunner vmRunner = vmInstall.getVMRunner(ILaunchManager.DEBUG_MODE);
			if (vmRunner != null) {
				{

					try {

						try {
							String[] newClassPath = getClassPath(myJavaProject);

							VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(iconfig.getRunnerClassName(
									config, launch, myJavaProject), newClassPath);
							IPath scriptFilePath = config.getScriptFilePath();
							if (scriptFilePath == null) {
								throw new CoreException(new Status(IStatus.ERROR, VdmSlDebugConstants.VDMSL_DEBUG_PLUGIN_ID, "Script File name is not specified..."));
							}

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

							int argNumber = 0;
							IProject project = proj.getProject();

							List<String> memberFilesList = getProjectMemberFiles(project);

							// List<String> memberFilesList =
							// getAllMemberFilesString(proj.getProject(), exts);

							String[] arguments = new String[memberFilesList.size() + 9];

							// 0: host
							// 1: port
							// 2: sessionID
							arguments[argNumber++] = "-h";
							arguments[argNumber++] = host;
							arguments[argNumber++] = "-p";
							arguments[argNumber++] = port;
							arguments[argNumber++] = "-k";
							arguments[argNumber++] = sessionId;

							// 3: dialect
							arguments[argNumber++] = "-" + VdmSlDebugConstants.VDMSL_VDMJ_DIALECT;

							// 4: expression eg. : new className().operation()
							String debugOperation = launch.getLaunchConfiguration().getAttribute(VdmSlDebugConstants.VDMSL_DEBUGGING_OPERATION, "");

//							String expression = "new "
//									+ launch.getLaunchConfiguration().getAttribute(
//											VdmSlDebugConstants.VDMSL_DEBUGGING_MODULE,
//											"") + "()." + debugOperation;
							String expression = debugOperation;
							arguments[argNumber++] = "-e";
							arguments[argNumber++] = expression;

							// 5-n: add files to the arguments
							for (int a = 0; a < memberFilesList.size(); a++) {
								arguments[argNumber++] = new File(memberFilesList.get(a)).toURI().toASCIIString();
							}

							vmConfig.setProgramArguments(arguments);
							ILaunch launchr = new Launch(launch.getLaunchConfiguration(), ILaunchManager.DEBUG_MODE, null);
							iconfig.adjustRunnerConfiguration(vmConfig, config,
									launch, myJavaProject);
							vmRunner.run(vmConfig, launchr, null);
							IDebugTarget[] debugTargets = launchr.getDebugTargets();
							for (int a = 0; a < debugTargets.length; a++) {
								launch.addDebugTarget(debugTargets[a]);
							}
							IProcess[] processes = launchr.getProcesses();
							for (int a = 0; a < processes.length; a++)
								launch.addProcess(processes[a]);
							return;
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, "", ""));
	}

	private static List<String> getProjectMemberFiles(IProject project)
			throws CoreException {

		List<IFile> files = ProjectUtility.getFiles(project);
		IContentTypeMatcher contentTypeMatcher = project.getContentTypeMatcher();
		List<String> memberFilesList = new ArrayList<String>();
		for (IFile file : files) {
			if (contentTypeMatcher.findContentTypeFor(file.toString()) != null)
				memberFilesList.add(ProjectUtility.getFile(project, file).getAbsolutePath());
		}
		return memberFilesList;
	}

	public static String[] getClassPath(IJavaProject myJavaProject)
			throws IOException, URISyntaxException {
		final List<String> result = new ArrayList<String>();
		// TODO ClasspathUtils.collectClasspath(new String[] {
		// GenericOvertureInstallType.EMBEDDED_VDMJ_BUNDLE_ID,
		// GenericOvertureInstallType.DBGP_FOR_VDMJ_BUNDLE_ID }, result);
		ClasspathUtils.collectClasspath(
				new String[] { "org.overture.ide.generated.vdmj", }, result);
		try {
			final String[] classPath = computeBaseClassPath(myJavaProject);
			for (int i = 0; i < classPath.length; ++i) {
				result.add(classPath[i]);
			}
		} catch (CoreException e) {
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	protected static String[] computeBaseClassPath(IJavaProject myJavaProject)
			throws CoreException {
		if (myJavaProject != null) {
			return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
		} else if (myJavaProject == null)
			return CharOperation.NO_STRINGS;
		return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
	}

	protected String constructProgramString(InterpreterConfig config)
			throws CoreException {

		return "";
	}

	protected String[] alterCommandLine(String[] cmdLine, String id) {
		ScriptConsoleServer server = ScriptConsoleServer.getInstance();
		String port = Integer.toString(server.getPort());
		String[] newCmdLine = new String[cmdLine.length + 4];

		newCmdLine[0] = cmdLine[0];
		newCmdLine[1] = DLTKCore.getDefault().getStateLocation().append("overture_proxy").toOSString();

		newCmdLine[2] = "localhost";
		newCmdLine[3] = port;
		newCmdLine[4] = id;

		for (int i = 1; i < cmdLine.length; ++i) {
			newCmdLine[i + 4] = cmdLine[i];
		}

		return newCmdLine;
	}
}
