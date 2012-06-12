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
package org.overture.ide.plugins.poviewer.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.poviewer.Activator;
import org.overture.ide.plugins.poviewer.IPoviewerConstants;
import org.overture.ide.plugins.poviewer.view.PoOverviewTableView;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.pog.obligation.ProofObligationList;

public abstract class ViewPosAction implements IObjectActionDelegate
{

	private Shell shell;
	private IWorkbenchPart targetPart;
	private File selectedFile = null;
	private File libFolder = null;

	/**
	 * Constructor for Action1.
	 */
	public ViewPosAction()
	{
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
		this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{

		org.eclipse.swt.widgets.Shell s = new org.eclipse.swt.widgets.Shell();

		try
		{
			IProject selectedProject = null;
			selectedProject = ProjectHelper.getSelectedProject(action, selectedProject);
			
			
			if (selectedProject == null)
			{
				ConsoleWriter.ConsolePrint(shell, "Could not find selected project");
				return;
			}

			IFile tmpFile = ProjectHelper.getSelectedFile(action);
			if (tmpFile != null)
			{
				 selectedFile = tmpFile.getLocation().toFile();
			}

			IVdmProject project = (IVdmProject) selectedProject.getAdapter(IVdmProject.class);
			
			libFolder = new File(selectedProject.getLocation().toFile(),"lib");
			
			viewPos(project);

		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(s, ex);
		}

	}

	public boolean skipElement(File file)
	{
		return (selectedFile != null && !selectedFile.getName().equals((file.getName()))) || (selectedFile==null && isLibrary(file));

	}

	private boolean isLibrary(File file)
	{
		return file.getAbsolutePath().startsWith(libFolder.getAbsolutePath());
	}

	protected abstract String getNature();

	private void viewPos(final IVdmProject project) throws PartInitException
	{
		final IVdmModel model = project.getModel();

		if (!model.isParseCorrect())
		{
			return;
			//return new Status(Status.ERROR, IPoviewerConstants.PLUGIN_ID, "Project contains parse errors");
		}

		if (model == null || !model.isTypeCorrect())
		{
			VdmTypeCheckerUi.typeCheck(shell, project);
		}
		final Job showJob = new Job("Generating Proof Obligations")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				monitor.worked(IProgressMonitor.UNKNOWN);

				

				try
				{
					if (!model.isParseCorrect() || !model.isTypeCorrect())
					{
						return new Status(Status.ERROR, IPoviewerConstants.PLUGIN_ID, "Project is not build correctly, build error");
					}

					final ProofObligationList pos = getProofObligations(model);
					pos.renumber();
					showPOs(project, pos);

				} catch (Exception e)
				{
					e.printStackTrace();
					return new Status(IStatus.ERROR, IPoviewerConstants.PLUGIN_ID, "Error showing PO's Model state: Parse="+model.isParseCorrect()+" TC="+model.isTypeCorrect() , e);
				}
				return new Status(IStatus.OK, "org.overture.ide.plugins.poviewer", "Ok");
			}

		};
		showJob.schedule();

	}

	private void openPoviewPerspective()
	{
		try
		{
			// IWorkbenchPage p=
			// targetPart.getSite().getWorkbenchWindow().o.getSite().getWorkbenchWindow().openPage(PoviewerPluginConstants.ProofObligationPerspectiveId,null);
			// p.activate(targetPart);
			PlatformUI.getWorkbench().showPerspective(IPoviewerConstants.ProofObligationPerspectiveId, targetPart.getSite().getWorkbenchWindow());
		} catch (WorkbenchException e)
		{

			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	protected abstract ProofObligationList getProofObligations(IVdmModel model)
			throws NotAllowedException;

	private void showPOs(final IVdmProject project, final ProofObligationList pos)
	{
		targetPart.getSite().getPage().getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				IViewPart v;
				try
				{
					v = targetPart.getSite().getPage().showView(IPoviewerConstants.PoOverviewTableViewId);
					if (v instanceof PoOverviewTableView)
					{
						((PoOverviewTableView) v).setDataList(project, pos);

					}

					openPoviewPerspective();
				} catch (PartInitException e)
				{
					if (Activator.DEBUG)
					{
						e.printStackTrace();
					}
				}

			}

		});
	}
}
