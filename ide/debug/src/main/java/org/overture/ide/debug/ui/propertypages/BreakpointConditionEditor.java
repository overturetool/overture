/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui.propertypages;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.overture.ast.lex.Dialect;
import org.overture.ide.debug.core.model.IVdmLineBreakpoint;
import org.overture.ide.ui.editor.core.VdmSourceViewer;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.Console;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;

public class BreakpointConditionEditor
{
	private VdmSourceViewer fViewer;
	// private IContentAssistProcessor fCompletionProcessor;
	private String fOldValue;
	private String fErrorMessage;
	private VdmLineBreakpointPropertyPage fPage;
	private IVdmLineBreakpoint fBreakpoint;
	private IHandlerService fHandlerService;
	private IHandler fHandler;
	private IHandlerActivation fActivation;
	private IDocumentListener fDocumentListener;
	private String fSyntaxErrorMessage;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent to add this widget to
	 * @param page
	 *            the page that is associated with this widget
	 */
	public BreakpointConditionEditor(Composite parent,
			VdmLineBreakpointPropertyPage page)
	{
		fPage = page;
		fBreakpoint = (IVdmLineBreakpoint) fPage.getBreakpoint();
		String condition = new String();
		try
		{
			condition = fBreakpoint.getExpression();
			fErrorMessage = "Enter a condition";

			fOldValue = ""; //$NON-NLS-1$

			fViewer = new VdmSourceViewer(parent, null, null, false, SWT.BORDER
					| SWT.V_SCROLL | SWT.H_SCROLL | SWT.LEFT_TO_RIGHT);
			fViewer.setInput(parent);
			IDocument document = new Document();
			// JDIDebugUIPlugin.getDefault().getJavaTextTools().setupJavaDocumentPartitioner(document,
			// IJavaPartitions.JAVA_PARTITIONING);
			// we can only do code assist if there is an associated type
			// IJavaDebugContentAssistContext context = null;
			// IType type = BreakpointUtils.getType(fBreakpoint);
			// if (type == null) {
			// context = new TypeContext(null, -1);
			// }
			// else {
			// try {
			// String source = null;
			// ICompilationUnit compilationUnit = type.getCompilationUnit();
			// if (compilationUnit != null && compilationUnit.getJavaProject().getProject().exists()) {
			// source = compilationUnit.getSource();
			// }
			// else {
			// IClassFile classFile = type.getClassFile();
			// if (classFile != null) {
			// source = classFile.getSource();
			// }
			// }
			// int lineNumber = fBreakpoint.getMarker().getAttribute(IMarker.LINE_NUMBER, -1);
			// int position= -1;
			// if (source != null && lineNumber != -1) {
			// try {
			// position = new Document(source).getLineOffset(lineNumber - 1);
			// }
			// catch (BadLocationException e) {JDIDebugUIPlugin.log(e);}
			// }
			// context = new TypeContext(type, position);
			// }
			// catch (CoreException e) {JDIDebugUIPlugin.log(e);}
			// }
			// fCompletionProcessor = new JavaDebugContentAssistProcessor(context);
			// fViewer.configure(new DisplayViewerConfiguration() {
			// public IContentAssistProcessor getContentAssistantProcessor() {
			// return fCompletionProcessor;
			// }
			// });
			fViewer.setEditable(true);
			// if we don't check upstream tracing can throw assertion exceptions see bug 181914
			document.set(condition == null ? "" : condition); //$NON-NLS-1$
			fViewer.setDocument(document);
			fViewer.setUndoManager(new TextViewerUndoManager(10));
			fViewer.getUndoManager().connect(fViewer);
			fDocumentListener = new IDocumentListener()
			{
				public void documentAboutToBeChanged(DocumentEvent event)
				{
				}

				public void documentChanged(DocumentEvent event)
				{
					valueChanged();
				}
			};
			fViewer.getDocument().addDocumentListener(fDocumentListener);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = fPage.convertHeightInCharsToPixels(10);
			gd.widthHint = fPage.convertWidthInCharsToPixels(40);
			fViewer.getControl().setLayoutData(gd);
			fHandler = new AbstractHandler()
			{
				public Object execute(ExecutionEvent event)
						throws org.eclipse.core.commands.ExecutionException
				{
					fViewer.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
					return null;
				}
			};
			fHandlerService = (IHandlerService) PlatformUI.getWorkbench().getAdapter(IHandlerService.class);

			fViewer.getControl().addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
					activateContentAssist();
				}

				public void focusLost(FocusEvent e)
				{
					deactivateContentAssist();
				}
			});
		} catch (CoreException exception)
		{
		}
	}

	/**
	 * Returns the condition defined in the source viewer.
	 * 
	 * @return the contents of this condition editor
	 */
	public String getCondition()
	{
		return fViewer.getDocument().get();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#refreshValidState()
	 */
	protected void refreshValidState()
	{
		if (!fViewer.isEditable())
		{
			fPage.removeErrorMessage(fErrorMessage);
			fPage.removeErrorMessage(fSyntaxErrorMessage);
		} else
		{
			String text = fViewer.getDocument().get();
			if (!(text != null && text.trim().length() > 0))
			{
				fPage.addErrorMessage(fErrorMessage);
				fPage.removeErrorMessage(fSyntaxErrorMessage);
			} else
			{
				fPage.removeErrorMessage(fErrorMessage);
				if (validateSyntax())
				{
					fPage.removeErrorMessage(fSyntaxErrorMessage);
				} else
				{
					fPage.addErrorMessage(fSyntaxErrorMessage);
				}

			}

		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean, org.eclipse.swt.widgets.Composite)
	 */
	public void setEnabled(boolean enabled)
	{
		fViewer.setEditable(enabled);
		fViewer.getTextWidget().setEnabled(enabled);
		if (enabled)
		{
			Color color = fViewer.getControl().getDisplay().getSystemColor(SWT.COLOR_WHITE);
			fViewer.getTextWidget().setBackground(color);
			fViewer.getTextWidget().setFocus();
		} else
		{
			Color color = fViewer.getControl().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			fViewer.getTextWidget().setBackground(color);
		}
		valueChanged();
	}

	/**
	 * Handle that the value changed
	 */
	protected void valueChanged()
	{
		String newValue = fViewer.getDocument().get();
		if (!newValue.equals(fOldValue))
		{
			fOldValue = newValue;

		}
		refreshValidState();
	}

	private boolean validateSyntax()
	{
		LexTokenReader ltr;
		ltr = new LexTokenReader(fOldValue, Dialect.VDM_RT, Console.charset);

		ExpressionReader reader = new ExpressionReader(ltr);

		try
		{
			reader.readExpression();
		} catch (ParserException e)
		{
			this.fSyntaxErrorMessage = e.getMessage();
			return false;

		} catch (LexException e)
		{
			this.fSyntaxErrorMessage = e.getMessage();
			return false;

		}
		return true;

	}

	/**
	 * Dispose of the handlers, etc
	 */
	public void dispose()
	{
		deactivateContentAssist();

		fViewer.getDocument().removeDocumentListener(fDocumentListener);
		// fViewer.dispose();
	}

	private void activateContentAssist()
	{
		fActivation = fHandlerService.activateHandler(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, fHandler);
	}

	private void deactivateContentAssist()
	{
		if (fActivation != null)
		{
			fHandlerService.deactivateHandler(fActivation);
			fActivation = null;
		}
	}
}
