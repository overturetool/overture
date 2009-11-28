package org.overture.ide.plugins.poviewer.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
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
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.plugins.poviewer.PoviewerPluginConstants;
import org.overture.ide.plugins.poviewer.view.PoOverviewTableView;
import org.overture.ide.utility.ProjectUtility;
import org.overturetool.vdmj.pog.ProofObligationList;

public abstract class ViewPosAction implements IObjectActionDelegate {

	private Shell shell;
	private IWorkbenchPart targetPart;
	private File selectedFile = null;

	/**
	 * Constructor for Action1.
	 */
	public ViewPosAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		this.targetPart = targetPart;
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

			IFile tmpFile = ProjectHelper.getSelectedFile(action);
			if (tmpFile != null) {
				selectedFile = ProjectUtility.getFile(selectedProject, tmpFile);
			}

			viewPos(selectedProject);

		} catch (Exception ex) {
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(s, ex);
		}

	}

	

	public boolean skipElement(File file) {
		return (selectedFile != null && !selectedFile.getName().equals(
				(file.getName())));

	}

	protected abstract String getNature();

	private void viewPos(final IProject project) throws PartInitException {

		final Job showJob = new Job("Showing PO's") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.worked(IProgressMonitor.UNKNOWN);

				RootNode root = AstManager.instance().getRootNode(project,
						getNature());
				if (root == null || !root.isChecked()) {
					IStatus res = buildProject(project, monitor, root);
					if (!res.isOK())
						return res;

					root = AstManager.instance().getRootNode(project,
							getNature());
				}

				try {
					if (root == null || (root != null && !root.isChecked()))
						throw new Exception(
								"Project is not build correctly, build error");

					final ProofObligationList pos = getProofObligations(root);

					showPOs(project, pos);

				} catch (Exception e) {
					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.plugins.poviewer",
							"Error showing PO's", e);
				}
				return new Status(IStatus.OK,
						"org.overture.ide.plugins.poviewer", "Ok");
			}

		};
		showJob.schedule();

	}

	private void openPoviewPerspective() {
		try {
			// IWorkbenchPage p=
			// targetPart.getSite().getWorkbenchWindow().o.getSite().getWorkbenchWindow().openPage(PoviewerPluginConstants.ProofObligationPerspectiveId,null);
			// p.activate(targetPart);
			PlatformUI.getWorkbench().showPerspective(
					PoviewerPluginConstants.ProofObligationPerspectiveId,
					targetPart.getSite().getWorkbenchWindow());
		} catch (WorkbenchException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	private IStatus buildProject(final IProject project,
			IProgressMonitor monitor, RootNode root) {
		if (root == null || !root.isChecked())
			try {
				project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			} catch (CoreException e1) {
				e1.printStackTrace();
				return new Status(IStatus.ERROR,
						"org.overture.ide.plugins.poviewer", "Error building",
						e1);
			}
		return new Status(IStatus.OK, PoviewerPluginConstants.PoViewerId,
				"Build successfull");
	}

	protected abstract ProofObligationList getProofObligations(RootNode root);

	private void showPOs(final IProject project, final ProofObligationList pos) {
		targetPart.getSite().getPage().getWorkbenchWindow().getShell()
				.getDisplay().asyncExec(new Runnable() {

					public void run() {
						IViewPart v;
						try {
							v = targetPart
									.getSite()
									.getPage()
									.showView(
											PoviewerPluginConstants.PoOverviewTableViewId);
							if (v instanceof PoOverviewTableView) {
								((PoOverviewTableView) v).setDataList(project,
										pos);

							}

							openPoviewPerspective();
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});
	}
}
