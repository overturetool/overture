package org.overture.ide.plugins.codegen.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;

public class PluginVdm2JavaUtil
{	
	
	private PluginVdm2JavaUtil()
	{
		
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
		outputDir = new File(outputDir, "java");
		outputDir.mkdirs();
		return outputDir;
	}

	public static File getQuotesFolder(IVdmProject project) throws CoreException
	{
		return getFolder(getOutputFolder(project), "quotes");
	}
	
	public static File getUtilsFolder(IVdmProject project) throws CoreException
	{
		return getFolder(getOutputFolder(project), "utils");
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
