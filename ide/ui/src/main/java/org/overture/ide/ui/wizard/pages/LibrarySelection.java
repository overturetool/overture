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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class LibrarySelection extends Composite
{

	private Label label = null;
	private Button checkBoxIo = null;
	private Button checkBoxCsvIo = null;
	private Button checkBoxMath = null;
	private Button checkBoxUtil = null;
	private Button checkBoxVdmUnit = null;
	boolean isOo;

	public LibrarySelection(Composite parent, int style, boolean isOo)
	{
		super(parent, style);
		this.isOo = isOo;
		initialize();
	}

	private void initialize()
	{
		label = new Label(this, SWT.NONE);
		label.setText("Select libraries to include");
		checkBoxIo = new Button(this, SWT.CHECK);
		checkBoxIo.setText("IO");
		checkBoxIo.setToolTipText("Provides basic IO facilities like println, and file IO");
		checkBoxMath = new Button(this, SWT.CHECK);
		checkBoxMath.setText("Math");
		checkBoxMath.setToolTipText("Offers a basic Math interface");
		checkBoxUtil = new Button(this, SWT.CHECK);
		checkBoxUtil.setText("VDM-Util");
		checkBoxUtil.setToolTipText("?");
		checkBoxCsvIo = new Button(this, SWT.CHECK);
		checkBoxCsvIo.setText("CSV");
		checkBoxCsvIo.setToolTipText("Provides IO facilities for CSV files");
		if (isOo)
		{
			checkBoxVdmUnit = new Button(this, SWT.CHECK);
			checkBoxVdmUnit.setText("VDM-Unit");
			checkBoxVdmUnit.setToolTipText("Provides JUnit like facilities for VDM models");
		}
		setSize(new Point(300, 200));
		setLayout(new GridLayout());
	}

	public boolean isIoSelected()
	{
		return checkBoxIo.getSelection();
	}

	public boolean isMathSelected()
	{
		return checkBoxMath.getSelection();
	}

	public boolean isUtilSelected()
	{
		return checkBoxUtil.getSelection();
	}

	public boolean isCsvSelected()
	{
		return checkBoxCsvIo.getSelection();
	}

	public boolean isVdmUnitSelected()
	{
		return checkBoxVdmUnit != null && checkBoxVdmUnit.getSelection();
	}

	public void setIoChecked(boolean b)
	{
		checkBoxIo.setSelection(b);
	}

	public void setMathChecked(boolean exists)
	{
		checkBoxMath.setSelection(exists);
	}

	public void setVdmUtilChecked(boolean exists)
	{
		checkBoxUtil.setSelection(exists);
	}

	public void setCsvChecked(boolean exists)
	{
		checkBoxCsvIo.setSelection(exists);
	}

	public void setVdmUnitChecked(boolean exists)
	{
		if (isOo)
		{
			checkBoxVdmUnit.setSelection(exists);
		}
	}
}
