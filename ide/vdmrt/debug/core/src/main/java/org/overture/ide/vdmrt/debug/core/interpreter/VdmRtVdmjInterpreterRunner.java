//package org.overture.ide.vdmrt.debug.core.interpreter;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.Status;
//import org.eclipse.debug.core.ILaunch;
//import org.eclipse.debug.core.ILaunchManager;
//import org.eclipse.dltk.core.IScriptProject;
//import org.eclipse.dltk.launching.AbstractInterpreterRunner;
//import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
//import org.eclipse.dltk.launching.IInterpreterInstall;
//import org.eclipse.dltk.launching.InterpreterConfig;
//import org.eclipse.dltk.launching.debug.DbgpConstants;
//import org.eclipse.jdt.core.IJavaProject;
//import org.eclipse.jdt.core.JavaCore;
//import org.eclipse.jdt.launching.IVMInstall;
//import org.eclipse.jdt.launching.IVMRunner;
//import org.eclipse.jdt.launching.JavaRuntime;
//import org.eclipse.jdt.launching.VMRunnerConfiguration;
//import org.overture.ide.ast.AstManager;
//import org.overture.ide.ast.IAstManager;
//import org.overture.ide.ast.RootNode;
//import org.overture.ide.debug.launching.ClasspathUtils;
//import org.overture.ide.debug.launching.IOvertureInterpreterRunnerConfig;
//import org.overture.ide.utility.ProjectUtility;
//import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;
//import org.overture.ide.vdmrt.core.VdmRtProjectNature;
//import org.overture.ide.vdmrt.debug.core.VdmRtDebugConstants;
//import org.overturetool.vdmj.debug.DBGPReader;
//import org.overturetool.vdmj.definitions.ClassDefinition;
//import org.overturetool.vdmj.definitions.ClassList;
//import org.overturetool.vdmj.runtime.ClassInterpreter;
//import org.overturetool.vdmj.runtime.Interpreter;
//
////TODO test if this is a specific runner or a common runner for both VDMTools and VDMJ
//public class VdmRtVdmjInterpreterRunner extends AbstractInterpreterRunner {
//	
//	public VdmRtVdmjInterpreterRunner(IInterpreterInstall install) {
//		super(install);
//	}
//	
//	public static void doRunImpl(InterpreterConfig config, ILaunch launch, IOvertureInterpreterRunnerConfig iconfig) throws CoreException {
//		String host = (String) config.getProperty(DbgpConstants.HOST_PROP);
//		if (host == null) {
//			host = "";
//		}
//
//		String port = (String) config.getProperty(DbgpConstants.PORT_PROP);
//		if (port == null) {
//			port = "";
//		}
//
//		String sessionId = (String) config.getProperty(DbgpConstants.SESSION_ID_PROP);
//
//		if (sessionId == null) {
//			sessionId = "";
//		}
//
//		IScriptProject proj = AbstractScriptLaunchConfigurationDelegate.getScriptProject(launch.getLaunchConfiguration());
//		IJavaProject myJavaProject = JavaCore.create(proj.getProject());
//		IVMInstall vmInstall = myJavaProject.exists() ? JavaRuntime.getVMInstall(myJavaProject) : JavaRuntime.getDefaultVMInstall();
//		if (vmInstall != null) {
//			IVMRunner vmRunner = vmInstall.getVMRunner(ILaunchManager.DEBUG_MODE);
//			if (vmRunner != null) {
//				{
//
//					try {
//
//						try {
//							String[] newClassPath = ClasspathUtils.getClassPath(myJavaProject);
//
//							VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(iconfig.getRunnerClassName(config, launch, myJavaProject), newClassPath);
//							vmConfig.setWorkingDirectory(proj.getProject().getLocation().toOSString());
//
//							
//							
//							// ******************** 
//							// Create arguments:
//							// 
//							//VDMJ use these arguments:
//							// 0: host
//							// 1: port
//							// 2: ideKey
//							// 3: Dialect
//							// 4: expression
//							// 5..nFiles: files
//							
//							int argNumber = 0;
//							IProject project = proj.getProject();
//							
//							List<IFile> memberFilesList = ProjectUtility.getFiles(project, VdmRtCorePluginConstants.CONTENT_TYPE);
//							
//							String[] arguments = new String[memberFilesList.size() + 13];
//
//							// 0: host
//							// 1: port
//							// 2: sessionID
//							arguments[argNumber++] = "-h";
//							arguments[argNumber++] = host;
//							arguments[argNumber++] = "-p";
//							arguments[argNumber++] = port;
//							arguments[argNumber++] = "-k";
//							arguments[argNumber++] = sessionId;
//							
//							// log
//							DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
//							Date date = new Date();
//							File logDir = new File(  config.getWorkingDirectoryPath().toOSString()  + File.separatorChar + "logs" + File.separatorChar + launch.getLaunchConfiguration().getName() );
//							logDir.mkdirs();
//							String logFilename = dateFormat.format(date) + ".logrt";
//							System.out.println(logFilename);
//							File f = new File(logDir , logFilename);
//						    if (!f.exists()){
//						      f.getParentFile().mkdirs();
//						      f.createNewFile();
//						    }
//						    
//							arguments[argNumber++] = "-log";
//							arguments[argNumber++] = logDir.toURI().toASCIIString() + logFilename;  
//							
//							
//							project.refreshLocal(IProject.DEPTH_INFINITE, null);
//							
//							// 3: dialect
//							arguments[argNumber++] = "-" + VdmRtDebugConstants.VDMRT_VDMJ_DIALECT;
//
//							// charset
//							arguments[argNumber++] = "-c";
//							arguments[argNumber++] = memberFilesList.get(0).getCharset();
//							
//							// 4: expression eg. : new className().operation()
//							String debugOperation = launch.getLaunchConfiguration().getAttribute(VdmRtDebugConstants.VDMRT_DEBUGGING_OPERATION, "");
//							
//							String expression = "new "
//									+ launch.getLaunchConfiguration().getAttribute(
//											VdmRtDebugConstants.VDMRT_DEBUGGING_CLASS,
//											"") + "()." + debugOperation;
//							//String expression = debugOperation;
//							arguments[argNumber++] = "-e";
//							arguments[argNumber++] = expression;
//
//							// 5-n: add files to the arguments
//							for (int a = 0; a < memberFilesList.size(); a++) {
//								arguments[argNumber++] = memberFilesList.get(a).getLocationURI().toASCIIString();
//							}
//							
////							System.out.println("arguments");
////							for (String argument : arguments) {
////								System.out.println(argument);
////							}
//							
//							// test
//							// TODO change AstManager  
//							//System.out.println("running");
//							DBGPReader.main(arguments);
//							
//							
////							try {
////								IAstManager astManager = AstManager.instance();
////								RootNode rootNode = astManager.getRootNode(proj.getProject(), VdmRtProjectNature.VDM_RT_NATURE);
////								if (rootNode.isChecked()){							
////									ClassList classList =  new ClassList();
////									List astList = rootNode.getRootElementList();
////									for (Object obj : astList) {
////										if (obj instanceof ClassDefinition){
////											ClassDefinition classDef = (ClassDefinition) obj;
////											classList.add(classDef);
////										}
////									}
////									if (classList.size() > 0){										
////										//ClassInterpreter classInterpreter = new ClassInterpreter(classList);
////										//classInterpreter.execute("", dbgp)
//////										DBGPReader dbgp = new DBGPReader(
//////												argumentHashMap.get("h"), // host 
//////												Integer.parseInt(argumentHashMap.get("p")), // port 
//////												argumentHashMap.get("k"), // sessionKey 
//////												(Interpreter)classInterpreter, // interpreter 
//////												expression);
////									}
////									else
////									{
////										throw new CoreException(new Status(IStatus.ERROR, "", "The project is empty"));
////									}
////								}
////								else
////								{
////									throw new CoreException(new Status(IStatus.ERROR, "", "The project is not type checked"));
////								}
////							} catch (Exception e) {
////								System.out.println(e.getMessage());
////							}
//							//System.out.println("stopped");
//							return;
//							
//
////							// Run new java program:
////							vmConfig.setProgramArguments(arguments);
////							ILaunch launchr = new Launch(launch.getLaunchConfiguration(), ILaunchManager.DEBUG_MODE, null);
////							iconfig.adjustRunnerConfiguration(vmConfig, config, launch, myJavaProject);
////							vmRunner.run(vmConfig, launchr, null);
////							IDebugTarget[] debugTargets = launchr.getDebugTargets();
////							for (int a = 0; a < debugTargets.length; a++) {
////								launch.addDebugTarget(debugTargets[a]);
////							}
////							IProcess[] processes = launchr.getProcesses();
////							for (int a = 0; a < processes.length; a++)
////								launch.addProcess(processes[a]);
////							return;
//						} catch (URISyntaxException e) {
//							e.printStackTrace();
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		throw new CoreException(new Status(IStatus.ERROR, "", ""));
//	}
//
//
//}
