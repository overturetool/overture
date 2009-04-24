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



import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;


public interface BlockTokenizer {

	void addBlockMarker(BlockMarker marker);

	void beginBlockMarkerScan(String newTagName, String context);

	void beginBlockTagScan(String newTagName);

	List getBlockMarkers();

	ITextRegion getNextToken() throws IOException;

	int getOffset();

	boolean isEOF();

	BlockTokenizer newInstance();

	void removeBlockMarker(BlockMarker marker);

	void removeBlockMarker(String tagname);

	void reset(char[] charArray);

	void reset(char[] charArray, int newOffset);

	void reset(InputStream in);

	void reset(InputStream in, int newOffset);

	void reset(Reader in);

	void reset(Reader in, int newOffset);
}
