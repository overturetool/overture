/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.utility;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;

public class VdmTypeCheckerUi
{

	public static class CompletedStatus
	{
		private boolean completed;

		public synchronized boolean isCompleted()
		{
			return completed;
		}

		public synchronized void setCompledted()
		{
			completed = true;
		}
	}

	public static boolean typeCheck(Shell shell, final IVdmProject project)
	{
		Assert.isNotNull(shell, "Shell for type checker cannot be null");
		Assert.isNotNull(project, "Project for type checker cannot be null");

		final IVdmModel model = project.getModel();

		final CompletedStatus checkCompleted = new CompletedStatus();
		// TODO we may be able to use the istypechecked and istypecorrect in a better way here
		if (!model.getRootElementList().isEmpty() && model.isTypeCorrect())
		{
			return true; // skip future checking to speed up the process
		}

		try
		{
			IRunnableWithProgress op = new IRunnableWithProgress()
			{

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException
				{
					try
					{
						model.refresh(false, monitor);
						project.typeCheck(monitor);
						checkCompleted.setCompledted();

					} catch (CoreException e)
					{
						throw new InvocationTargetException(e);
					}

				}
			};
			new ProgressMonitorDialog(shell).run(true, true, op);
		} catch (InvocationTargetException e)
		{
			return false;
		} catch (InterruptedException e)
		{

		}

		while (!checkCompleted.isCompleted())
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
			}
		}

		return project.getModel().isTypeCorrect();
	}

	public static boolean typeCheck(final IVdmProject project,
			IProgressMonitor monitorParent)
	{
		Assert.isNotNull(project, "Project for type checker cannot be null");

		final IVdmModel model = project.getModel();

		IProgressMonitor monitor = new SubProgressMonitor(monitorParent, 20);
		final CompletedStatus checkCompleted = new CompletedStatus();
		// TODO we may be able to use the istypechecked and istypecorrect in a better way here
		if (!model.getRootElementList().isEmpty() && model.isTypeCorrect())
		{
			return true; // skip future checking to speed up the process
		}

		model.refresh(false, monitor);
		try
		{
			project.typeCheck(monitor);
		} catch (CoreException e1)
		{
			return false;
		}
		checkCompleted.setCompledted();

		while (!checkCompleted.isCompleted())
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
			}
		}

		return project.getModel().isTypeCorrect();
	}
}
