/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;


/**
 * A simple description of a bit of text (technically, a bit of a text buffer)
 * that has a "type" associated with it. For example, for the XML text
 * "&LT;IMG&GT;", the ' <' might form a region of type "open bracket" where as
 * the text "IMG" might form a region of type "tag name".
 * 
 * Note that there are three positions associated with a region, the start,
 * the end, and the end of the text. The end of the region should always be
 * greater than or equal to the end of the text, because the end of the text
 * simply includes any white space that might follow the non-whitespace
 * portion of the region. This whitespace is assumed to be ignorable except
 * for reasons of maintaining it in the original document for formatting,
 * appearance, etc.
 * 
 * Follows the Prime Directives:
 * 
 * getEnd() == getStart() + getLength()
 * 
 * getTextEnd() == getStart() + getTextLength();
 * 
 * Event though 'end' and 'length' are redundant (given start), both methods
 * are provided, since some parsers/implementations favor one or the other for
 * efficiency.
 * 
 * @plannedfor 1.0
 */
public interface ITextRegion {

	/**
	 * Changes length of region. May be less than, equal to, or greater than
	 * zero. It may not, however, cause the length of the region to be less
	 * than or equal to zero, or an illegal argument acception may be thrown
	 * at runtime.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void adjustLength(int i);

	/**
	 * Changes start offset of region. May be less than, equal to, or greater
	 * than zero. It may not, however, cause the offset of the region to be
	 * less than zero, or an illegal argument acception may be thrown at
	 * runtime.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void adjustStart(int i);

	/**
	 * Changes text length of region.
	 * 
	 * May be less than, equal to, or greater than zero. It may not, however,
	 * cause the text length of the region to be greater than the length of a
	 * region, or an illegal argument acception may be thrown at runtime.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void adjustTextLength(int i);

	/**
	 * Makes this regions start, length, and text length equal to the
	 * paremter's start, length, and text length.
	 * 
	 * @param region
	 */
	void equatePositions(ITextRegion region);

	/**
	 * Returns the end offset of this region. Whether is relative to the
	 * document, or another region depends on the subtype.
	 * 
	 * Follows the Prime Directive: getEnd() == getStart() + getLength()
	 * 
	 * @return the end offset of this region.
	 */
	int getEnd();

	/**
	 * Returns the length of the region.
	 * 
	 * Follows the Prime Directive: getEnd() == getStart() + getLength()
	 * 
	 * @return the length of the region.
	 */
	int getLength();

	/**
	 * Returns the start of the region. Whether is relative to the document,
	 * or another region depends on the subtype.
	 * 
	 * Follows the Prime Directive: getEnd() == getStart() + getLength()
	 * 
	 * @return the start of the region.
	 */
	int getStart();

	/**
	 * Returns the end offset of the text of this region.
	 * 
	 * In some implementations, the "end" of the region (accessible via
	 * getEnd()) also contains any and all white space that may or may not be
	 * present after the "token" (read: relevant) part of the region. This
	 * method, getTextEnd(), is specific for the "token" part of the region,
	 * without the whitespace.
	 * 
	 * @return the end offset of the text of this region.
	 */
	int getTextEnd();

	/**
	 * Returns the length of the text of this region.
	 * 
	 * The text length is equal to length if there is no white space at the
	 * end of a region. Otherwise it is smaller than length.
	 * 
	 * @return the length of the text of this region.
	 */
	int getTextLength();

	/**
	 * Returns the type of this region.
	 * 
	 * @see regiontypes, structureddocumentregiontypes
	 * @return the type of this region.
	 */
	String getType();

	/**
	 * Allows the region itself to participate in reparsing process.
	 * 
	 * The region itself can decide if it can determine the appropriate event
	 * to return, based on the requested change. If it can not, this method
	 * must return null, so a "higher level" reparsing process will be given
	 * the oppurtunity to decide. If it returns an Event, that's it, not other
	 * reparsing process will get an oppurtunity to reparse.
	 * 
	 * For use by parsers and reparsers only.
	 * 
	 */

}
