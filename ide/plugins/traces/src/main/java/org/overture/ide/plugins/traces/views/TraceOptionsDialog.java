package org.overture.ide.plugins.traces.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.overturetool.vdmj.traces.TraceReductionType;

public class TraceOptionsDialog extends Composite
{
	public static boolean isCanceled = false;
	private Button buttonCancel = null;
	private Button buttonOk = null;
	private Combo comboReductionType = null;
	private Label label1 = null;
	private Label label2 = null;
	private Label label3 = null;
	private Text textSeed = null;
	private Text textSubset = null;

	public TraceOptionsDialog(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize()
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.setLayout(gridLayout);
		label1 = new Label(this, SWT.NONE);
		label1.setText("Trace Reduction Type:");
		createComboReductionType();
		setSize(new Point(421, 224));
		label2 = new Label(this, SWT.NONE);
		label2.setText("Seed:");
		textSeed = new Text(this, SWT.BORDER);
		textSeed.setText(new Long(System.currentTimeMillis()).toString());
		label3 = new Label(this, SWT.NONE);
		label3.setText("Sub set:");
		textSubset = new Text(this, SWT.BORDER);
		textSubset.setText("1.00000000000");
		buttonCancel = new Button(this, SWT.NONE);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				isCanceled = true;
				getShell().close();
			}
		});
		buttonOk = new Button(this, SWT.NONE);
		buttonOk.setText("Ok");
		buttonOk.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				isCanceled = false;
				subset= Float.parseFloat(textSubset.getText());
				seed= Long.parseLong(textSeed.getText());
				reductionType= TraceReductionType.valueOf(comboReductionType.getText());
				getShell().close();
			}
		});
	}

	/**
	 * This method initializes comboReductionType
	 * 
	 */
	private void createComboReductionType()
	{

		comboReductionType = new Combo(this, SWT.READ_ONLY);

		String[] reductions = new String[TraceReductionType.values().length];
		int i = 0;
		for (TraceReductionType r : TraceReductionType.values())
		{
			reductions[i] = r.toString();
			i++;
		}

		comboReductionType.setItems(reductions);
		if (reductions.length > 0)
			comboReductionType.select(0);
		comboReductionType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private float subset;
	private long seed;
	private TraceReductionType reductionType;
	public float getSubset()
	{
		return subset;
	}

	public long getSeed()
	{
		return seed;
	}

	public TraceReductionType getTraceReductionType()
	{
		return reductionType;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
