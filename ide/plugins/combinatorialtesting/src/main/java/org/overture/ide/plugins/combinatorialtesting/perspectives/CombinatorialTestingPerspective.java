/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.overture.ide.ui.IVdmUiConstants;

/**
 * This class is meant to serve as an example for how various contributions are made to a perspective. Note that some of
 * the extension point id's are referred to as API constants while others are hardcoded and may be subject to change.
 */
public class CombinatorialTestingPerspective implements IPerspectiveFactory
{

	private IPageLayout factory;

	public CombinatorialTestingPerspective()
	{
		super();
	}

	public void createInitialLayout(IPageLayout factory)
	{
		this.factory = factory;
		addViews();
		addActionSets();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
	}

	private void addViews()
	{
		// Creates the overall folder layout.
		// Note that each new Folder uses a percentage of the remaining EditorArea.
		String editorArea = factory.getEditorArea();

		IFolderLayout folder = factory.createFolder("left", IPageLayout.LEFT, (float) 0.2, editorArea); //$NON-NLS-1$
		String navigator = IVdmUiConstants.NAVIGATOR;

		folder.addView(navigator);

		IFolderLayout bottom = factory.createFolder("bottomRight", // NON-NLS-1
				IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView("org.eclipse.ui.console.ConsoleView");
		bottom.addView("org.overture.ide.plugins.combinatorialtesting.views.TraceTest"); // NON-NLS-1

		IFolderLayout right = factory.createFolder("Right", // NON-NLS-1
				IPageLayout.RIGHT, 0.72f, factory.getEditorArea());

		right.addView("org.overture.ide.plugins.combinatorialtesting.views.TracesView"); // NON-NLS-1

	}

	private void addActionSets()
	{
		factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); // NON-NLS-1
		factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); // NON-NLS-1
		factory.addActionSet("org.eclipse.debug.ui.profileActionSet"); // NON-NLS-1
		factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); // NON-NLS-1
	}

	private void addPerspectiveShortcuts()
	{
	}

	private void addNewWizardShortcuts()
	{
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");// NON-NLS-1
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");// NON-NLS-1
	}

	private void addViewShortcuts()
	{
		factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}

}
