package org.overture.ide.ui.refactoringCommand;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RenameRefactoringWizard extends WizardPage {
     
	Text newNameText;
     
    protected RenameRefactoringWizard(String pageName) {
		super(pageName);
		setTitle("Rename Information");
		setDescription("Please enter your rename information");
    }
    
    public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		setControl(composite);
		new Label(composite,SWT.NONE).setText("New Name");
		newNameText = new Text(composite,SWT.NONE);
    }
    
    public String getNewNameText(){
    	return newNameText.getText();
    }
    
}