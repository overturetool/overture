/*
 * #%~
 * org.overture.ide.plugins.csk
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
package org.overture.ide.plugins.csk.handlers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.plugins.csk.Activator;
import org.overture.ide.plugins.csk.internal.VdmTools;

public class OpenVdmToolsProjectCommandHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		final IContainer c = (IContainer) selection.getFirstElement();

		final IProject project = c.getProject();
		
		final IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
		
		if (vdmProject != null)
		{
			final List<File> files = new ArrayList<File>();

			try
			{
				for (IVdmSourceUnit source : vdmProject.getSpecFiles())
				{
					files.add(source.getSystemFile());
				}

				UIJob job = new UIJob("Create VDM Tools Project")
				{
					
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						try
						{
							new VdmTools().createProject(this.getDisplay().getActiveShell(), vdmProject, files);
							project.refreshLocal(IResource.DEPTH_INFINITE, null);
						} catch (IOException e)
						{
							return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to open VDM Tools",e);
						} catch (CoreException e)
						{
							return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to open VDM Tools",e);
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
								
				

			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
