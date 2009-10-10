package org.overture.ide.plugins.idedebughelper.actions;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class AssociateFilesWithNatureAction implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public AssociateFilesWithNatureAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		org.eclipse.swt.widgets.Shell s = new org.eclipse.swt.widgets.Shell();
		try {
			IProject selectedProject = null;
			selectedProject = ProjectHelper.getSelectedProject(action,
					selectedProject);
			if (selectedProject == null) {
				ConsoleWriter.ConsolePrint(shell,
						"Could not find selected project");
				return;
			}

			if (selectedProject.hasNature(VdmPpProjectNature.VDM_PP_NATURE))
				renameFiles(selectedProject, "vdmpp");
			if (selectedProject.hasNature(VdmSlProjectNature.VDM_SL_NATURE))
				renameFiles(selectedProject, "vdmsl");
			 if(selectedProject.hasNature(VdmRtProjectNature.VDM_RT_NATURE))
			 renameFiles(selectedProject,"vdmrt");

		} catch (Exception ex) {
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(s, ex);
		}

	}

	private void renameFiles(final IProject selectedProject, final String string) {
		final Job expandJob = new Job("Renaming") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				monitor.worked(IProgressMonitor.UNKNOWN);
				try {
					for (File f : selectedProject.getLocation().toFile()
							.listFiles()) {
						if (f.isFile() && !f.getName().startsWith(".")) {
							File dest = new File(f.getAbsolutePath().substring(
									0, f.getAbsolutePath().lastIndexOf('.'))
									+ "." + string);
							f.renameTo(dest);
							f.delete();
						}
						
					}
					selectedProject.refreshLocal(IResource.DEPTH_INFINITE, null);

				} catch (Exception e) {

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
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
