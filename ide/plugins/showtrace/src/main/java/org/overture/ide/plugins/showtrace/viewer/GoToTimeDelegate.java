package org.overture.ide.plugins.showtrace.viewer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class GoToTimeDelegate implements IEditorActionDelegate {

	VdmRtLogEditor activeEditor;
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	if(targetEditor instanceof VdmRtLogEditor)
		activeEditor = (VdmRtLogEditor) targetEditor;

	}

	public void run(IAction action) {
		activeEditor.getMoveHorizontalAction().run();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
