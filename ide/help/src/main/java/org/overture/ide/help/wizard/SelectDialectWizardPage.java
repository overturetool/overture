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
