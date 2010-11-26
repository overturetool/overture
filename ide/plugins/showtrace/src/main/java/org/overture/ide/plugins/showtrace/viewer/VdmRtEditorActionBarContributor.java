package org.overture.ide.plugins.showtrace.viewer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.EditorActionBarContributor;

public class VdmRtEditorActionBarContributor extends EditorActionBarContributor
{
	VdmRtLogEditor activeEditor;
	private IEditorPart activeEditorPart;

	// Action exportAction;
	public VdmRtEditorActionBarContributor()
	{
		createActions();
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		if (targetEditor instanceof VdmRtLogEditor)
			activeEditor = (VdmRtLogEditor) targetEditor;

	}

	private void createActions()
	{
		// exportAction = new Action() {
		// public void run() {
		// if(activeEditor!=null)
		// {
		// activeEditor.getExportDiagramAction().run();
		// }
		// }
		// };
		// exportAction.setText("Export image");
		// exportAction.setToolTipText("Sample Action tool tip");
		// exportAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
		// getImageDescriptor(IDE.SharedImages.IMG_OBJS_TASK_TSK));
	}

	public void contributeToMenu(IMenuManager manager)
	{
		IMenuManager menu = new MenuManager("RT Logviewer &Menu");
		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		if (activeEditor != null)
		{
			menu.add(activeEditor.getExportDiagramAction());
			menu.add(activeEditor.getMoveHorizontalAction());
		}
	}

	public void contributeToToolBar(IToolBarManager manager)
	{
		manager.add(new Separator());
		if (activeEditor != null)
		{
			manager.add(activeEditor.getExportDiagramAction());
			manager.add(activeEditor.getMoveHorizontalAction());
		}
	}
	
	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part)
			return;

		activeEditorPart = part;

		
	}

}
