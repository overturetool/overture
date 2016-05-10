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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.part.ViewPart;
import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ct.utils.TraceHelperNotInitializedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.ITreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.NotYetReadyTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.ProjectTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TreeParent;
import org.xml.sax.SAXException;

public class ViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider
{
	private TreeParent invisibleRoot;
	ViewPart viewer;
	Map<INode, List<TraceTreeNode>> containerNodes = new HashMap<INode, List<TraceTreeNode>>();

	public ViewContentProvider(ViewPart p)
	{
		// this.traceHelpers = trs;
		viewer = p;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput)
	{

	}

	public void resetCache(IVdmProject project)
	{
		Set<INode> containers = TraceAstUtility.getTraceContainers(project);
		for (INode iNode : containers)
		{
			containerNodes.remove(iNode);
		}
	}

	public void dispose()
	{
	}

	public void addChild(ProjectTreeNode project)
	{
		invisibleRoot.addChild(project);
	}

	public Object[] getElements(Object parent)
	{
		if (invisibleRoot == null)
		{
			initialize();
		}
		return getChildren(invisibleRoot);
	}

	public Object getParent(Object child)
	{
		if (child instanceof ITreeNode)
		{
			return ((ITreeNode) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent)
	{
		if (parent instanceof ProjectTreeNode)
		{
			Set<INode> containers = TraceAstUtility.getTraceContainers(((ProjectTreeNode) parent).project);
			return containers.toArray();
		}
		if (parent instanceof ITreeNode)
		{
			return ((ITreeNode) parent).getChildren().toArray();
		}
		if (parent instanceof SClassDefinition
				|| parent instanceof AModuleModules)
		{
			List<TraceTreeNode> children = new ArrayList<TraceTreeNode>();

			List<ANamedTraceDefinition> traceDefs = TraceAstUtility.getTraceDefinitions((INode) parent);
			if (containerNodes.containsKey(parent)
					&& containerNodes.get(parent).size() == traceDefs.size())
			{
				return containerNodes.get(parent).toArray();
			} else
			{

				for (ANamedTraceDefinition def : traceDefs)
				{
					// ITracesHelper tr = traceHelpers.get(TraceAstUtility.getProject(def));
					try
					{
						children.add(new TraceTreeNode(def));
					} catch (SAXException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TraceHelperNotInitializedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				containerNodes.put((INode) parent, children);
			}

			return children.toArray();
		}

		return new Object[0];
	}

	public boolean hasChildren(Object parent)
	{
		if (parent instanceof ProjectTreeNode)
		{
			HasTraceAnalysis analysis = new HasTraceAnalysis();
			for (INode node : ((ProjectTreeNode) parent).project.getModel().getRootElementList())
			{
				if (analysis.hasTrace(node))
				{
					return true;
				}

			}
			return false;

		} else if (parent instanceof NotYetReadyTreeNode)
		{
			return false;
		}
		if (parent instanceof ITreeNode)
		{
			return ((ITreeNode) parent).hasChildren();
		} else if (parent instanceof SClassDefinition
				|| parent instanceof AModuleModules)
		{
			return true;
		}
		return false;
	}

	static class HasTraceAnalysis extends AnalysisAdaptor
	{
		boolean hasTrace = false;

		public boolean hasTrace(INode node)
		{
			hasTrace = false;

			try
			{
				node.apply(this);
			} catch (Throwable e)
			{
			}

			return hasTrace;
		}

		@Override
		public void defaultSClassDefinition(SClassDefinition node)
				throws AnalysisException
		{
			for (PDefinition def : node.getDefinitions())
			{
				if (def instanceof ANamedTraceDefinition)
				{
					hasTrace = true;
					throw new AnalysisException("stop search");
				}
			}
		}

		@Override
		public void caseAModuleModules(AModuleModules node)
				throws AnalysisException
		{
			for (PDefinition def : node.getDefs())
			{
				if (def instanceof ANamedTraceDefinition)
				{
					hasTrace = true;
					throw new AnalysisException("stop search");
				}
			}
		}

	}

	static class TraceContainerSearch extends AnalysisAdaptor
	{
		Set<INode> containers = new HashSet<INode>();

		public Set<INode> getTraceContainers(INode node)
		{
			containers.clear();

			try
			{
				node.apply(this);
			} catch (Throwable e)
			{

			}

			return containers;
		}

		@Override
		public void defaultSClassDefinition(SClassDefinition node)
				throws AnalysisException
		{
			for (PDefinition def : node.getDefinitions())
			{
				if (def instanceof ANamedTraceDefinition)
				{
					containers.add(node);
					throw new AnalysisException("stop search");
				}
			}
		}

		@Override
		public void caseAModuleModules(AModuleModules node)
				throws AnalysisException
		{
			for (PDefinition def : node.getDefs())
			{
				if (def instanceof ANamedTraceDefinition)
				{
					containers.add(node);
					throw new AnalysisException("stop search");
				}
			}
		}

	}

	static class TraceSearch extends AnalysisAdaptor
	{
		List<ANamedTraceDefinition> containers = new ArrayList<ANamedTraceDefinition>();

		public List<ANamedTraceDefinition> getTraces(INode node)
		{
			containers.clear();

			try
			{
				node.apply(this);
			} catch (Throwable e)
			{

			}

			return containers;
		}

		@Override
		public void defaultSClassDefinition(SClassDefinition node)
		{
			for (PDefinition def : node.getDefinitions())
			{
				if (def instanceof ANamedTraceDefinition)
				{
					containers.add((ANamedTraceDefinition) def);
				}
			}
		}

		@Override
		public void caseAModuleModules(AModuleModules node)
		{
			for (PDefinition def : node.getDefs())
			{
				if (def instanceof ANamedTraceDefinition)
				{
					containers.add((ANamedTraceDefinition) def);
				}
			}
		}

	}

	/*
	 * We will set up a dummy model to initialize tree heararchy. In a real code, you will connect to a real model and
	 * expose its hierarchy.
	 */
	private void initialize()
	{
		invisibleRoot = new TreeParent("");

		ProjectTreeNode projectTreeNode;

		for (IVdmProject project : TraceAstUtility.getProjects())
		{
			HasTraceAnalysis analysis = new HasTraceAnalysis();
			for (INode node : project.getModel().getRootElementList())
			{
				if (analysis.hasTrace(node))
				{
					projectTreeNode = new ProjectTreeNode(project);
					invisibleRoot.addChild(projectTreeNode);
					break;
				}
			}
		}

	}
}
