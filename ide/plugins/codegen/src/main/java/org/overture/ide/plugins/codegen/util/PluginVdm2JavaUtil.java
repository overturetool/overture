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
import org.overture.codegen.ir.NodeInfo;
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
		return vdmProject.getDialect() == Dialect.VDM_PP;
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
	
	public static String limitStr(String str)
	{
		if(str == null)
			return "";
		
		int length = str.length();
		final int limit = 100;
		
		String subString = null;
		
		if(length <= limit)
		{
			subString = str.substring(0, length);
		}
		else
		{
			subString = str.substring(0, limit) + "...";
		}
		
		return subString.replaceAll("\\s+", " ");
	}
	
	public static String formatNodeString(NodeInfo nodeInfo, LocationAssistantCG locationAssistant)
	{
		INode node = nodeInfo.getNode();
		StringBuilder messageSb = new StringBuilder();
		messageSb.append(limitStr(node.toString()));
		messageSb.append(" (" + node.getClass().getSimpleName() + ")");
		
		ILexLocation location = locationAssistant.findLocation(node);
		if(location != null)
			messageSb.append(" " + location.toShortString());
		
		String reason = nodeInfo.getReason();
		if(reason != null)
			messageSb.append(". Reason: " + reason);
			
		return messageSb.toString();
	}
	
	public static void addMarkers(NodeInfo nodeInfo, LocationAssistantCG locationAssistant)
	{
		if(nodeInfo == null)
			return;

		INode node = nodeInfo.getNode();
		
		ILexLocation location = locationAssistant.findLocation(node);
		
		if(location == null)
			return;
		
		IFile ifile = PluginVdm2JavaUtil.convert(location.getFile());

		String reason = nodeInfo.getReason();

		String message = "Code generation support not implemented: " + node.toString();
		message += (reason != null ? ". Reason: " + reason : "");
		
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
}
