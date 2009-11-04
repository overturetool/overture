package org.overture.ide.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/***
 * Build project button org.eclipse.ui.popupMenus-objectContribution
 * @author kela
 *
 */
public class CleanProjectAction  extends ProjectAction implements IObjectActionDelegate {

	
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
		
	}

	public void run(IAction action) {
		try {
			IProject selectedProject = null;
			selectedProject = getSelectedProject(action,
					selectedProject);
			selectedProject.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		} catch (CoreException e) {
			System.out.println("Error forcing clean");
			e.printStackTrace();
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
