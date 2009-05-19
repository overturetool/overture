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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.debug.DbgpConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;
import org.overturetool.eclipse.plugins.launching.IConfigurableRunner;
import org.overturetool.eclipse.plugins.launching.IOvertureInterpreterRunnerConfig;
import org.overturetool.eclipse.plugins.launching.OvertureLaunchConstants;

public class OvertureInterpreterRunner extends AbstractInterpreterRunner implements IConfigurableRunner {

	public static final IOvertureInterpreterRunnerConfig VDMJ_CONFIG = new IOvertureInterpreterRunnerConfig() {

		public void adjustRunnerConfiguration(VMRunnerConfiguration vconfig, InterpreterConfig iconfig, ILaunch launch, IJavaProject project) {

		}

		public String[] computeClassPath(InterpreterConfig config, ILaunch launch, IJavaProject project) throws Exception {
			return OvertureInterpreterRunner.getClassPath(project);
		}

		public String[] getProgramArguments(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return new String[0];
		}

		public String getRunnerClassName(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return "org.overturetool.vdmj.debug.DBGPReader";
		}

		public String getRunnerOperationName(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return null;
		}
	};
	
	public static final IOvertureInterpreterRunnerConfig VDMTOOLS_CONFIG = new IOvertureInterpreterRunnerConfig() {

		public void adjustRunnerConfiguration(VMRunnerConfiguration vconfig, InterpreterConfig iconfig, ILaunch launch, IJavaProject project) {

		}

		public String[] computeClassPath(InterpreterConfig config, ILaunch launch, IJavaProject project) throws Exception {
			return OvertureInterpreterRunner.getClassPath(project);
		}

		public String[] getProgramArguments(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return new String[0];
		}

		public String getRunnerClassName(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return "org.overturetool.vdmtools.dbgp.VDMToolsRunner";
		}

		public String getRunnerOperationName(InterpreterConfig config, ILaunch launch, IJavaProject project) {
			return null;
		}
	};
	
	private IOvertureInterpreterRunnerConfig config = VDMJ_CONFIG;
	private IOvertureInterpreterRunnerConfig vdmToolsConfig = VDMTOOLS_CONFIG;
	
	private static String[] exts = new String[] { "vpp", "tex", "vdm" };

