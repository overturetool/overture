package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class OvertureGenerateCodeCppWizardPage extends WizardPage{
	Combo combo;
	IProject[] iprojects;
	Text textProjectPath;
	FileDialog fileDialog;
	Button btnSavePath;
	
	protected OvertureGenerateCodeCppWizardPage(String pageName) {
		super(pageName);
		setTitle("Generate C++ code");
        setDescription("Please choose the wanted options");
	}

	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);
        setControl(composite);
        // combo
        new Label(composite,SWT.NONE).setText("Select project: ");
        combo = new Combo(composite, SWT.NONE);
        
        // project path 
        new Label(composite,SWT.NONE).setText("Save Path for the project");
        textProjectPath = new Text(composite,SWT.BORDER);
        
        // btn save
        btnSavePath = new Button(composite, SWT.PUSH);
        btnSavePath.setText("Browse...");
        btnSavePath.addSelectionListener(new SelectionListener()
        {

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				fileDialog = new FileDialog(composite.getShell(), SWT.SAVE);
				fileDialog.setFileName("project");
				fileDialog.open();
				textProjectPath.setText(
						fileDialog.getFilterPath() +
						System.getProperty("file.separator") +
						fileDialog.getFileName());
			}
        });
        
        
        IWorkspaceRoot iworkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		iprojects = iworkspaceRoot.getProjects();
		for (IProject project : iprojects) {
			combo.add(project.getName());
		}
		combo.select(0);
	}
	
	

}
