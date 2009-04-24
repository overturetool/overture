/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;

import org.eclipse.jface.text.IDocument;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.OvertureTextTools;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.SimpleOvertureSourceViewerConfiguration;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;

/**
 * Overture templates preference page
 */
public class OvertureTemplatePreferencePage extends
		ScriptTemplatePreferencePage {
	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage#createSourceViewerConfiguration()
	 */
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleOvertureSourceViewerConfiguration(getTextTools()
				.getColorManager(), getPreferenceStore(), null,
				IOverturePartitions.OVERTURE_PARTITIONING, false);
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage#getTemplateAccess()
	 */
	protected ScriptTemplateAccess getTemplateAccess() {
		return OvertureTemplateAccess.getInstance();
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage#setDocumentParticioner(org.eclipse.jface.text.IDocument)
	 */
	protected void setDocumentParticioner(IDocument document) {
		getTextTools().setupDocumentPartitioner(document,
				IOverturePartitions.OVERTURE_PARTITIONING);
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage#setPreferenceStore()
	 */
	protected void setPreferenceStore() {
		setPreferenceStore(UIPlugin.getDefault().getPreferenceStore());
	}

	private OvertureTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
}
