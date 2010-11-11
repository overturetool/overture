package org.overture.ide.vdmrt.ui.actions;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;

@SuppressWarnings("restriction")
public class CloneVdmPpProjectToVdmRtAction implements IObjectActionDelegate
{
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{

	}

	public void run(IAction action)
	{
		try
		{
			IVdmProject selectedProject = TreeSelectionLocater.getSelectedProject(action);
			if (selectedProject == null)
			{
				return;
			}
			IProject project = (IProject) selectedProject.getAdapter(IProject.class);

			if (project != null)
			{
				if (project.hasNature(IVdmPpCoreConstants.NATURE))
				{
					IProjectDescription description = new ProjectDescription();
					description.setName(project.getName() + "_VDM_RT");
					description.setBuildSpec(project.getDescription().getBuildSpec());
					List<String> natures = new Vector<String>();
					natures.addAll(Arrays.asList(project.getDescription().getNatureIds()));

					if (natures.contains(IVdmPpCoreConstants.NATURE))
					{
						natures.remove(IVdmPpCoreConstants.NATURE);
					}
					natures.add(IVdmRtCoreConstants.NATURE);
					String[] n = new String[natures.size()];
					natures.toArray(n);
					description.setNatureIds(n);

					project.copy(description, true, null);

					rename(project.getLocation().toFile());
				}
			}
		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());

		}

	}

	public static void rename(File file)
	{
		if (file.isFile() && file.getName().toLowerCase().endsWith("vdmpp"))
		{
			String sourcePath = file.getAbsolutePath();
			String newPath = sourcePath.substring(0, sourcePath.length() - 5)
					+ "vdmrt";
			if(file.renameTo(new File(newPath)))
			{
				
			}
		
		}
		if (file.isDirectory())
		{
			for (File member : file.listFiles())
			{
				rename(member);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection)
	{

	}

}
