/*
 * #%~
 * Developer Utilities for Overture
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
package org.overture.ide.plugins.developerutils.dot;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.preview.DotGraphVisitor;
import org.overture.ast.preview.GraphViz;
import org.overture.ast.preview.GraphViz.GraphVizException;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.developerutils.IOvertureDeveloperUtils;
import org.overture.ide.plugins.developerutils.OvertureDeveliperUtilsPlugin;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

public class DotHandler extends AbstractHandler implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{

			final IContainer c = (IContainer) ((IStructuredSelection) selection).getFirstElement();

			final IProject project = c.getProject();
			IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);
			if (p != null)
			{
				final IVdmModel model = p.getModel();
				if (!model.isParseCorrect())
				{
					return null;
					//return new Status(Status.ERROR, IPoviewerConstants.PLUGIN_ID, "Project contains parse errors");
				}

				if (model == null || !model.isTypeCorrect())
				{
					VdmTypeCheckerUi.typeCheck(HandlerUtil.getActiveShell(event), p);
				}
				for (INode node : p.getModel().getRootElementList())
				{
					String name = "out";
					if (node instanceof SClassDefinition)
					{
						name = ((SClassDefinition) node).getName().getName();
					} else if (node instanceof AModuleModules)
					{
						name = ((AModuleModules) node).getName().getName();
					}
					name += ".svg";
					File generated = p.getModelBuildPath().getOutput().getLocation().toFile();
					generated.mkdirs();
					try
					{
						File dotpath = getDotPath();
						if(!dotpath.exists())
						{
							MessageDialog.openError(HandlerUtil.getActiveShell(event), "Dot path not valid", "Could not find the following path: \""+dotpath.toString()+"\". Check that the path is set under Preferences->VDM->Dot DOT Path.");
							return null;
						}
						makeImage(dotpath, node, "svg", new File(generated, name));
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			try
			{
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e)
			{
			}
		}
		return null;
	}

	private File getDotPath()
	{
		IPreferenceStore store = OvertureDeveliperUtilsPlugin.getDefault().getPreferenceStore();
		return new File(store.getString(IOvertureDeveloperUtils.DOT_PATH_PREFERENCE));
	}

	public static void makeImage(File dotPath, INode node, String type,
			File output) throws GraphVizException
	{
		DotGraphVisitor visitor = new DotGraphVisitor();
		try
		{
			node.apply(visitor, null);
		} catch (Throwable e)
		{
			// Ignore
		}
		GraphViz gv = new GraphViz(dotPath);
		gv.writeGraphToFile(gv.getGraph(visitor.getResultString(), type), output);
	}
}
