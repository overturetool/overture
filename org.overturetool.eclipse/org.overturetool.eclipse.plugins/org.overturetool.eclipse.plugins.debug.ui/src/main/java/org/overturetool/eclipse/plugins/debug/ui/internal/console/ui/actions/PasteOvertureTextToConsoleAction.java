package org.overturetool.eclipse.plugins.debug.ui.internal.console.ui.actions;

import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.ScriptConsoleManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overturetool.eclipse.plugins.debug.ui.internal.console.ui.OvertureConsole;


public class PasteOvertureTextToConsoleAction implements IEditorActionDelegate {

	private ISelection selection;

	private IEditorPart targetEditor;

	protected IDocument getDocument() {
		if (!(targetEditor instanceof ITextEditor))
			return null;

		ITextEditor editor = (ITextEditor) targetEditor;
		IDocumentProvider dp = editor.getDocumentProvider();
		return dp.getDocument(editor.getEditorInput());
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = targetEditor;
	}

	public void run(IAction action) {
		ScriptConsoleManager manager = ScriptConsoleManager.getInstance();

		ScriptConsole console = manager
				.getActiveScriptConsole(OvertureConsole.CONSOLE_TYPE);

		if (console == null) {
			return;
		}

		if (selection instanceof ITextSelection) {
			String text = ((ITextSelection) selection).getText();
			console.getInput().insertText(text);
		}		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}
