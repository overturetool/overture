package org.overture.ide.ui.wizard.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LibraryIncludePage extends org.eclipse.jface.wizard.WizardPage
{

	public LibraryIncludePage(String name) {
		super(name);
		setTitle(name);
		setMessage("Select the libraries to include");
	}

	LibrarySelection libSelectionGui;

	public void createControl(Composite parent)
	{
		libSelectionGui = new LibrarySelection(parent, SWT.NONE);
		setControl(libSelectionGui);
		

	}
	
	public LibrarySelection getLibrarySelection()
	{
		return libSelectionGui;
	}

}
