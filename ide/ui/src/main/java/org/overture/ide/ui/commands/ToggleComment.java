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
package org.overture.ide.ui.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmEditor;

public class ToggleComment extends AbstractHandler{

	private VdmEditor editor;
	private ITextOperationTarget fOperationTarget;
	static private String fDocumentPartitioning;
	static private Map<String, String[]> fPrefixesMap;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		
		if(editorPart == null)
			return null;
		
		if(!(editorPart instanceof VdmEditor))
			return null;
		
		editor = (VdmEditor) editorPart;
			
		fOperationTarget = (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
		
		
		
		
		
		this.configure((ISourceViewer) fOperationTarget,editor.getNewSourceViewerConfiguration());
		
		
		
		if (!validateEditorInputState())
			return null;
		
		final int operationCode;
		if (isSelectionCommented(editor.getSelectionProvider().getSelection()))
			operationCode= ITextOperationTarget.STRIP_PREFIX;
		else
			operationCode= ITextOperationTarget.PREFIX;

		Shell shell= editor.getSite().getShell();
		if (!fOperationTarget.canDoOperation(operationCode)) {
			if (shell != null)
				MessageDialog.openError(shell, "ToggleComment_error_title", "ToggleComment_error_message");
			return null;
		}

		Display display= null;
		if (shell != null && !shell.isDisposed())
			display= shell.getDisplay();

		BusyIndicator.showWhile(display, new Runnable() {
			public void run() {
				fOperationTarget.doOperation(operationCode);
			}
		});
		
		return null;
	}

	
	/**
	 * Is the given selection single-line commented?
	 *
	 * @param selection Selection to check
	 * @return <code>true</code> iff all selected lines are commented
	 */
	private boolean isSelectionCommented(ISelection selection) {
		if (!(selection instanceof ITextSelection))
			return false;

		ITextSelection textSelection= (ITextSelection) selection;
		if (textSelection.getStartLine() < 0 || textSelection.getEndLine() < 0)
			return false;

		IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());

		try {

			IRegion block= getTextBlockFromSelection(textSelection, document);
			ITypedRegion[] regions= TextUtilities.computePartitioning(document, fDocumentPartitioning, block.getOffset(), block.getLength(), false);

			int[] lines= new int[regions.length * 2]; // [startline, endline, startline, endline, ...]
			for (int i= 0, j= 0; i < regions.length; i++, j+= 2) {
				// start line of region
				lines[j]= getFirstCompleteLineOfRegion(regions[i], document);
				// end line of region
				int length= regions[i].getLength();
				int offset= regions[i].getOffset() + length;
				if (length > 0)
					offset--;
				lines[j + 1]= (lines[j] == -1 ? -1 : document.getLineOfOffset(offset));
			}

			// Perform the check
			for (int i= 0, j= 0; i < regions.length; i++, j += 2) {
				String[] prefixes= (String[]) fPrefixesMap.get(regions[i].getType());
				if (prefixes != null && prefixes.length > 0 && lines[j] >= 0 && lines[j + 1] >= 0)
					if (!isBlockCommented(lines[j], lines[j + 1], prefixes, document))
						return false;
			}

			return true;

		} catch (BadLocationException x) {
			// should not happen
			VdmUIPlugin.log(x);
		}

