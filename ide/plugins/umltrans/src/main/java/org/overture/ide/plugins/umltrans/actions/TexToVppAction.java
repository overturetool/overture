/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.umltrans.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.overturetool.tex.ClassExstractorFromTexFiles;

public class TexToVppAction implements IObjectActionDelegate
{

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public TexToVppAction()
	{
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{

		org.eclipse.swt.widgets.Shell s = new org.eclipse.swt.widgets.Shell();

		org.eclipse.swt.widgets.FileDialog fd = new org.eclipse.swt.widgets.FileDialog(
				s, SWT.MULTI);
		fd.setText("Open");

		String[] filterExt = { "*.tex" };
		fd.setFilterExtensions(filterExt);
		String ret = fd.open();
		String[] fLst = fd.getFileNames();
		if (ret != null)
		{

			try
			{
				List<File> files = new Vector<File>();
				for (int i = 0; i < fLst.length; i++)
				{
					String separator = System.getProperty("file.separator");
					files.add(new File(fd.getFilterPath() + separator + fLst[i]));
				}

				

				translate(files, new File(files.get(0).getParent()));
				

			} catch (Exception ex)
			{
				System.err.println(ex.getMessage() + ex.getStackTrace());
				ConsoleWriter.ConsolePrint(s, ex);
			}

		}
	}

	private void translate(final List<File> files, final File outputDir)
			throws FileNotFoundException, CGException, IOException
	{

		final Job expandJob = new Job("Translating VDM to UML")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				monitor.worked(IProgressMonitor.UNKNOWN);
				try
				{
					final List<File> vppFiles = ClassExstractorFromTexFiles.exstract(
							files,
							outputDir);

					for (int i = 0; vppFiles != null && i < vppFiles.size(); i++)
						ConsoleWriter.ConsolePrint(shell, "Output file: "
								+ vppFiles.get(i));

				} catch (FileNotFoundException e)
				{

					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans",
							"Translation error in file", e);
				} catch (IOException e)
				{

					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans",
							"Translation error in file", e);
				} catch (Exception e)
				{

					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans", "Translation error", e);
				}

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK, "org.overture.ide.umltrans",
						IStatus.OK, "Translation completed", null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(0);

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
