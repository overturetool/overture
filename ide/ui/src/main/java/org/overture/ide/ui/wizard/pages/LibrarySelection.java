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

	public LibrarySelection(Composite parent, int style) {
		super(parent, style);
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
		checkBoxUtil.setText("Util");
		checkBoxUtil.setToolTipText("?");
		checkBoxCsvIo = new Button(this, SWT.CHECK);
		checkBoxCsvIo.setText("CSV");
		checkBoxCsvIo.setToolTipText("Provides IO facilities for CSV files");
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
}
