package org.overture.ide.plugins.poviewer.actions;

import org.eclipse.core.resources.IProject;
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
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.pog.ProofObligationList;

public class ViewPosAction implements IObjectActionDelegate {

	private Shell shell;
	private IWorkbenchPart targetPart;

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

			RootNode root = AstManager.instance().getRootNode(selectedProject,
					VdmPpProjectNature.VDM_PP_NATURE);
			if (root != null && root.isChecked())
				viewPos(selectedProject, root);

		} catch (Exception ex) {
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(s, ex);
		}

	}

	private void viewPos(IProject project, RootNode root)
			throws PartInitException {
		ClassList cl = new ClassList();
		for (Object definition : root.getRootElementList()) {
			if (definition instanceof ClassDefinition)
				cl.add((ClassDefinition) definition);
		}

		ProofObligationList pos = cl.getProofObligations();

		IViewPart v = targetPart.getSite().getPage().showView(
				PoviewerPluginConstants.PoOverviewTableViewId);

		if (v instanceof PoOverviewTableView) {
			((PoOverviewTableView) v).setDataList(project, pos);

		}
		
		openPoviewPerspective();

	}

	private void openPoviewPerspective() {
		try {
//		IWorkbenchPage p=	targetPart.getSite().getWorkbenchWindow().o.getSite().getWorkbenchWindow().openPage(PoviewerPluginConstants.ProofObligationPerspectiveId,null);
//p.activate(targetPart);
PlatformUI.getWorkbench().showPerspective(PoviewerPluginConstants.ProofObligationPerspectiveId, targetPart.getSite().getWorkbenchWindow());
		} catch (WorkbenchException e) {
			
			e.printStackTrace();
		}
	}
	
	

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
