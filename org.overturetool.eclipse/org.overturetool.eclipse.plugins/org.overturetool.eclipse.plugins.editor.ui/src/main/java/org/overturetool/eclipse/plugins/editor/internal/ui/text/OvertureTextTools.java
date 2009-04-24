/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighter;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;

public class OvertureTextTools extends ScriptTextTools {

	private final class SH extends SemanticHighlighting {

		private String preferenceKey;

		public String getBackgroundPreferenceKey() {
			return bgColor;
		}

		private String bgColor;

		public SH(String editorXmlTagNameColor, String bgColor) {
			this.preferenceKey = editorXmlTagNameColor;
			this.bgColor = bgColor;
		}

		public String getPreferenceKey() {
			return preferenceKey;
		}

	}

	private IPartitionTokenScanner fPartitionScanner;

	private final static String[] LEGAL_CONTENT_TYPES = new String[] {
			IOverturePartitions.OVERTURE_STRING, IOverturePartitions.OVERTURE_COMMENT,
			IOverturePartitions.OVERTURE_DOC };

	public OvertureTextTools(boolean autoDisposeOnDisplayDispose) {
		super(IOverturePartitions.OVERTURE_PARTITIONING, LEGAL_CONTENT_TYPES,
				autoDisposeOnDisplayDispose);
		fPartitionScanner = new OverturePartitionScanner();
	}

	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		return new OvertureSourceViewerConfiguration(getColorManager(),
				preferenceStore, editor, partitioning);
	}

	public IPartitionTokenScanner getPartitionScanner() {
		return fPartitionScanner;
	}

	public SemanticHighlighting[] getSemanticHighlightings() {
		return null; //TODO necessary
//			new SemanticHighlighting[] {
//				new SH(OverturePreferenceConstants.EDITOR_XML_TAG_NAME_COLOR,
//						null),
//				new SH(
//						OverturePreferenceConstants.EDITOR_XML_ATTR_NAME_COLOR,
//						null),
//				new SH(OverturePreferenceConstants.EDITOR_XML_COMMENT_COLOR,
//						null), };
	}

	public ISemanticHighlighter getSemanticPositionUpdater() {
		return new OverturePositionUpdater();
	}

}
