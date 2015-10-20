/*
 * #%~
 * Code Generator Plugin
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
package org.overture.ide.plugins.codegen.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.Preferences;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.plugins.codegen.CodeGenConsole;
import org.overture.ide.plugins.codegen.ICodeGenConstants;
import org.overture.ide.plugins.codegen.commands.Vdm2JavaCommand;

public class PluginVdm2JavaUtil
{
	public static final String CODEGEN_RUNTIME_LIB_FOLDER = "lib";
	
	public static final String CODEGEN_RUNTIME_BIN_FILE = "codegen-runtime.jar";
	public static final String CODEGEN_RUNTIME_SOURCES_FILE = "codegen-runtime-sources.jar";

	public static final String VDM2JML_RUNTIME_BIN_FILE = "vdm2jml-runtime.jar";
	public static final String VDM2JML_RUNTIME_SOURCES_FILE = "vdm2jml-runtime-sources.jar";
	
	public static final String ECLIPSE_CLASSPATH_TEMPLATE_FILE = "cg.classpath";
	public static final String ECLIPSE_PROJECT_TEMPLATE_FILE = "cg.project";

	public static final String ECLIPSE_PROJECT_ROOT_FOLDER = "java";
	public static final String ECLIPSE_CLASSPATH_FILE = ".classpath";
	public static final String ECLIPSE_PROJECT_FILE = ".project";
	public static final String ECLIPSE_RES_FILES_FOLDER = "eclipsefiles";
	public static final String ECLIPSE_PROJECT_SRC_FOLDER = "src"; 
	
	public static final String RUNTIME_CLASSPATH_ENTRY = "<classpathentry kind=\"lib\" path=\"lib/codegen-runtime.jar\"/>\n";
	public static final String VDM2JML_CLASSPATH_ENTRY = "<classpathentry kind=\"lib\" path=\"lib/vdm2jml-runtime.jar\"/>\n";


	public static final String WARNING = "[WARNING]";
	
	private PluginVdm2JavaUtil()
	{
	}

	public static IFile convert(File file)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile iFile = workspace.getRoot().getFileForLocation(location);

		return iFile;
	}

	public static boolean isSupportedVdmDialect(IVdmProject vdmProject)
	{
		return vdmProject.getDialect() == Dialect.VDM_PP || vdmProject.getDialect() == Dialect.VDM_SL;
	}

	public static IVdmProject getVdmProject(ExecutionEvent event)
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
		{
			return null;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object firstElement = structuredSelection.getFirstElement();

		if (!(firstElement instanceof IProject))
		{
			return null;
		}

		IProject project = (IProject) firstElement;
		IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

		return vdmProject;
	}
	
	public static List<INode> getNodes(List<IVdmSourceUnit> sources)
	{
		List<INode> nodes = new ArrayList<INode>();

		for (IVdmSourceUnit source : sources)
		{
			nodes.addAll(source.getParseList());
		}
		
		return nodes;
	}

	public static List<SClassDefinition> getClasses(
			List<IVdmSourceUnit> sources)
	{
		List<SClassDefinition> classes = new LinkedList<SClassDefinition>();
		
		for(INode n : getNodes(sources))
		{
			if(n instanceof SClassDefinition)
			{
				classes.add((SClassDefinition) n);
			}
		}
		
		return classes;
	}
	
	public static List<AModuleModules> getModules(List<IVdmSourceUnit> sources)
	{
		List<AModuleModules> modules = new LinkedList<AModuleModules>();

		for(INode n : getNodes(sources))
		{
			if(n instanceof AModuleModules)
			{
				modules.add((AModuleModules) n);
			}
		}
		
		return modules;
	}

	public static File getEclipseProjectFolder(IVdmProject project)
	{
		return new File(getProjectDir(project), ECLIPSE_PROJECT_ROOT_FOLDER);
	}
	

	public static File getCodeGenRuntimeLibFolder(IVdmProject project)
	{
		return getFolder(getEclipseProjectFolder(project), CODEGEN_RUNTIME_LIB_FOLDER);
	}
	
	public static File getJavaCodeOutputFolder(IVdmProject project, JavaSettings settings)
			throws CoreException
	{
		File outputDir = getEclipseProjectFolder(project);
		outputDir = getFolder(outputDir, ECLIPSE_PROJECT_SRC_FOLDER);
		
		return outputDir;
	}

	public static void addMarkers(String generalMessage,
			Set<Violation> violations)
	{
		List<Violation> list = GeneralCodeGenUtils.asSortedList(violations);

		for (Violation violation : list)
		{
			IFile ifile = convert(violation.getLocation().getFile());
			FileUtility.addMarker(ifile, generalMessage + ": "
					+ violation.getDescripton(), violation.getLocation(), IMarker.PRIORITY_NORMAL, ICodeGenConstants.PLUGIN_ID, -1);
		}
	}

	public static String limitStr(String str)
	{
		if (str == null)
		{
			return "";
		}

		int length = str.length();
		final int limit = 100;

		String subString = null;

		if (length <= limit)
		{
			subString = str.substring(0, length);
		} else
		{
			subString = str.substring(0, limit) + "...";
		}

		return subString.replaceAll("\\s+", " ");
	}

	public static String formatNodeString(VdmNodeInfo nodeInfo,
			LocationAssistantCG locationAssistant)
	{
		INode node = nodeInfo.getNode();
		StringBuilder messageSb = new StringBuilder();
		messageSb.append(limitStr(node.toString()));
		messageSb.append(" (" + node.getClass().getSimpleName() + ")");

		ILexLocation location = locationAssistant.findLocation(node);
		if (location != null)
		{
			messageSb.append(" " + location.toShortString());
		}

		String reason = nodeInfo.getReason();
		if (reason != null)
		{
			messageSb.append(". Reason: " + reason);
		}

		return messageSb.toString();
	}

	public static void addMarkers(VdmNodeInfo nodeInfo,
			LocationAssistantCG locationAssistant)
	{
		if (nodeInfo == null)
		{
			return;
		}

		INode node = nodeInfo.getNode();

		ILexLocation location = locationAssistant.findLocation(node);

		if (location == null)
		{
			return;
		}

		IFile ifile = convert(location.getFile());

		String reason = nodeInfo.getReason();

		String message = "Code generation support not implemented: "
				+ node.toString();
		message += reason != null ? ". Reason: " + reason : "";

		FileUtility.addMarker(ifile, message, location, IMarker.PRIORITY_NORMAL, ICodeGenConstants.PLUGIN_ID, -1);
	}

	private static File getProjectDir(IVdmProject project)
	{
		return project.getModelBuildPath().getOutput().getLocation().toFile();
	}

	private static File getFolder(File parent, String folder)
	{
		File resultingFolder = new File(parent, folder);
		resultingFolder.mkdirs();
		
		return resultingFolder;
	}

	public static void copyCodeGenFile(String inOutFileName, File outputFolder)
			throws IOException
	{
		copyCodeGenFile(inOutFileName, inOutFileName, outputFolder);
	}

	public static void copyCodeGenFile(String inputFileName,
			String outputFileName, File outputFolder) throws IOException
	{
		InputStream input = Vdm2JavaCommand.class.getResourceAsStream('/' + inputFileName);

		if (input == null)
		{
			throw new IOException("Could not find resource: " + inputFileName);
		}

		byte[] buffer = new byte[8 * 1024];

		try
		{
			File outputFile = new File(outputFolder, outputFileName);

			outputFile.getParentFile().mkdirs();
			if (!outputFile.exists())
			{
				outputFile.createNewFile();
			}

			OutputStream output = new FileOutputStream(outputFile);
			try
			{
				int bytesRead;
				while ((bytesRead = input.read(buffer)) != -1)
				{
					output.write(buffer, 0, bytesRead);
				}
			} finally
			{
				output.close();
			}
		} finally
		{
			input.close();
		}
	}

	public static List<String> getClassesToSkip()
	{
		Preferences preferences = InstanceScope.INSTANCE.getNode(ICodeGenConstants.PLUGIN_ID);

		String userInput = preferences.get(ICodeGenConstants.CLASSES_TO_SKIP, ICodeGenConstants.CLASSES_TO_SKIP_DEFAULT);

		return GeneralCodeGenUtils.getClassesToSkip(userInput);
	}

	public static String dialog(List<LaunchConfigData> launchConfigs)
	{
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		dialog.setTitle("Launch Configuration Selection");
		dialog.setMessage("Select a Launch configuration (* = any string, ? = any char):");
		dialog.setMultipleSelection(false);
		dialog.setElements(launchConfigs.toArray());

		int resCode = dialog.open();

		if (resCode == ElementListSelectionDialog.OK)
		{
			Object[] dialogResult = dialog.getResult();

			if (dialogResult.length == 1
					&& dialogResult[0] instanceof LaunchConfigData)
			{
				LaunchConfigData chosenConfig = (LaunchConfigData) dialogResult[0];
				
				return chosenConfig.getExp();
			}
		}
		
		return null;
	}

	public static List<LaunchConfigData> getProjectLaunchConfigs(final IProject project)
	{
		List<LaunchConfigData> matches = new LinkedList<>();

		try
		{
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();

			for (ILaunchConfiguration launchConfig : configs)
			{
				String launchConfigProjectName = launchConfig.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT, "");

				if (launchConfigProjectName != null && !launchConfigProjectName.equals("")
						&& launchConfigProjectName.equals(project.getName()))
				{
					String exp = launchConfig.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION, "");
					matches.add(new LaunchConfigData(launchConfig.getName(), exp));
				}
			}
		} catch (CoreException e)
		{

			CodeGenConsole.GetInstance().printErrorln("Problem looking up launch configurations for project "
					+ project.getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return matches;
	}
}
