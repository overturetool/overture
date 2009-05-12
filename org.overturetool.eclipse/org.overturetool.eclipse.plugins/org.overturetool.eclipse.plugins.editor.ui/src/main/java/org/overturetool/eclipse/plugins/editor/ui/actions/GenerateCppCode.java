package org.overturetool.eclipse.plugins.editor.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.overturetool.eclipse.plugins.editor.internal.ui.wizards.OvertureGenerateCppWizard;

public class GenerateCppCode implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		
	}

	public void init(IWorkbenchWindow arg0) {
		
	}

	public void run(IAction arg0) {
		OvertureGenerateCppWizard wizard = new OvertureGenerateCppWizard();
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.create();
        dialog.open();

	}

	public void selectionChanged(IAction arg0, ISelection arg1) {
		
	}

}
