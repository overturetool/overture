/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.builder;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;

public class VdmBuilder extends VdmCoreBuilder
{

	@SuppressWarnings("deprecation")
	@Override
	public void build(final IProgressMonitor monitor) throws CoreException
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("VdmBuilder.build:      "+ getProject().getName());
		}
		try
		{
			final SafeBuilder builder = new SafeBuilder(getVdmProject(),monitor);

			builder.start();
			while (!builder.isInterrupted() && builder.isAlive())
			{
				if (monitor.isCanceled())
				{
					builder.interrupt();
					Thread.sleep(2000);
					builder.stop();
				}
				Thread.sleep(10);
			}
		} catch (Exception e)
		{
			VdmCore.log(e);
		}

	}

	public void clean(IProgressMonitor monitor)
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("VdmBuilder.clean:      "+ getProject().getName());
		}
		monitor.beginTask("Cleaning project: " + getProject().getName(), IProgressMonitor.UNKNOWN);

		if (getProject().getAdapter(IVdmProject.class) != null)
		{
			SafeBuilder.clearProblemMarkers(getProject());

			// IMPORTANT we do not have an incremental builder so a full parse/
			// build is required, therefore remove any
			// AST nodes in store.
			// clearInternalModel();

			try
			{
				IVdmProject vdmProject = ((IVdmProject) getProject().getAdapter(IVdmProject.class));
				if (vdmProject != null)
				{
					IContainer container = vdmProject.getModelBuildPath().getOutput();
					ResourcesPlugin.getWorkspace().delete(new IResource[] { container }, true, monitor);
				}

			} catch (Exception e)
			{
				VdmCore.log("Error cleaning project: " + getProject(), e);
			}
		}
		monitor.done();

	}

	public void endBuild(IProgressMonitor monitor)
	{
	}

	public void initialize()
	{
		if (VdmCore.DEBUG)
		{
			System.out.println("VdmBuilder.initialize: "+ getProject().getName());
		}

		syncProjectResources();

	}

}
