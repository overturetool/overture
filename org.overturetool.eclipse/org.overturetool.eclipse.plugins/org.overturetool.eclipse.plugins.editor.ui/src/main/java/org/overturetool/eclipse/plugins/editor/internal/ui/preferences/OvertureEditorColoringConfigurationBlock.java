/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.preferences;

import java.io.InputStream;

import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;

import org.eclipse.dltk.ui.preferences.AbstractScriptEditorColoringConfigurationBlock;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overturetool.eclipse.plugins.editor.internal.ui.editor.OvertureDocumentSetupParticipant;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.SimpleOvertureSourceViewerConfiguration;
import org.overturetool.eclipse.plugins.editor.ui.OverturePreferenceConstants;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;

public class OvertureEditorColoringConfigurationBlock extends
		AbstractScriptEditorColoringConfigurationBlock implements
		IPreferenceConfigurationBlock {

	private static final String PREVIEW_FILE_NAME = "PreviewFile.txt";

	private static final String[][] fSyntaxColorListModel = new String[][] {
			{
					PreferencesMessages.DLTKEditorPreferencePage_singleLineComment,
					OverturePreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR,
					sCommentsCategory 
			},
			{ 
				PreferencesMessages.DLTKEditorPreferencePage_CommentTaskTags,
				OverturePreferenceConstants.COMMENT_TASK_TAGS,
				sCommentsCategory },
			{
				PreferencesMessages.DLTKEditorPreferencePage_keywords,
				OverturePreferenceConstants.EDITOR_KEYWORD_COLOR,
				sCoreCategory 
			},
			{
				PreferencesMessages.DLTKEditorPreferencePage_strings,
				OverturePreferenceConstants.EDITOR_STRING_COLOR,
				sCoreCategory 
			},

			{
				PreferencesMessages.DLTKEditorPreferencePage_numbers,
				OverturePreferenceConstants.EDITOR_NUMBER_COLOR,
				sCoreCategory 
			},
			{
					PreferencesMessages.DLTKEditorPreferencePage_function_colors,
					OverturePreferenceConstants.EDITOR_FUNCTION_DEFINITION_COLOR,
					sCoreCategory 
			},
			{
				"Operators",
				OverturePreferenceConstants.OVERTURE_OPERATOR,
				sCoreCategory 
			},
			{
				"Functions",
				OverturePreferenceConstants.OVERTURE_FUNCTION,
				sCoreCategory 
			},
			{
				"Constants",
				OverturePreferenceConstants.OVERTURE_CONSTANT,
				sCoreCategory 
			},
			{
				"Type",
				OverturePreferenceConstants.OVERTURE_TYPE,
				sCoreCategory 
			},
			{
				"Predicate",
				OverturePreferenceConstants.OVERTURE_PREDICATE,
				sCoreCategory 
			}
	};

	public OvertureEditorColoringConfigurationBlock(
			OverlayPreferenceStore store) {
		super(store);
	}

	protected String[] getCategories() {
		return new String[] { sCoreCategory, sDocumentationCategory, sCommentsCategory};
	}

	protected String[][] getSyntaxColorListModel() {
		return fSyntaxColorListModel;
	}

	protected ProjectionViewer createPreviewViewer(Composite parent,
			IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
		return new ScriptSourceViewer(parent, verticalRuler, overviewRuler,
				showAnnotationsOverview, styles, store);
	}

	protected ScriptSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, boolean configureFormatter) {
		return new SimpleOvertureSourceViewerConfiguration(
				colorManager,
				preferenceStore,
				editor,
				IOverturePartitions.OVERTURE_PARTITIONING,
				configureFormatter);
	}

	protected void setDocumentPartitioning(IDocument document) {
		OvertureDocumentSetupParticipant participant = new OvertureDocumentSetupParticipant();
		participant.setup(document);
	}

	protected InputStream getPreviewContentReader() {
		return getClass().getResourceAsStream(PREVIEW_FILE_NAME);
	}
}
