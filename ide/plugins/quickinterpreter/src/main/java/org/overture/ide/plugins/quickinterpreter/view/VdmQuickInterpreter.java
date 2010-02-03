package org.overture.ide.plugins.quickinterpreter.view;

import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.syntax.ParserException;

public class VdmQuickInterpreter extends ViewPart
{
	private final int ENTER_KEYCODE = 13;
	private final int UP_KEYCODE = 16777217;
	private final int DOWN_KEYCODE = 16777218;
	private final int HISTORY_COUNT = 200;
	private List<String> history = new Vector<String>(HISTORY_COUNT);
	private int index = -1;
	private Text textAreaResult = null;
	private Text textInput = null;
	ClassInterpreter ci;

	public VdmQuickInterpreter() {

		init();

	}

	private void init()
	{
		try
		{
			Settings.dialect = Dialect.VDM_PP;
			ci = new ClassInterpreter(new ClassList());
			ci.init(null);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent)
	{
		// parent.setLayout(new FormLayout ());
		GridLayout layout = new GridLayout(1, false);
		// RowLayout layout =new RowLayout ();
		// layout.fill= true;
		// layout.type = SWT.VERTICAL;
		// layout.pack=false;

		FillLayout fillLayout = new FillLayout();

		fillLayout.type = org.eclipse.swt.SWT.VERTICAL;
		fillLayout.marginHeight = 0;

		fillLayout.spacing = 5;
		// top = new Composite(parent, SWT.EMBEDDED);
		// top.setLayout(layout);
		// top.pack();
		parent.setLayout(layout);

		textAreaResult = new Text(parent, SWT.MULTI | SWT.V_SCROLL
				| SWT.READ_ONLY);
		textAreaResult.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL,
				true,
				true,
				1,
				1));
		textInput = new Text(parent, SWT.BORDER | SWT.SINGLE);
		textInput.setLayoutData(new GridData(SWT.FILL, 10, false, false, 1, 1));
		textInput.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			public void keyPressed(org.eclipse.swt.events.KeyEvent e)
			{
				if (e.keyCode == ENTER_KEYCODE)
				{
					String input = textInput.getText();
					textInput.setText("");
					textAreaResult.append("\n" + input);
					addEntry(input);
				} else if (e.keyCode == UP_KEYCODE)
				{
					index--;
					String input = getEntry();
					if (input.length() > 0)
						textInput.setText(input);
					else
						index = 0;
				} else if (e.keyCode == DOWN_KEYCODE)
				{
					index++;
					String input = getEntry();
					if (input.length() > 0)
						textInput.setText(input);
					else
						index = history.size() - 1;
				}
			}
		});

	}

	private void addEntry(String input)
	{
		index++;
		if (index >= HISTORY_COUNT)
			index = 0;
		if (history.size() > index)
			history.add(index, input);
		else
			history.add(input);

		try
		{

			// long before = System.currentTimeMillis();
			Settings.dialect = Dialect.VDM_PP;
			if (input.startsWith("p ") || input.startsWith("print "))
				input = input.substring(input.indexOf(' '));
			textAreaResult.append(" = " + ci.execute(input.trim(), null));
			// long after = System.currentTimeMillis();
			// textAreaResult.append("Executed in " +
			// (double)(after-before)/1000 + " secs. ");

		}catch(ParserException e)
		{
			textAreaResult.append(" = "+ e.toString());
			init();
		}
		catch (Exception e)
		{
			textAreaResult.append(" = Fatal error");
			init();
			// e.printStackTrace();
		}

	}

	private String getEntry()
	{
		if (index < history.size() && index >= 0)
			return history.get(index);
		else
			return "";
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

} // @jve:decl-index=0:visual-constraint="22,16,646,230"
