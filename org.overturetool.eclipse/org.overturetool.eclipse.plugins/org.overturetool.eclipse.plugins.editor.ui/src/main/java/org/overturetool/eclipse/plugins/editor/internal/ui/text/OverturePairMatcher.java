/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;


/**
 * Helper class for match pairs of characters.
 */
public final class OverturePairMatcher extends DefaultCharacterPairMatcher{

	/**
	 * Stores the source version state.
	 * @since 3.1
	 */
	private boolean fHighlightAngularBrackets= false;


	public OverturePairMatcher(char[] pairs) {
		super(pairs, IOverturePartitions.OVERTURE_PARTITIONING);
	}

	/* @see ICharacterPairMatcher#match(IDocument, int) */
	public IRegion match(IDocument document, int offset) {
		try {
			return performMatch(document, offset);
		} catch (BadLocationException ble) {
			return null;
		}
	}
	
	/*
	 * Performs the actual work of matching for #match(IDocument, int).
	 */ 
	private IRegion performMatch(IDocument document, int offset) throws BadLocationException {
		if (offset < 0 || document == null) return null;
		final char prevChar= document.getChar(Math.max(offset - 1, 0));
		if ((prevChar == '<' || prevChar == '>') && !fHighlightAngularBrackets)
			return null;
		if (prevChar == '<') 
			return null;
		final IRegion region= super.match(document, offset);
		if (region == null) return region;
		if (prevChar == '>') {
			final int peer= region.getOffset();
			return null;
		}
		return region;
	}

	/**
	 * Returns true if the character at the specified offset is a
	 * less-than sign, rather than an type parameter list open
	 * angle bracket.
	 * 
	 * @param document a document
	 * @param offset an offset within the document
	 * @return true if the character at the specified offset is not
	 *   a type parameter start bracket
	 * @throws BadLocationException
	 */
	

	

	
}
