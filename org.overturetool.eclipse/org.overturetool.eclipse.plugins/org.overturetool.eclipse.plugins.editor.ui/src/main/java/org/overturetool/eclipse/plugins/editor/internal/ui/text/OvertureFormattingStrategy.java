/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.internal.ui.formatting.CodeFormatter;
import org.overturetool.eclipse.plugins.editor.internal.ui.formatting.CodeFormatterUtil;

/**
 * Formatting strategy for java source code.
 * 
 * @since 3.0
 */
public class OvertureFormattingStrategy extends
		ContextBasedFormattingStrategy {

	/** Documents to be formatted by this strategy */
	private final LinkedList fDocuments = new LinkedList();
	/** Partitions to be formatted by this strategy */
	private final LinkedList fPartitions = new LinkedList();

	/**
	 * Creates a new java formatting strategy.
	 */
	public OvertureFormattingStrategy() {
		super();
	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#format()
	 */
	public void format() {
		super.format();

		final IDocument document = (IDocument) fDocuments.removeFirst();
		final TypedPosition partition = (TypedPosition) fPartitions
				.removeFirst();

		if (document != null && partition != null) {
			Map partitioners = null;
			try {
				int offset = partition.getOffset();
				IRegion line= document.getLineInformationOfOffset(offset);
				int lineOffset= line.getOffset();
								
				StringBuffer computeIndentation = new StringBuffer();
				for (int a=offset;a<(document.getLength());a++){
					char c=document.getChar(a);
					if (Character.isISOControl(c))break;
					if (Character.isWhitespace(c))computeIndentation.append(c);					
					else break;
				}
				final TextEdit edit = CodeFormatterUtil.format2(
						CodeFormatter.K_JAVA_SCRIPT, document.get(), offset, partition.getLength(),
						computeIndentation, TextUtilities
								.getDefaultLineDelimiter(document),
						getPreferences());
				if (edit != null) {
					if (edit.getChildrenSize() > 20)
						partitioners = TextUtilities
								.removeDocumentPartitioners(document);

					edit.apply(document);
				}

			} catch (MalformedTreeException exception) {
				UIPlugin.log(exception);
			} catch (BadLocationException exception) {
				// Can only happen on concurrent document modification - log and
				// bail out
				UIPlugin.log(exception);
			} finally {
				if (partitioners != null)
					TextUtilities.addDocumentPartitioners(document,
							partitioners);
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStarts(org.eclipse.jface.text.formatter.IFormattingContext)
	 */
	public void formatterStarts(final IFormattingContext context) {
		super.formatterStarts(context);

		fPartitions.addLast(context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
		fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));

	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStops()
	 */
	public void formatterStops() {
		super.formatterStops();

		fPartitions.clear();
		fDocuments.clear();
	}
}