		return false;
	}
	
	/**
	 * Checks and validates the editor's modifiable state. Returns <code>true</code> if an action
	 * can proceed modifying the editor's input, <code>false</code> if it should not.
	 *
	 * <p>If the editor implements <code>ITextEditorExtension2</code>,
	 * this method returns {@link ITextEditorExtension2#validateEditorInputState()};<br> else if the editor
	 * implements <code>ITextEditorExtension</code>, it returns {@link ITextEditorExtension#isEditorInputReadOnly()};<br>
	 * else, {@link ITextEditor#isEditable()} is returned, or <code>false</code> if the editor is <code>null</code>.</p>
	 *
	 * <p>There is only a difference to {@link #canModifyEditor()} if the editor implements
	 * <code>ITextEditorExtension2</code>.</p>
	 *
	 * @return <code>true</code> if a modifying action can proceed to modify the underlying document, <code>false</code> otherwise
	 * @since 3.0
	 */
	protected boolean validateEditorInputState() {
		ITextEditor editor= this.editor;
		if (editor instanceof ITextEditorExtension2)
			return ((ITextEditorExtension2) editor).validateEditorInputState();
		else if (editor instanceof ITextEditorExtension)
			return !((ITextEditorExtension) editor).isEditorInputReadOnly();
		else if (editor != null)
			return editor.isEditable();
		else
			return false;
	}
	
	/**
	 * Creates a region describing the text block (something that starts at
	 * the beginning of a line) completely containing the current selection.
	 *
	 * @param selection The selection to use
	 * @param document The document
	 * @return the region describing the text block comprising the given selection
	 */
	private IRegion getTextBlockFromSelection(ITextSelection selection, IDocument document) {

		try {
			IRegion line= document.getLineInformationOfOffset(selection.getOffset());
			int length= selection.getLength() == 0 ? line.getLength() : selection.getLength() + (selection.getOffset() - line.getOffset());
			return new Region(line.getOffset(), length);

		} catch (BadLocationException x) {
			// should not happen
			VdmUIPlugin.log(x);
		}

		return null;
	}
	
	/**
	 * Returns the index of the first line whose start offset is in the given text range.
	 *
	 * @param region the text range in characters where to find the line
	 * @param document The document
	 * @return the first line whose start index is in the given range, -1 if there is no such line
	 */
	private int getFirstCompleteLineOfRegion(IRegion region, IDocument document) {

		try {

			int startLine= document.getLineOfOffset(region.getOffset());

			int offset= document.getLineOffset(startLine);
			if (offset >= region.getOffset())
				return startLine;

			offset= document.getLineOffset(startLine + 1);
			return (offset > region.getOffset() + region.getLength() ? -1 : startLine + 1);

		} catch (BadLocationException x) {
			// should not happen
			VdmUIPlugin.log(x);
		}

		return -1;
	}
	
	/**
	 * Determines whether each line is prefixed by one of the prefixes.
	 *
	 * @param startLine Start line in document
	 * @param endLine End line in document
	 * @param prefixes Possible comment prefixes
	 * @param document The document
	 * @return <code>true</code> iff each line from <code>startLine</code>
	 *             to and including <code>endLine</code> is prepended by one
	 *             of the <code>prefixes</code>, ignoring whitespace at the
	 *             begin of line
	 */
	private boolean isBlockCommented(int startLine, int endLine, String[] prefixes, IDocument document) {

		try {

			// check for occurrences of prefixes in the given lines
			for (int i= startLine; i <= endLine; i++) {

				IRegion line= document.getLineInformation(i);
				String text= document.get(line.getOffset(), line.getLength());

				int[] found= TextUtilities.indexOf(prefixes, text, 0);

				if (found[0] == -1)
					// found a line which is not commented
					return false;

				String s= document.get(line.getOffset(), found[0]);
				s= s.trim();
				if (s.length() != 0)
					// found a line which is not commented
					return false;

			}

			return true;

		} catch (BadLocationException x) {
			// should not happen
			VdmUIPlugin.log(x);
		}

		return false;
	}
	
	public void configure(ISourceViewer sourceViewer, SourceViewerConfiguration configuration) {
		if(fPrefixesMap != null && fDocumentPartitioning != null)
			return;
		
		fPrefixesMap= null;

		String[] types= configuration.getConfiguredContentTypes(sourceViewer);
		Map<String, String[]> prefixesMap= new HashMap<String, String[]>(types.length);
		for (int i= 0; i < types.length; i++) {
			String type= types[i];
			String[] prefixes= configuration.getDefaultPrefixes(sourceViewer, type);
			if (prefixes != null && prefixes.length > 0) {
				int emptyPrefixes= 0;
				for (int j= 0; j < prefixes.length; j++)
					if (prefixes[j].length() == 0)
						emptyPrefixes++;

				if (emptyPrefixes > 0) {
					String[] nonemptyPrefixes= new String[prefixes.length - emptyPrefixes];
					for (int j= 0, k= 0; j < prefixes.length; j++) {
						String prefix= prefixes[j];
						if (prefix.length() != 0) {
							nonemptyPrefixes[k]= prefix;
							k++;
						}
					}
					prefixes= nonemptyPrefixes;
				}

				prefixesMap.put(type, prefixes);
			}
		}
		fDocumentPartitioning= configuration.getConfiguredDocumentPartitioning(sourceViewer);
		fPrefixesMap= prefixesMap;
	}
}
