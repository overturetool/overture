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
package org.overture.ide.ui.wizard.pages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.overture.ide.core.resources.IVdmProject;

public class LibraryIncludePage extends org.eclipse.jface.wizard.WizardPage
{
	boolean isOo;

	public LibraryIncludePage(String name, boolean ooDialect, IVdmProject prj)
	{
		super(name);
		setTitle(name);
		setMessage("Select the libraries to include");
		isOo = ooDialect;
		this.prj = prj;
	}

	LibrarySelection libSelectionGui;
	private IVdmProject prj;

	public void createControl(Composite parent)
	{
		libSelectionGui = new LibrarySelection(parent, SWT.NONE, isOo);
		try
		{
			LibraryUtil.setSelections(prj, libSelectionGui);
		} catch (CoreException e)
		{
		}
		setControl(libSelectionGui);

	}

	public LibrarySelection getLibrarySelection()
	{
		return libSelectionGui;
	}

}
