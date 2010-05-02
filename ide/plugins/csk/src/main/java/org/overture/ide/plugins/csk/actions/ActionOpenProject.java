package org.overture.ide.plugins.csk.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

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

	public void init(IViewPart view)
	{

	}

	public void run(IAction action)
	{
		IVdmProject project = getSelectedProject(action);
		if (project != null)
		{
			List<File> files = new Vector<File>();

			try
			{
				for (IVdmSourceUnit source : project.getSpecFiles())
				{
					files.add(source.getSystemFile());
				}

				new VdmTools().createProject(project.getLocation().toFile(), project.getName(), files);

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
