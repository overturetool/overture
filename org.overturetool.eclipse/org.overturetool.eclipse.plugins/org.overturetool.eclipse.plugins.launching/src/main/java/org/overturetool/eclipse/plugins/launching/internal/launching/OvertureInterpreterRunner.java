/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.overturetool.eclipse.plugins.launching.internal.launching;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;
import org.overturetool.eclipse.plugins.launching.IOvertureInterpreterRunnerConfig;
import org.overturetool.eclipse.plugins.launching.OvertureLaunchConstants;

public class OvertureInterpreterRunner extends AbstractInterpreterRunner implements org.overturetool.eclipse.plugins.launching.IConfigurableRunner {

	public static final IOvertureInterpreterRunnerConfig DEFAULT_CONFIG = new IOvertureInterpreterRunnerConfig() {

		public void adjustRunnerConfiguration(VMRunnerConfiguration vconfig, InterpreterConfig iconfig, ILaunch launch, IJavaProject project) {

		}

		public String[] computeClassPath(InterpreterConfig config, ILaunch launch, IJavaProject project) throws Exception {
			return OvertureInterpreterRunner.getClassPath(project);
		}

		public String[] getProgramArguments(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return new String[0];
		}

		public String getRunnerClassName(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return "OvertureRunner";
		}

		public String getRunnerOperationName(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			// TODO Auto-generated method stub
			return null;
		}

	};
	private IOvertureInterpreterRunnerConfig config = DEFAULT_CONFIG;

	public void run(InterpreterConfig config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		doRunImpl(config, launch, this.config);
	}

