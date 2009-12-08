package org.overture.ide.plugins.poviewer.view;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.overturetool.vdmj.pog.ProofObligation;

public class PoTableView extends ViewPart implements ISelectionListener {
	private Text viewer;
	final Display display = Display.getCurrent();
	/**
	 * The constructor.
	 */
	public PoTableView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new Text(parent, SWT.MULTI);

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.setFocus();
	}

	public void setDataList(final IProject project, final ProofObligation data) {
		display.asyncExec(new Runnable() {

			public void run() {
				viewer.setText(data.getValue());
			}

		});
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

	}
}
