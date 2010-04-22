package org.overture.ide.plugins.poviewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.overture.ide.ui.IVdmUiConstants;

public class ProofObligationPerspectiveFactory implements IPerspectiveFactory {

	private IPageLayout factory;
	public void createInitialLayout(IPageLayout layout) {
		this.factory = layout;
		addViews();
	}
	private void addViews() {
		// Creates the overall folder layout. 
		// Note that each new Folder uses a percentage of the remaining EditorArea.
		String editorArea = factory.getEditorArea();
		
		IFolderLayout folder= factory.createFolder("left", IPageLayout.LEFT, (float)0.2, editorArea); //$NON-NLS-1$
		String navigator =IVdmUiConstants.NAVIGATOR;

		folder.addView(navigator);
		
		IFolderLayout bottom =
			factory.createFolder(
				"bottomRight", //NON-NLS-1
				IPageLayout.BOTTOM,
				0.75f,
				factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView("org.eclipse.ui.console.ConsoleView");
		bottom.addView(IPoviewerConstants.PoTableViewId); //NON-NLS-1
		

		IFolderLayout right =
			factory.createFolder(
				"Right", //NON-NLS-1
				IPageLayout.RIGHT,
				0.65f,
				factory.getEditorArea());
		
		right.addView(IPoviewerConstants.PoOverviewTableViewId); //NON-NLS-1
		
	}

}
