/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.combinatorialtesting.views.ViewContentProvider.TraceContainerSearch;
import org.overture.ide.plugins.combinatorialtesting.views.ViewContentProvider.TraceSearch;

public class TraceAstUtility
{
	static Map<INode, IVdmProject> cache = new HashMap<INode, IVdmProject>();

	static public Set<IVdmProject> getProjects()
	{
		Set<IVdmProject> projects = new HashSet<IVdmProject>();
		IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iprojects = iworkspaceRoot.getProjects();
		for (IProject iProject : iprojects)
		{
			IVdmProject vdmProject = (IVdmProject) iProject.getAdapter(IVdmProject.class);

			if (vdmProject != null)
			{
				projects.add(vdmProject);
			}
		}

		return projects;
	}

	static public IVdmProject getProject(INode node)
	{
		if (cache.containsKey(node))
		{
			return cache.get(node);
		}
		for (IVdmProject project : getProjects())
		{
			for (INode projectNode : project.getModel().getRootElementList())
			{
				if (node instanceof SClassDefinition
						|| node instanceof AModuleModules)
				{
					if (node == projectNode)
					{
						cache.put(node, project);
						return project;
					}
				} else if (node instanceof ANamedTraceDefinition)
				{
					LinkedList<PDefinition> definitions = null;
					if (projectNode instanceof SClassDefinition)
					{
						definitions = ((SClassDefinition) projectNode).getDefinitions();
					} else if (projectNode instanceof AModuleModules)
					{
						definitions = ((AModuleModules) projectNode).getDefs();
					}

					for (PDefinition pDefinition : definitions)
					{
						if (node == pDefinition)
						{
							cache.put(node, project);
							return project;
						}
					}
				} else
				{
					// not supported
				}
			}
		}
		return null;
	}

	public static Set<INode> getTraceContainers(IVdmProject project)
	{
		TraceContainerSearch analysis = new TraceContainerSearch();
		Set<INode> containers = new HashSet<INode>();
		for (INode node : project.getModel().getRootElementList())
		{
			containers.addAll(analysis.getTraceContainers(node));
		}

		for (INode iNode : containers)
		{
			cache.put(iNode, project);
		}
		return containers;

	}

	public static String getContainerName(INode node)
	{
		if (node instanceof SClassDefinition)
		{
			return ((SClassDefinition) node).getName().getName();
		} else if (node instanceof AModuleModules)
		{
			return ((AModuleModules) node).getName().getName();
		}
		return null;
	}

	public static List<ANamedTraceDefinition> getTraceDefinitions(INode node)
	{
		return new TraceSearch().getTraces(node);
	}

	public static INode getTraceDefinitionContainer(ANamedTraceDefinition node)
	{
		INode parent = null;
		parent = node.getAncestor(SClassDefinition.class);
		if (parent != null)
		{
			return parent;
		}
		parent = node.getAncestor(AModuleModules.class);
		return parent;
	}
}
