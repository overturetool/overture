package org.overture.ide.plugins.codegen.util;

import java.io.File;
import java.util.ArrayList;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.plugins.codegen.ICodeGenConstants;

public class PluginVdm2JavaUtil
{
	private static final String JAVA_FOLDER = "java";
	private static final String QUOTES_FOLDER = "quotes";
	private static final String UTILS_FOLDER = "utils";
	
	private PluginVdm2JavaUtil()
	{
	}
	
	public static IFile convert(File file)
	{
		IWorkspace workspace= ResourcesPlugin.getWorkspace();    
		IPath location= Path.fromOSString(file.getAbsolutePath()); 
		IFile ifile= workspace.getRoot().getFileForLocation(location);
		
		return ifile;
	}
		
	public static boolean isSupportedVdmDialect(IVdmProject vdmProject)
	{
		return vdmProject.getDialect() == Dialect.VDM_PP || vdmProject.getDialect() == Dialect.VDM_RT;
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

		IProject project = ((IProject) firstElement);
		IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

		return vdmProject;
	}
	
	public static List<SClassDefinition> mergeParseLists(List<IVdmSourceUnit> sources)
	{
		List<SClassDefinition> mergedParseLists = new ArrayList<SClassDefinition>();
		
		for (IVdmSourceUnit source : sources)
		{
			List<INode> parseList = source.getParseList();
			
			for (INode node : parseList)
			{
				if(node instanceof SClassDefinition)
					mergedParseLists.add(SClassDefinition.class.cast(node));
				
			}
		}
		return mergedParseLists;
	}

	public static File getOutputFolder(IVdmProject project) throws CoreException
	{
		File outputDir = getProjectDir(project);
		outputDir = new File(outputDir, JAVA_FOLDER);
		outputDir.mkdirs();
		return outputDir;
	}

	public static File getQuotesFolder(IVdmProject project) throws CoreException
	{
		return getFolder(getOutputFolder(project), QUOTES_FOLDER);
	}
	
	public static File getUtilsFolder(IVdmProject project) throws CoreException
	{
		return getFolder(getOutputFolder(project), UTILS_FOLDER);
	}
	
	public static void addMarkers(String generalMessage, Set<Violation> violations)
	{
		List<Violation> list = JavaCodeGenUtil.asSortedList(violations);
		
		for (Violation violation : list)
		{
			IFile ifile = PluginVdm2JavaUtil.convert(violation.getLocation().getFile());
			FileUtility.addMarker(ifile, generalMessage + ": " + violation.getDescripton(), violation.getLocation(), IMarker.PRIORITY_NORMAL, ICodeGenConstants.PLUGIN_ID, -1);
		}
	}
	
	public static String formatNodeString(INode node)
	{
		StringBuilder messageSb = new StringBuilder();
		messageSb.append(node.toString());
		messageSb.append(" (" + node.getClass().getSimpleName() + ")");
		
		ILexLocation location = LocationAssistantCG.findLocation(node);
		if(location != null)
			messageSb.append(" " + location.toShortString());
			
		return messageSb.toString();
	}
	
	public static void addMarkers(INode unsupportedNode)
	{
		if(unsupportedNode == null)
			return;
		
		ILexLocation location = LocationAssistantCG.findLocation(unsupportedNode);
		
		if(location == null)
			return;
		
		IFile ifile = PluginVdm2JavaUtil.convert(location.getFile());

		FileUtility.addMarker(ifile, "Code generation support not implemented: " + unsupportedNode.toString(), location, IMarker.PRIORITY_NORMAL, ICodeGenConstants.PLUGIN_ID, -1);
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
}
