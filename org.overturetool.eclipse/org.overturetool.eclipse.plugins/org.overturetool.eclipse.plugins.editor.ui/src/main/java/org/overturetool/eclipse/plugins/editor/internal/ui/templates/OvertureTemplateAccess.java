/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;

/**
 * Provides access to Overture templates
 */
public class OvertureTemplateAccess extends ScriptTemplateAccess {
	// Template
	private static final String CUSTOM_TEMPLATES_KEY = EditorCoreUIConstants.CUSTOM_TEMPLATES_KEY;//"org.overture.Templates";

	private static OvertureTemplateAccess instance;

	public static OvertureTemplateAccess getInstance() {
		if (instance == null) {
			instance = new OvertureTemplateAccess();
		}

		return instance;
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return UIPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getContextTypeId()
	 */
	protected String getContextTypeId() {
		return OvertureUniversalTemplateContextType.CONTEXT_TYPE_ID;
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getCustomTemplatesKey()
	 */
	protected String getCustomTemplatesKey() {
		return CUSTOM_TEMPLATES_KEY;
	}
}
