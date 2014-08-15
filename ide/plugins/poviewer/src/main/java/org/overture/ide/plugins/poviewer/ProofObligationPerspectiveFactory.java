/*
 * #%~
 * org.overture.ide.plugins.poviewer
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
