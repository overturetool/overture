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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.preview.Main;
import org.overture.ide.core.resources.IVdmProject;

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
				for (INode node : p.getModel().getRootElementList())
				{
					String name = "out";
					if (node instanceof SClassDefinition)
					{
						name = ((SClassDefinition) node).getName().name;
					} else if (node instanceof AModuleModules)
					{
						name = ((AModuleModules) node).getName().name;
					}
					name += ".svg";
					File generated = p.getModelBuildPath().getOutput().getLocation().toFile();
					generated.mkdirs();
					Main.makeImage(node, "svg", new File(generated, name));
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

}
