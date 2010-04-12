package org.overture.ide.debug.core.launching;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;
import org.overture.ide.debug.core.IDebugConstants;

public class VdmSourcePathComputerDelegate implements
		ISourcePathComputerDelegate
{

	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException
	{

		IProject project = null;
		IVdmProject vdmProject = null;
		project = ResourcesPlugin.getWorkspace()
				.getRoot()
				.getProject(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT,
						""));

		if (project != null && VdmProject.isVdmProject(project))
		{
			vdmProject = VdmProject.createProject(project);
		}

		String path = vdmProject.getFullPath().toOSString();

		ISourceContainer sourceContainer = null;
		if (path != null)
		{
			IResource resource = ResourcesPlugin.getWorkspace()
					.getRoot()
					.findMember(vdmProject.getFullPath());
			if (resource != null)
			{
				IContainer container = resource.getParent();
				if (container.getType() == IResource.PROJECT)
				{
					sourceContainer = new ProjectSourceContainer((IProject) container,
							false);
				} else if (container.getType() == IResource.FOLDER)
				{
					sourceContainer = new FolderSourceContainer(container,
							false);
				}
			}
		}
		if (sourceContainer == null)
		{
			sourceContainer = new WorkspaceSourceContainer();
		}
		return new ISourceContainer[] { sourceContainer };
	}

}
