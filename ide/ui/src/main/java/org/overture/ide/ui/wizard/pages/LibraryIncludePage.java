package org.overture.ide.ui.wizard.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LibraryIncludePage extends org.eclipse.jface.wizard.WizardPage
{
	boolean isOo;

	public LibraryIncludePage(String name, boolean ooDialect)
	{
		super(name);
		setTitle(name);
		setMessage("Select the libraries to include");
		isOo = ooDialect;
	}

	LibrarySelection libSelectionGui;

	public void createControl(Composite parent)
	{
		libSelectionGui = new LibrarySelection(parent, SWT.NONE, isOo);
		setControl(libSelectionGui);

	}

	public LibrarySelection getLibrarySelection()
	{
		return libSelectionGui;
	}

}
