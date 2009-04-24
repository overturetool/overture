/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.formatting;

import java.util.Map;

import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;


public class OldCodeFormatter extends CodeFormatter {

	Map options;

	public OldCodeFormatter(Map options) {
		this.options = options;
	}

	public TextEdit format(int kind, String source, int offset, int length,
			StringBuffer computeIndentation, String lineSeparator) {
		String newText = formatString(
				source.substring(offset, offset + length), computeIndentation);
		return new ReplaceEdit(offset, length, newText);
	}

	public String formatString(String substring, StringBuffer computeIndentation) {
		//TODO: add parser
		return null;
	}

}