	public void run(InterpreterConfig config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		IScriptProject proj = AbstractScriptLaunchConfigurationDelegate.getScriptProject(launch.getLaunchConfiguration());
		try {
			IInterpreterInstall interpreterInstall = ScriptRuntime.getInterpreterInstall(proj);
			interpreterInstall.getInstallLocation();
			if (interpreterInstall.getInterpreterInstallType().getId().equals("org.overturetool.eclipse.plugins.launching.internal.launching.VDMJInstallType")) {
				doRunImpl(config, launch, this.config);
			} else if (interpreterInstall.getInterpreterInstallType().getName().equals(OvertureDebugConstants.TOOL_VDMTOOLS)) {
				doRunImpl(config, launch, this.vdmToolsConfig);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
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
							
							//TODO get the project dialect 
							String dialect = "VDM_PP";
							if (true){								
								dialect = "VDM_PP";
							}
							else
							{
								dialect = "VDM_SL";
							}
							
							// Select interpreter:
							String toolType = OvertureDebugConstants.TOOL_VDMJ;
							IInterpreterInstall interpreterInstall;
							String VDMToolsPath = "";
							try {
								interpreterInstall = ScriptRuntime.getInterpreterInstall(proj);
								if (interpreterInstall.getInterpreterInstallType().getId().equals(org.overturetool.eclipse.plugins.editor.core.OvertureConstants.VDMTOOLS_INTERPRETER_ID)) {
									toolType = OvertureDebugConstants.TOOL_VDMTOOLS;
									VDMToolsPath = interpreterInstall.getInstallLocation().toOSString();
								} else if (interpreterInstall.getInterpreterInstallType().getId().equals(org.overturetool.eclipse.plugins.editor.core.OvertureConstants.VDMJ_INTERPRETER_ID)){
									toolType = OvertureDebugConstants.TOOL_VDMJ;
								}else{
									throw new CoreException(null);//TODO;
								}
							} catch (CoreException e) {
								e.printStackTrace();
							}
							
							
							// ******************** 
							// Create arguments:
							// 
							//VDMJ use these arguments:
							// 0: host
							// 1: port
							// 2: ideKey
							// 3: Dialect
							// 4: expression
							// 5..nFiles: files
							
							// VDMTools arguments: 
							// 0: host
							// 1: port
							// 2: ideKey
							// 3: Dialect
							// 4: expression
							// 5: path to vdmtools
							// 6..nFiles: files
							// 
							// 
							
							int argNumber = 0;
							
							// scriptFilePath.toPortableString(),
							
							ArrayList<String> memberFilesList = getAllMemberFilesString(proj.getProject(), exts);
//							String[] eclipseArguments = iconfig.getProgramArguments(config, launch, myJavaProject);
							// +2 in order to allocate space for debugClass, debugOperation, tool, dialect, debugFromConsole 
							
							String[] arguments = new String[memberFilesList.size() + 5];	
							if (toolType.equals(OvertureDebugConstants.TOOL_VDMTOOLS)){
								arguments = new String[memberFilesList.size() + 6]; 
							}
							
							// 0: host 
							// 1: port
							// 2: sessionID
							arguments[argNumber++] = host;
							arguments[argNumber++] = port;
							arguments[argNumber++] = sessionId;
							arguments[argNumber++] = dialect;
							// 4: expression eg. : new className().operation()
							String debugOperation = launch.getLaunchConfiguration().getAttribute(OvertureDebugConstants.DEBUGGING_OPERATION, "");
							
							
							String expression = 
									"new " + 
									launch.getLaunchConfiguration().getAttribute(OvertureDebugConstants.DEBUGGING_CLASS, "") + 
									"()." + debugOperation;
									
							arguments[argNumber++] = expression;
							
							if (toolType.equals(OvertureDebugConstants.TOOL_VDMTOOLS)){
								// VDMTool path
								arguments[argNumber++] =  VDMToolsPath;
							}

							// TODO maybe get the arguments if we choose a non void filter add eclipse arguments:
//							for (int a = 0; a < eclipseArguments.length; a++)
//							{
//								arguments[a + fileHostPortSessionStrs.length] = eclipseArguments[a];
//							}
							
							// add files to the arguments
							for (int a = 0; a < memberFilesList.size(); a++) 
							{
								arguments[argNumber++] = new File( memberFilesList.get(a) ).toURI().toASCIIString();
							}
							
							
							// Run new java program:
							vmConfig.setProgramArguments(arguments);
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
							VDMToolsInstallType.DBGP_FOR_VDMTOOLS_BUNDLE_ID,
							//GenericOvertureInstalltype.DBGP_FOR_VDMTOOLS_BUNDLE_ID,
							//GenericOvertureInstalltype.DBGP_FOR_ABSTRACT_BUNDLE_ID
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
		if (!myJavaProject.exists())
			return CharOperation.NO_STRINGS;
		return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
	}

//	protected String constructProgramString(InterpreterConfig config) throws CoreException {
//
//		return "";
//	}

	public OvertureInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

//	protected String[] alterCommandLine(String[] cmdLine, String id) {
//		ScriptConsoleServer server = ScriptConsoleServer.getInstance();
//		String port = Integer.toString(server.getPort());
//		String[] newCmdLine = new String[cmdLine.length + 4];
//
//		newCmdLine[0] = cmdLine[0];
//		newCmdLine[1] = DLTKCore.getDefault().getStateLocation().append("overture_proxy").toOSString();
//
//		newCmdLine[2] = "localhost";
//		newCmdLine[3] = port;
//		newCmdLine[4] = id;
//
//		for (int i = 1; i < cmdLine.length; ++i) {
//			newCmdLine[i + 4] = cmdLine[i];
//		}
//
//		return newCmdLine;
//	}

	protected String getProcessType() {
		return OvertureLaunchConstants.ID_OVERTURE_PROCESS_TYPE;
	}

	public void setRunnerConfig(IOvertureInterpreterRunnerConfig config) {
		this.config = config;
	}
}
