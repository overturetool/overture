/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.ui.OvertureImages;

public class OvertureFileCreationWizard extends NewSourceModuleWizard {

	public OvertureFileCreationWizard() {
		setDefaultPageImageDescriptor(OvertureImages.DESC_WIZBAN_PROJECT_CREATION);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(OvertureWizardMessages.FileCreationWizard_title);
	}

	protected NewSourceModulePage createNewSourceModulePage() {
		return new NewSourceModulePage() {

			protected String getRequiredNature() {
				return OvertureNature.NATURE_ID;
			}

			protected String getPageDescription() {
				return "This wizard creates a new Overture file.";
			}

			protected String getPageTitle() {
				return "Create new Overture file";
			}
		};
	}
}
