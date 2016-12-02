package org.overture.ide.ui.refactoringCommand;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class RefactoringCommand  extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = 
		        workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = 
		        window == null ? null : window.getActivePage();

		IEditorPart editorPart = 
		        activePage == null ? null : activePage.getActiveEditor();
		IEditorInput input = 
				editorPart == null ? null : editorPart.getEditorInput();
		IPath path = input instanceof FileEditorInput 
		        ? ((FileEditorInput)input).getPath()
		        : null;
		if (path != null)
		{
		    // Do something with path.
		}
		
		ITextEditor editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		IDocumentProvider provider = editor.getDocumentProvider();
		IDocument document = provider.getDocument(editorPart
		        .getEditorInput());
		ITextSelection textSelection = (ITextSelection) editorPart
		        .getSite().getSelectionProvider().getSelection();
		
		int caretOffset = textSelection.getOffset();
		
		try {
			if(document != null && caretOffset != -1){
				int lineNumber = document.getLineOfOffset(caretOffset); // one off need +1
				IRegion lineInfo = document.getLineInformation(lineNumber);

				int lineOffset = (caretOffset - lineInfo.getOffset()) + 1;
				System.out.println("line nr: " + (lineNumber + 1) + " offset: " + lineOffset);
				run(path.toString(), lineNumber, lineOffset);
			} else{
				System.out.println("error in line");
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//	    NewLambdaJavaFunctionProjectWizard newWizard = new NewLambdaJavaFunctionProjectWizard();
//	    newWizard.init(PlatformUI.getWorkbench(), null);
//	    WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), newWizard);
//	    return dialog.open();
		
		return null;
	}
	
//	@SuppressWarnings("restriction")
//	public Object Openwizard(ExecutionEvent event) throws ExecutionException {
//	    NewLambdaJavaFunctionProjectWizard newWizard = new NewLambdaJavaFunctionProjectWizard();
//	    newWizard.init(PlatformUI.getWorkbench(), null);
//	    WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), newWizard);
//	    return dialog.open();
//	}
	

	public void run(String filePath, int lineNumber, int lineOffset) {
		CaptureRenameInformationWizard wizard = new CaptureRenameInformationWizard(filePath, lineNumber, lineOffset);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.create();
		dialog.open();
	}
	 
}
