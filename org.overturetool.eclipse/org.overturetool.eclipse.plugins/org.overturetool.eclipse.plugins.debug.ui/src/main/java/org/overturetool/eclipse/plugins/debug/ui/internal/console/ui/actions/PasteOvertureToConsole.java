package org.overturetool.eclipse.plugins.debug.ui.internal.console.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class PasteOvertureToConsole implements IObjectActionDelegate {

	private ISelection selection;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
			
	}

	public void run(IAction action) {
		//TODO: implement
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;		
	}
}
