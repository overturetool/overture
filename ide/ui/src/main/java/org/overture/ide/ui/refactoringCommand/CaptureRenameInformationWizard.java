package org.overture.ide.ui.refactoringCommand;

import org.eclipse.jface.wizard.Wizard;

public class CaptureRenameInformationWizard extends Wizard {
	private RenameRefactoringWizard renameInfoPage;
	private String filePath;
	private int lineNumber;
	private int lineOffset;
	//private RefactoringMain refactoringMain;
	public CaptureRenameInformationWizard(String filePath, int lineNumber, int lineOffset){
		
		this.filePath = filePath;
		this.lineNumber = lineNumber;
		this.lineOffset = lineOffset;
	}
	
    public void addPages() {
		renameInfoPage = new RenameRefactoringWizard("Rename Information Page");
		addPage(renameInfoPage);
    }
     
    public boolean performFinish() {
    	
    	String[] parameters = new String[4];
    	parameters[0] = "-print";
    	parameters[1] = "-sl";
    	
    	StringBuilder renameInput = new StringBuilder();
    	renameInput.append("-rename;");
    	renameInput.append(lineNumber + ";");
    	renameInput.append(lineOffset + ";");
    	String newName = renameInfoPage.getNewNameText();
    	renameInput.append(newName);
    	parameters[2] = renameInput.toString();
    	//-rename;135;16;period
		
    	parameters[3] = filePath;
    	
    	return true;
    }
}