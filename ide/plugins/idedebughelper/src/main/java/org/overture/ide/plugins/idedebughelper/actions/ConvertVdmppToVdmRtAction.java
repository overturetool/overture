package org.overture.ide.plugins.idedebughelper.actions;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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

public class ConvertVdmppToVdmRtAction implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public ConvertVdmppToVdmRtAction() {
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
					String name = selectedProject.getName() + "RT";
					URI location = new URI(ResourcesPlugin.getWorkspace()
							.getRoot().getLocationURI()
							+ "/" + name);

					IProject project = CustomProjectSupport.createProject(name,
							location, VdmRtProjectNature.VDM_RT_NATURE);
					CustomProjectSupport.addComment(project, "Generated from: "
							+ name);

					CustomProjectSupport.addBuilder(project, "org.eclipse.dltk.core.scriptbuilder");

					FileUtil.copyFiles(selectedProject.getLocation().toFile(),
							project.getLocation().toFile());
					
					for (File f : project.getLocation().toFile()
							.listFiles()) {
						FileUtil.renameExtensionFile(f, "vdmrt");
					}

					project.refreshLocal(IResource.DEPTH_INFINITE, null);
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
							IResource.DEPTH_INFINITE, monitor);

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
