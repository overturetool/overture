/*
 * #%~
 * org.overture.ide.help
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
package org.overture.ide.help.wizard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SelectDialectWizardPage extends WizardPage
{
	public interface DialectSelectedHandler
	{
		public void dialectSelected(String dialect);
	}

	private DialectSelectedHandler handler;
	private Button vdmslbuttonRadio;
	private Button vdmppbuttonRadio;
	private Button vdmrtbuttonRadio;
	private Set<Button> radioButtons;

	protected SelectDialectWizardPage(DialectSelectedHandler handler)
	{
		super("Dialect Selectio");
		this.handler = handler;
	}

	@Override
	public void createControl(Composite parent)
	{
		SelectionListener selectionChangeListner = new SelectionListener()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				for (Button b : new Button[] { vdmslbuttonRadio,
						vdmppbuttonRadio, vdmrtbuttonRadio })
				{
					if (radioButtons.contains(e.widget) && !e.widget.equals(b))
					{
						b.setSelection(false);
					} else
					{
						b.setSelection(true);
					}
				}
				if(e.widget instanceof Button &&((Button)e.widget).getSelection())
				{
					handler.dialectSelected(((Button)e.widget).getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{

			}
		};

		Composite c = new Composite(parent, SWT.NONE);
		Label label = new Label(c, SWT.NONE);
		label.setText("Select dialect");
		vdmslbuttonRadio = new Button(c, SWT.RADIO);
		vdmslbuttonRadio.setText("VDM-SL");
		vdmslbuttonRadio.addSelectionListener(selectionChangeListner);
		vdmslbuttonRadio.setSelection(true);

		vdmppbuttonRadio = new Button(c, SWT.RADIO);
		vdmppbuttonRadio.setText("VDM-PP");
		vdmppbuttonRadio.addSelectionListener(selectionChangeListner);

		vdmrtbuttonRadio = new Button(c, SWT.RADIO);
		vdmrtbuttonRadio.setText("VDM-RT");
		vdmrtbuttonRadio.addSelectionListener(selectionChangeListner);
		radioButtons = new HashSet<Button>(Arrays.asList(new Button[] {
				vdmslbuttonRadio, vdmppbuttonRadio, vdmrtbuttonRadio }));
		c.setSize(new Point(300, 200));
		c.setLayout(new GridLayout());
		setControl(c);
	}

	@Override
	public String getTitle()
	{
		return "Select Dialect";
	}

	public String getSelectedDialect()
	{
		for (Button b : this.radioButtons)
		{
			if (b.getSelection())
			{
				return b.getText();
			}
		}
		return null;
	}

	@Override
	public boolean isPageComplete()
	{
		if (handler != null)
		{
			handler.dialectSelected(getSelectedDialect());
		}
		return super.isPageComplete();
	}

}
