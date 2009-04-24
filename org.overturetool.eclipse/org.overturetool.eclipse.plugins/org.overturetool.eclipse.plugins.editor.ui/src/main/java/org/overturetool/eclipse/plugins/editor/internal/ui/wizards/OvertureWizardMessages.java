/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import org.eclipse.osgi.util.NLS;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;

public class OvertureWizardMessages {

	private static final String BUNDLE_NAME= EditorCoreUIConstants.OVERTURE_WIZARD_MESSAGES_BUNDLE_NAME;//"org.overturetool.internal.ui.wizards.OvertureWizardMessages";//$NON-NLS-1$

	public static String FileCreationWizard_title;

	public static String ProjectCreationWizard_title;
	public static String ProjectCreationWizardFirstPage_title;
	public static String ProjectCreationWizardFirstPage_description;
		
	static {
		NLS.initializeMessages(BUNDLE_NAME, OvertureWizardMessages.class);
	}
}
