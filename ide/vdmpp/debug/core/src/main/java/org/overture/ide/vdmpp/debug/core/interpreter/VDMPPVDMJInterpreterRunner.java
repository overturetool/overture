package org.overture.ide.vdmpp.debug.core.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
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
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.IAstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.debug.launching.ClasspathUtils;
import org.overture.ide.debug.launching.IOvertureInterpreterRunnerConfig;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmpp.debug.core.VDMPPDebugConstants;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;

//TODO test if this is a specific runner or a common runner for both VDMTools and VDMJ
public class VDMPPVDMJInterpreterRunner extends AbstractInterpreterRunner {

	// TODO get source files in an other way or move to util
	//private static String[] exts = new String[] { "vpp", "tex", "vdm", "vdmpp", "vdmsl", "vdmrt" };
//	/**
//	 * This method returns a list of files under the given directory or its
//	 * subdirectories. The directories themselves are not returned.
//	 * 
//	 * @param dir
//	 *            a directory
//	 * @return list of IResource objects representing the files under the given
//	 *         directory and its subdirectories
//	 */
//	private static ArrayList<String> getAllMemberFilesString(IContainer dir, String[] exts) {
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
	
	public VDMPPVDMJInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}
	
	@SuppressWarnings("unchecked")
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
							String[] newClassPath = ClasspathUtils.getClassPath(myJavaProject);

							VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(iconfig.getRunnerClassName(config, launch, myJavaProject), newClassPath);
							vmConfig.setWorkingDirectory(proj.getProject().getLocation().toOSString());

							
							
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
							
							int argNumber = 0;
							
							List<String> memberFilesList = new Vector<String>();//getAllMemberFilesString(proj.getProject(), exts);
						List<IFile> files = 	ProjectUtility.getFiles(proj.getProject(), VdmPpCorePluginConstants.CONTENT_TYPE);
							
						for (IFile iFile : files) {
							memberFilesList.add(ProjectUtility.getFile(proj.getProject(), iFile).getAbsolutePath());
						}
						
						
							String[] arguments = new String[memberFilesList.size() + 11]; 
							
							// 0: host 
							// 1: port
							// 2: sessionID
							arguments[argNumber++] = "-h";
							arguments[argNumber++] = host;
							arguments[argNumber++] = "-p";
							arguments[argNumber++] = port;
							arguments[argNumber++] = "-k"; // key
							arguments[argNumber++] = sessionId; 
							
							// 3: dialect
							arguments[argNumber++] = "-" + VDMPPDebugConstants.VDMPP_VDMJ_DIALECT;
							// charset
							arguments[argNumber++] = "-c";
							Charset.defaultCharset().name(); // 
							File in =  new File(memberFilesList.get(0));
							InputStreamReader r = new InputStreamReader(new FileInputStream(in));
							System.out.println(r.getEncoding());
							arguments[argNumber++] = r.getEncoding();
							
							
							
							// 4: expression eg. : new className().operation()
							String debugOperation = launch.getLaunchConfiguration().getAttribute(VDMPPDebugConstants.VDMPP_DEBUGGING_OPERATION, "");
							
							String expression = 
								"new " + 
								launch.getLaunchConfiguration().getAttribute(VDMPPDebugConstants.VDMPP_DEBUGGING_CLASS, "") + 
								"()." + debugOperation;
							arguments[argNumber++] = "-e";		
							arguments[argNumber++] = expression;
							
							
							// 5-n: add files to the arguments
							for (int a = 0; a < memberFilesList.size(); a++) 
							{
								arguments[argNumber++] = new File( memberFilesList.get(a) ).toURI().toASCIIString();
							}
							
//							System.out.println("arguments");
//							for (String argument : arguments) {
//								System.out.println(argument);
//							}
							
							// test
							// TODO change AstManager  
							//System.out.println("running");
							try {
								IAstManager astManager = AstManager.instance();
								
//								BuilderPp builder = new BuilderPp();
//								final List ast = (List) astManager.getAstList(proj.getProject(), VdmPpProjectNature.VDM_PP_NATURE);
//								builder.buileModelElements(proj.getProject(),ast);
								proj.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
								
								RootNode rootNode = astManager.getRootNode(proj.getProject(), VdmPpProjectNature.VDM_PP_NATURE);
								if (rootNode.isChecked()){
									ClassList classList = new ClassList();
																		
									List classDefinitions = rootNode.getRootElementList();
									for (Iterator iterator = classDefinitions.iterator(); iterator.hasNext();) {
										Object object = iterator.next();
										if (object instanceof ClassDefinition)
										{
											classList.add((ClassDefinition)object);
										}
									}
									
									if (classList.size() > 0)
									{
										//ClassInterpreter classInterpreter = new ClassInterpreter(classList);
										//new DBGPReader(host, Integer.parseInt(port), sessionId, classInterpreter, expression).run(true);
									}
								}
								else
								{
									// TODO if project isn't type checked..make some kind of 
									// message dialog telling it to the user
									throw new CoreException(new Status(IStatus.ERROR, VDMPPDebugConstants.VDMPP_DEBUG_PLUGIN_ID, "The project is not type checked"));
								}
							} catch (Exception e) {
								return;
								//throw new CoreException(new Status(IStatus.ERROR, VDMPPDebugConstants.VDMPP_DEBUG_PLUGIN_ID, e.getMessage()));
							}
							//System.out.println("stopped");
							return;
							

//							// Run new java program:
//							vmConfig.setProgramArguments(arguments);
//							ILaunch launchr = new Launch(launch.getLaunchConfiguration(), ILaunchManager.DEBUG_MODE, null);
//							iconfig.adjustRunnerConfiguration(vmConfig, config, launch, myJavaProject);
//							vmRunner.run(vmConfig, launchr, null);
//							IDebugTarget[] debugTargets = launchr.getDebugTargets();
//							for (int a = 0; a < debugTargets.length; a++) {
//								launch.addDebugTarget(debugTargets[a]);
//							}
//							IProcess[] processes = launchr.getProcesses();
//							for (int a = 0; a < processes.length; a++)
//								launch.addProcess(processes[a]);
//							return;
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
	
	protected String[] alterCommandLine(String[] cmdLine, String id) {
		ScriptConsoleServer server = ScriptConsoleServer.getInstance();
		String port = Integer.toString(server.getPort());
		String[] newCmdLine = new String[cmdLine.length + 4];

		newCmdLine[0] = cmdLine[0];
		newCmdLine[1] = DLTKCore.getDefault().getStateLocation().append(
				"overture_proxy").toOSString();

		newCmdLine[2] = "localhost";
		newCmdLine[3] = port;
		newCmdLine[4] = id;

		for (int i = 1; i < cmdLine.length; ++i) {
			newCmdLine[i + 4] = cmdLine[i];
		}

		return newCmdLine;
	}


}
