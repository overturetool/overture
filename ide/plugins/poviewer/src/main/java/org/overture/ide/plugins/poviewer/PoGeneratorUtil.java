/*
 * #%~
 * org.overture.ide.plugins.poviewer
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
package org.overture.ide.plugins.poviewer;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.poviewer.view.PoOverviewTableView;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.pog.contexts.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.visitors.PogVisitor;

public class PoGeneratorUtil
{

	private Shell shell;
	// private IWorkbenchPart targetPart;
	private IWorkbenchSite site;
	private File selectedFile = null;
	private File libFolder = null;
	private File selectedFolder;
	private static String lastPoggedProject = null;

	/**
	 * Constructor for Action1.
	 */
	public PoGeneratorUtil(Shell shell, IWorkbenchSite site)
	{
		this.shell = shell;
		this.site = site;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void generate(IProject selectedProject, IFile file)
	{

		if (file != null)
		{
			this.selectedFile = file.getLocation().toFile();
		}

		generate(selectedProject);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void generate(IProject selectedProject, IFolder folder)
	{
		if (folder != null)
		{
			this.selectedFolder = folder.getLocation().toFile();
		}
		generate(selectedProject);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void generate(IProject selectedProject)
	{
		try
		{
			if (selectedProject == null)
			{
				return;
			}

			IVdmProject project = (IVdmProject) selectedProject.getAdapter(IVdmProject.class);

			libFolder = new File(selectedProject.getLocation().toFile(), "lib");
			lastPoggedProject=project.getName();
			viewPos(project);

		} catch (Exception e)
		{
			System.err.println(e.getMessage() + e.getStackTrace());
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, IPoviewerConstants.PLUGIN_ID, "Error in po generation", e));
		}

	}
	
	public void generate(IVdmModel model)
	{
		try
		{
			if (model == null)
			{
				return;
			}

			if (model.getSourceUnits() == null){
				return;
						
			}
			
			if (model.getSourceUnits().isEmpty()){
				return;
			}
			
			IVdmProject vdmproject = (IVdmProject) model.getSourceUnits().get(0).getProject();
			
			IProject iproject = (IProject) vdmproject.getAdapter(IProject.class);
			
			libFolder = new File(iproject.getLocation().toFile(), "lib");

			viewPos(vdmproject);

		} catch (Exception e)
		{
			System.err.println(e.getMessage() + e.getStackTrace());
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, IPoviewerConstants.PLUGIN_ID, "Error in po generation", e));
		}

	}

	public boolean skipElement(File file)
	{
		return (selectedFile != null && !selectedFile.getName().equals((file.getName())))
				|| (selectedFile == null && isLibrary(file) || !inSelectedFolder(file));

	}

	private boolean inSelectedFolder(File file)
	{
		if (selectedFolder == null)
		{
			return true;
		}

		return file.getAbsolutePath().startsWith(this.selectedFolder.getAbsolutePath());

	}

	private boolean isLibrary(File file)
	{
		return file.getAbsolutePath().startsWith(libFolder.getAbsolutePath());
	}

	// protected abstract String getNature();

	private void viewPos(final IVdmProject project) throws PartInitException
	{
		final IVdmModel model = project.getModel();

		if (!model.isParseCorrect())
		{
			return;
			// return new Status(Status.ERROR, IPoviewerConstants.PLUGIN_ID, "Project contains parse errors");
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

					final ProofObligationList pos = getProofObligations(model, project.getDialect());
					pos.renumber();
					showPOs(project, pos);

				} catch (Throwable e)
				{
					e.printStackTrace();
					return new Status(IStatus.ERROR, IPoviewerConstants.PLUGIN_ID, "Error showing PO's Model state: Parse="
							+ model.isParseCorrect()
							+ " TC="
							+ model.isTypeCorrect(), e);
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
			PlatformUI.getWorkbench().showPerspective(IPoviewerConstants.ProofObligationPerspectiveId, site.getWorkbenchWindow());
		} catch (WorkbenchException e)
		{

			e.printStackTrace();
		}
	}

	protected ProofObligationList getProofObligations(IVdmModel model,
			Dialect dialect) throws NotAllowedException, Throwable
	{
		PogVisitor pogVisitor = new PogVisitor();
		ProofObligationList obligations = new ProofObligationList();

		if (!model.isTypeCorrect())
		{
			return null;
		}

		if (dialect == Dialect.VDM_SL)
		{

			for (Object definition : model.getModuleList())
			{
				if (definition instanceof AModuleModules)
					if (!((AModuleModules) definition).getName().toString().equals("DEFAULT")
							&& skipElement(((AModuleModules) definition).getName().getLocation().getFile()))
						continue;
					else
					{
						IProofObligationList tmp = ((AModuleModules) definition).apply(pogVisitor, new POContextStack());
						tmp.trivialCheck();
						obligations.addAll(tmp);
					}
			}

		} else
		{
			for (Object definition : model.getClassList())
			{
				if (definition instanceof SClassDefinition)
				{
					if (skipElement(((SClassDefinition) definition).getLocation().getFile()))
						continue;
					else
					{
						IProofObligationList tmp = pogVisitor.defaultPDefinition((SClassDefinition) definition, new POContextStack());
						tmp.trivialCheck();
						obligations.addAll(tmp);
					}
				}
			}

		}
		final ProofObligationList pos = obligations;
		return pos;
	}

	private void showPOs(final IVdmProject project,
			final ProofObligationList pos)
	{
		site.getPage().getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				IViewPart v;
				try
				{
					v = site.getPage().showView(IPoviewerConstants.PoOverviewTableViewId);
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

	public boolean isPoggedModel(IVdmModel model)
	{
		IVdmProject vdmproject = (IVdmProject) model.getSourceUnits().get(0).getProject();
		if (vdmproject != null)
		{
			return vdmproject.getName().equals(lastPoggedProject);
		}
		return false;
	}
}
