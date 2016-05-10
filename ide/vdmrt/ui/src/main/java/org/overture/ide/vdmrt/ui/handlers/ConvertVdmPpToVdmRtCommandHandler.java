/*
 * #%~
 * org.overture.ide.vdmrt.ui
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
package org.overture.ide.vdmrt.ui.handlers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;
import org.overture.ide.vdmrt.core.VdmRtCorePlugin;

@SuppressWarnings("restriction")
public class ConvertVdmPpToVdmRtCommandHandler extends AbstractHandler
		implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
			final IProject project = (IProject) selection.getFirstElement();

			if (project == null)
			{
				return null;
			}
			
			if (project != null)
			{
				if (project.hasNature(IVdmPpCoreConstants.NATURE))
				{
					Job copyProject = new Job("Converting VDM-PP project to VDM-RT...")
					{
						
						@Override
						protected IStatus run(IProgressMonitor monitor)
						{
							try
							{
								monitor.beginTask("Creating new project", IProgressMonitor.UNKNOWN);
								IProjectDescription description = new ProjectDescription();
								description.setName(project.getName() + "_VDM_RT");
								description.setBuildSpec(project.getDescription().getBuildSpec());
								List<String> natures = new ArrayList<String>();
								natures.addAll(Arrays.asList(project.getDescription().getNatureIds()));

								if (natures.contains(IVdmPpCoreConstants.NATURE))
								{
									natures.remove(IVdmPpCoreConstants.NATURE);
								}
								monitor.beginTask("Adding VDM-RT nature to project", IProgressMonitor.UNKNOWN);
								natures.add(IVdmRtCoreConstants.NATURE);
								String[] n = new String[natures.size()];
								natures.toArray(n);
								description.setNatureIds(n);

								monitor.beginTask("Copying files", IProgressMonitor.UNKNOWN);
								project.copy(description, true, monitor);

								IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
								if (newProject != null)
								{
									rename(newProject.getLocation().toFile());
									newProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
								}
								return Status.OK_STATUS;
							} catch (Exception ex)
							{
								return new Status(IStatus.ERROR, VdmRtCorePlugin.PLUGIN_ID, "Error converting VDM-PP project to VDM-RT",ex);
							}
						}
					};
					copyProject.schedule();
				}
				
			}
		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());
		}
		return null;

	}

	public static void rename(File file)
	{
		if (file.isFile() && file.getName().toLowerCase().endsWith("vdmpp"))
		{
			String sourcePath = file.getAbsolutePath();
			String newPath = sourcePath.substring(0, sourcePath.length() - 5)
					+ "vdmrt";
			if (file.renameTo(new File(newPath)))
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
}
