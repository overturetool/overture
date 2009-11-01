package org.overture.ide.plugins.proofsupport.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.overture.ide.plugins.proofsupport.views.actions.Data;
import org.overture.ide.plugins.proofsupport.views.actions.PoTableView;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.proofsupport.AutomaticProofSystemBatch;
import org.overturetool.proofsupport.ProofResult;
import org.overturetool.proofsupport.external_tools.pog.PoGenerator;
import org.overturetool.proofsupport.external_tools.pog.PoProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsPoProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;

public class RunProofsupportAction implements IObjectActionDelegate {

	private Shell shell;
	private IWorkbenchPart targetPart;

	/**
	 * Constructor for Action1.
	 */
	public RunProofsupportAction() {
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

			List<IFile> files = ProjectUtility.getFiles(selectedProject,
					"org.eclipse.core.runtime.text");
			VdmToolsWrapper vdmTools = null;
			String holDir = null;
			String mosmlDir = null;

			// TODO: this code is a hack and should be replaced by a proper
			// configuration menu
			// and calls to where the resources reside!
			String fileName = null;
			for (IFile f : files) {
				fileName = f.getName();
				ConsoleWriter.ConsolePrint(s, "file: " + fileName);
				if (fileName.equals("proverSettings.txt")) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(new FileInputStream(f
									.getLocation().toFile())));
					String vppdeExecutable = reader.readLine();
					mosmlDir = reader.readLine();
					holDir = reader.readLine();
					reader.close();
					vdmTools = new VdmToolsWrapper(vppdeExecutable);
				}
			}

			if (mosmlDir != null && holDir != null && vdmTools != null) {
				ConsoleWriter.ConsolePrint(s, "Proof system settings loaded");

				String vdmModel = selectModelFile(s, selectedProject);
				if (vdmModel != null) {
					ConsoleWriter.ConsolePrint(s, vdmModel + " selected.");
					if (selectedProject
							.hasNature(VdmPpProjectNature.VDM_PP_NATURE)
							|| selectedProject
									.hasNature(VdmSlProjectNature.VDM_SL_NATURE))
						createProofs(selectedProject, vdmModel, holDir,
								mosmlDir, vdmTools, new VdmToolsPoProcessor(),
								s);
				} else
					ConsoleWriter.ConsolePrint(s, "Operation canceled.");
			} else {
				ConsoleWriter
						.ConsolePrint(
								s,
								"Can't access settings for prover, please set the required variables in proverSettings.txt");
				ProverSettings.createDefaultFile(selectedProject);
			}

		} catch (Exception ex) {
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(s, ex);
		}

	}

	private String selectModelFile(org.eclipse.swt.widgets.Shell s,
			IProject selectedProject) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath location = root.getLocation();

		IPath projectPath = selectedProject.getFullPath();

		String vdmModel = selectFileToProcess(s, location.toOSString()
				+ System.getProperty("file.separator")
				+ projectPath.toOSString(), "Select the model to be analyzed");
		return vdmModel;
	}

	private String selectFileToProcess(org.eclipse.swt.widgets.Shell s,
			String workspace, String title) {
		org.eclipse.swt.widgets.FileDialog fd = new org.eclipse.swt.widgets.FileDialog(
				s, SWT.OPEN);

		fd.setFilterPath(workspace);
		fd.setText(title);
		String[] filterExt = { "*.vdmpp" };
		fd.setFilterExtensions(filterExt);
		return fd.open();
	}

	private void createProofs(final IProject selectedProject,
			final String vdmModel, final String holDir, final String mosmlDir,
			final PoGenerator poGen, final PoProcessor poProc,
			final Shell output) {
		final Job expandJob = new Job("Automatic Proof System running") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				monitor.worked(IProgressMonitor.UNKNOWN);
				try {

					ConsoleWriter.ConsolePrint(output, "Starting proof system");
					AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(
							mosmlDir, holDir, poGen, poProc);
					ProofResult[] result = aps.dischargeAllPos(vdmModel,
							new ArrayList<String>(0));

					for (ProofResult r : result)
						ConsoleWriter.ConsolePrint(output, r.toString());

					ConsoleWriter.ConsolePrint(output, "Proof system done.");
					ConsoleWriter.Show();

					addDataToTable(result);

					selectedProject
							.refreshLocal(IResource.DEPTH_INFINITE, null);

				} catch (Exception e) {

					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide..plugins.proofsupport",
							"Proof error", e);
				}

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK,
						"org.overture.ide..plugins.proofsupport", IStatus.OK,
						"Automatic Proof System completed", null);

			}

			private void addDataToTable(ProofResult[] result) {
				final List<Data> list = new ArrayList<Data>();

				for (ProofResult r : result) {
					list.add(new Data(r));
				}

				shell.getDisplay().asyncExec(new Runnable() {

					public void run() {
						IViewPart v = targetPart
								.getSite()
								.getPage()
								.findView(
										"org.overture.ide.plugins.proofsupport.views.PoTableView");

						if (v instanceof PoTableView) {
							((PoTableView) v).setDataList(list);

						}
					}

				});

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
