/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
//		VdmUIPlugin.println("Editor Area: " + editorArea );
//		
//		IFolderLayout folder= layout.createFolder("left", IPageLayout.LEFT, (float)0.2, editorArea); //$NON-NLS-1$
//		String navigator = IVdmIdeUiConstants.NAVIGATOR;
//		folder.addView(navigator);		
//		
//		
		IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75, editorArea); //$NON-NLS-1$
//
		if(VdmUIPlugin.DEBUG)
			outputfolder.addView("org.eclipse.pde.runtime.LogView");
		
		outputfolder.addView(IPageLayout.ID_PROBLEM_VIEW);
		outputfolder.addView(IPageLayout.ID_TASK_LIST);
		outputfolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float)0.75, editorArea);
//		
//		//outputfolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
//		outputfolder.addPlaceholder(IPageLayout.ID_BOOKMARKS);
//		outputfolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
//		
//		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float)0.75, editorArea);
//				
//		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
//		
//		
//		// views - standard workbench
//		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
//		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
//		//layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		//layout.addShowViewShortcut(navigator);
//		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
//		layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
//		
//		// General
//		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
//		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
//		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
//
//		//VDM Stuff
//		layout.addNewWizardShortcut("org.overture.ide.vdmpp.ui.projectWizard");//$NON-NLS-1$

	}

	
	
}
