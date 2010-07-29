package org.overture.ide.plugins.csk.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ViewPluginAction;

import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.plugins.csk.internal.VdmTools;

@SuppressWarnings("restriction")
public class ActionOpenProject implements IViewActionDelegate
{
	IViewPart view;

	public void init(IViewPart view)
	{
		this.view = view;
	}

	public void run(IAction action)
	{
		IVdmProject vdmProject = getSelectedProject(action);
		if (vdmProject != null)
		{
			List<File> files = new Vector<File>();

			try
			{
				for (IVdmSourceUnit source : vdmProject.getSpecFiles())
				{
					files.add(source.getSystemFile());
				}

				new VdmTools().createProject(view.getSite().getShell(), vdmProject, files);
				IProject project = (IProject) vdmProject.getAdapter(IProject.class);
				
				project.refreshLocal(IResource.DEPTH_INFINITE, null);

			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub

	}

	public static IVdmProject getSelectedProject(IAction action)
	{

		if (action instanceof ViewPluginAction)
		{
			ViewPluginAction vAction = (ViewPluginAction) action;

			if (vAction.getSelection() instanceof TreeSelection)
			{
				Object selection = ((TreeSelection) vAction.getSelection()).getFirstElement();
				if (selection instanceof IVdmProject)
				{
					return (IVdmProject) selection;
				}
			}
		}
		return null;
	}

}
