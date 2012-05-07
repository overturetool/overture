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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.umlMapper.Vdm2Uml;
import org.overturetool.umltrans.Main.CmdLineProcesser;
import org.overturetool.umltrans.Main.ParseException;
import org.overturetool.umltrans.uml.IUmlModel;
import org.overturetool.umltrans.vdm2uml.Uml2XmiEAxml;
import org.overturetool.util.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;


public class Vdm2UmlAction implements IObjectActionDelegate
{

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public Vdm2UmlAction()
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
		IProject selectedProject = null;
		selectedProject = ProjectHelper.getSelectedProject(
				action,
				selectedProject);
		if (selectedProject == null)
		{
			ConsoleWriter.ConsolePrint(shell, "Could not find selected project");
			return;
		}

		//String[] filterExt = { "vpp", "tex" };

		try
		{

			
			
			List<IVdmSourceUnit> files = new Vector<IVdmSourceUnit>();// = ProjectUtility.getFiles(selectedProject);
			
			IVdmProject p = (IVdmProject) selectedProject.getAdapter(IVdmProject.class);
			IVdmModel model = p.getModel();
			if(p != null)
			{				
				if(p.getDialect() == Dialect.VDM_PP || p.getDialect() == Dialect.VDM_RT)
				{
					
					if(!model.isTypeChecked())
					{
						VdmTypeCheckerUi.typeCheck(shell, p);
					}
					
					if(!model.isTypeCorrect())
					{
						ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Not TypeChecked", "Not TypeChecked", new Status(IStatus.ERROR, "umlTrans", "Project is not type checked"));
						return;
					}
					
					files = p.getSpecFiles();
				}else
				{
					return;
				}
			}
			
			ClassList cList = model.getClassList();
			System.out.println(cList);
			
//			if (selectedProject.hasNature(VdmPpProjectNature.VDM_PP_NATURE))
//				files= ProjectUtility.getFiles(selectedProject, VdmPpCorePluginConstants.CONTENT_TYPE);
//			
//			if (selectedProject.hasNature(VdmRtProjectNature.VDM_RT_NATURE))
//				files= ProjectUtility.getFiles(selectedProject, VdmRtCorePluginConstants.CONTENT_TYPE);
			Vdm2Uml vdm2umlMapper = new Vdm2Uml();
			IUmlModel umlModel = vdm2umlMapper.init(cList);
			

			
			List<File> filesPathes = new Vector<File>();
			for (IVdmSourceUnit file : files)
			{
				filesPathes.add(file.getSystemFile());
			}

			File genDir = new File(selectedProject.getLocation().toFile(),"generated");
			
			if(!genDir.exists())
			{
				genDir.mkdir();
			}
			
			
			File outFile = new File(genDir,
					selectedProject.getName() + ".xmi");
			if (outFile != null)
			{
				Uml2XmiEAxml xmi = new Uml2XmiEAxml();
				xmi.Save(outFile.getAbsolutePath(),
						umlModel,
						null);
//				if (!(outFile.endsWith(".xml") || outFile.endsWith(".xml")))
//					outFile += ".xml";
				//translate(filesPathes, outFile,selectedProject);


			}

		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(shell, ex);

		}

	}

	private void translate(final List<File> filesPathes,
			final File outFile, final IProject selectedProject)
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
					CmdLineProcesser.setOutput(new ConsoleWriter(shell));
					CmdLineProcesser.toUml( outFile,filesPathes);
				} catch (FileNotFoundException e)
				{
					ConsoleWriter.ConsolePrint(shell, e);
					e.printStackTrace();
					return new Status(IStatus.ERROR, "org.overture.ide.umltrans",
							 "Translation error in file", e);
				}catch(ParseException e)
				{
//					for (ParserError error : e.getErrors())
//					{
//						try{
//							System.out.println(error.toString());
//						//FileUtility.addMarker(ProjectUtility.findIFile(selectedProject, error.file), error.message, error.line,error.col, IMarker.SEVERITY_ERROR);
//						}catch(Exception ex)
//						{
//							
//						}
//					}
					
					e.printStackTrace();
				}
				catch (CGException e)
				{
					ConsoleWriter.ConsolePrint(shell, e);
					e.printStackTrace();
					return new Status(IStatus.ERROR, "org.overture.ide.umltrans",
							 "Translation error in specification", e);
				} catch (IOException e)
				{
					ConsoleWriter.ConsolePrint(shell, e);
					e.printStackTrace();
					return new Status(IStatus.ERROR, "org.overture.ide.umltrans",
							 "Translation error in file", e);
				}catch(Exception e)
				{
					ConsoleWriter.ConsolePrint(shell, e);
					return new Status(IStatus.ERROR, "org.overture.ide.umltrans",
							 "Unknown error", e);
				}

				shell.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						try
						{
							selectedProject.refreshLocal(1, null);
						} catch (CoreException e)
						{
							
							e.printStackTrace();
						}
					}

				});

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK, "org.overturetool.umltrans",
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
