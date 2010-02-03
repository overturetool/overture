package org.overture.ide.ui.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LibraryIncludePage extends org.eclipse.jface.wizard.WizardPage
{

	protected LibraryIncludePage(String name) {
		super(name);
		setTitle(name);
		setMessage("Select the libraries to include");
	}

	LibrarySelection libSelectionGui;

	public void createControl(Composite parent)
	{
		libSelectionGui = new LibrarySelection(parent, SWT.EMBEDDED);
		setControl(libSelectionGui);
		

	}
	
	public LibrarySelection getLibrarySelection()
	{
		return libSelectionGui;
	}

}
