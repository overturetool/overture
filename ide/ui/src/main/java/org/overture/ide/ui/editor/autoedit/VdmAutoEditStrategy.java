/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.editor.autoedit;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmAutoEditStrategy implements IAutoEditStrategy {

	public void customizeDocumentCommand(IDocument document,
			DocumentCommand command) {

		if(command.text.equals("\""))
		{
			command.text = "\"\"";
			configureCommand(command);
		}
		else if(command.text.equals("'"))
		{
			command.text = "''";
			configureCommand(command);
		}
		else if(command.text.equals("{"))
		{			
			int line;
			try {
				line = document.getLineOffset(command.offset);
				String ident = getIndentOfLine(document,line);
				command.text = "{" + "\r\n" + ident + "}";
			} catch (BadLocationException e) {
				VdmUIPlugin.printe(e);
			}
			
		}
		
	}

	private String getIndentOfLine(IDocument document, int line) throws BadLocationException {
		if(line > -1)
		{
			int start = document.getLineOffset(line);
			int end = start + document.getLineLength(line) - 1;
			int whiteend = findEndOfWhiteSpace(document,start,end);
			return document.get(start,whiteend - start);			
		}
		else
		{
			return "";
		}
	}

	private int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException{
		while(offset < end)
		{
			char c = document.getChar(offset);
			if(c != ' ' & c != '\t'){
				return offset;
			}
			offset++;
		}
		return end;
	}

	private void configureCommand(DocumentCommand command) {
		command.caretOffset = command.offset + 1;
		command.shiftsCaret = false;		
	}

}