	/**
	 * This method returns a list of files under the given directory or its
	 * subdirectories. The directories themselves are not returned.
	 * 
	 * @param dir
	 *            a directory
	 * @return list of IResource objects representing the files under the given
	 *         directory and its subdirectories
	 */
	private static ArrayList<String> getAllMemberFilesString(IContainer dir, String[] exts) {
		ArrayList<String> list = new ArrayList<String>();
		IResource[] arr = null;
		try {
			arr = dir.members();
		} catch (CoreException e) {
		}

		for (int i = 0; arr != null && i < arr.length; i++) {
			if (arr[i].getType() == IResource.FOLDER) {
				list.addAll(getAllMemberFilesString((IFolder) arr[i], exts));
			} else {

				for (int j = 0; j < exts.length; j++) {
					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension())) {
						list.add(arr[i].getLocation().toOSString());
						break;
					}
				}
			}
		}
		return list;
	}

	public static void doRunImpl(InterpreterConfig config, ILaunch launch, IOvertureInterpreterRunnerConfig iconfig) throws CoreException {

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
		IVMInstall vmInstall = myJavaProject.exists() ? JavaRuntime.getVMInstall(myJavaProject) : JavaRuntime.getDefaultVMInstall();
		if (vmInstall != null) {
			IVMRunner vmRunner = vmInstall.getVMRunner(ILaunchManager.DEBUG_MODE);
			if (vmRunner != null) {
				{

					try {

						try {
							String[] newClassPath = getClassPath(myJavaProject);

							VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(iconfig.getRunnerClassName(config, launch, myJavaProject), newClassPath);
							IPath scriptFilePath = config.getScriptFilePath();
							if (scriptFilePath == null) {
								throw new CoreException(new Status(IStatus.ERROR, OvertureDebugConstants.PLUGIN_ID, "Script File name is not specified..."));
							}
							String[] exts = new String[] { "vpp", "tex", "vdm" };

							ArrayList<String> memberFilesList = getAllMemberFilesString(proj.getProject(), exts);

							String[] strings = new String[] { scriptFilePath.toPortableString(), host, "" + port, sessionId };
							String[] newStrings = iconfig.getProgramArguments(config, launch, myJavaProject);
							// +2 in order to allocate space for debugClass, debugOperation, tool, dialect, debugFromConsole 
							String[] rs = new String[strings.length + newStrings.length + memberFilesList.size() + 5];	
							
						
							// add file host port sessionID
							for (int a = 0; a < strings.length; a++)
								rs[a] = strings[a];
							// add program arguments
							for (int a = 0; a < newStrings.length; a++)
								rs[a + strings.length] = newStrings[a];
							
							// add files
							for (int a = 0; a < memberFilesList.size(); a++) {
								rs[a + strings.length + newStrings.length] = "-file " + memberFilesList.get(a);
							}
							
							//TODO move to constant file
							rs[rs.length - 5] = "-" + OvertureDebugConstants.DEBUGGING_CLASS + " " + launch.getLaunchConfiguration().getAttribute(OvertureDebugConstants.DEBUGGING_CLASS, "");
							rs[rs.length - 4] = "-" + OvertureDebugConstants.DEBUGGING_OPERATION + " " + launch.getLaunchConfiguration().getAttribute(OvertureDebugConstants.DEBUGGING_OPERATION, "");
							rs[rs.length - 3] = "-" + OvertureDebugConstants.DEBUGGING_FROM_CONSOLE + " " + launch.getLaunchConfiguration().getAttribute(OvertureDebugConstants.DEBUGGING_FROM_CONSOLE, false);
							rs[rs.length - 2] = "-" + OvertureDebugConstants.DEBUGGING_DIALECT + ""; //TODO
							rs[rs.length - 1] = "-" + OvertureDebugConstants.DEBUGGING_TOOL + " " + ""; //TODO
							

							vmConfig.setProgramArguments(rs);
							ILaunch launchr = new Launch(launch.getLaunchConfiguration(), ILaunchManager.DEBUG_MODE, null);
							iconfig.adjustRunnerConfiguration(vmConfig, config, launch, myJavaProject);
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

	public static String[] getClassPath(IJavaProject myJavaProject) throws IOException, URISyntaxException {
		final List<String> result = new ArrayList<String>();
		// TODO ClasspathUtils.collectClasspath(new String[] { GenericOvertureInstallType.EMBEDDED_VDMJ_BUNDLE_ID, GenericOvertureInstallType.DBGP_FOR_VDMJ_BUNDLE_ID }, result);
		ClasspathUtils.collectClasspath
				(
					new String[]
					      {
							GenericOvertureInstalltype.DBGP_FOR_VDMJ_BUNDLE_ID,
							GenericOvertureInstalltype.DBGP_FOR_VDMTOOLS_BUNDLE_ID,
							GenericOvertureInstalltype.DBGP_FOR_ABSTRACT_BUNDLE_ID
					      },
					result
				);
		try {
			final String[] classPath = computeBaseClassPath(myJavaProject);
			for (int i = 0; i < classPath.length; ++i) {
				result.add(classPath[i]);
			}
		} catch (CoreException e) {
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	protected static String[] computeBaseClassPath(IJavaProject myJavaProject) throws CoreException {
		if (myJavaProject != null){
			return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
		}
		if (!myJavaProject.exists())
			return CharOperation.NO_STRINGS;
		return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
	}

	protected String constructProgramString(InterpreterConfig config) throws CoreException {

		return "";
	}

	public OvertureInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

	protected String[] alterCommandLine(String[] cmdLine, String id) {
		ScriptConsoleServer server = ScriptConsoleServer.getInstance();
		String port = Integer.toString(server.getPort());
		String[] newCmdLine = new String[cmdLine.length + 4];

		newCmdLine[0] = cmdLine[0];
		newCmdLine[1] = DLTKCore.getDefault().getStateLocation().append("tcl_proxy").toOSString();

		newCmdLine[2] = "localhost";
		newCmdLine[3] = port;
		newCmdLine[4] = id;

		for (int i = 1; i < cmdLine.length; ++i) {
			newCmdLine[i + 4] = cmdLine[i];
		}

		return newCmdLine;
	}

	protected String getProcessType() {
		return OvertureLaunchConstants.ID_OVERTURE_PROCESS_TYPE;
	}

	public void setRunnerConfig(IOvertureInterpreterRunnerConfig config) {
		this.config = config;
	}
}
