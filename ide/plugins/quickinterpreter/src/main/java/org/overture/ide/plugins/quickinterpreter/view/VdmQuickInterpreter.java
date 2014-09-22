/*
 * #%~
 * org.overture.ide.plugins.quickinterpreter
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
package org.overture.ide.plugins.quickinterpreter.view;

import java.util.List;
import java.util.Vector;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import org.overture.ast.lex.Dialect;
import org.overture.ast.util.modules.ModuleList;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmsl.ui.editor.syntax.VdmSlCodeScanner;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.parser.config.Properties;
import org.overture.parser.syntax.ParserException;

public class VdmQuickInterpreter extends ViewPart
{
	private final int ENTER_KEYCODE = 13;
	private final int UP_KEYCODE = 16777217;
	private final int DOWN_KEYCODE = 16777218;
	private final int HISTORY_COUNT = 200;
	private List<String> history = new Vector<String>(HISTORY_COUNT);
	private int index = -1;
	private StyledText textAreaResult = null;
	private Text textInput = null;
	Interpreter interpreter;
	
	VdmSlCodeScanner scannerResult = new VdmSlCodeScanner(new VdmColorProvider());

	public VdmQuickInterpreter()
	{

		init();

	}

	private void init()
	{
		try
		{
			Settings.dialect = Dialect.VDM_SL;
			Settings.release = Release.VDM_10;
			Properties.numeric_type_bind_generation = true;
			interpreter = new ModuleInterpreter(new ModuleList());
			interpreter.init(null);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void createPartControl(Composite parent)
	{
		GridLayout layout = new GridLayout(1, false);

		FillLayout fillLayout = new FillLayout();

		fillLayout.type = org.eclipse.swt.SWT.VERTICAL;
		fillLayout.marginHeight = 0;

		fillLayout.spacing = 5;
		parent.setLayout(layout);

		textAreaResult = new StyledText(parent, SWT.MULTI | SWT.V_SCROLL
				| SWT.READ_ONLY);
		textAreaResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textInput = new Text(parent, SWT.BORDER | SWT.SINGLE);
		textInput.setLayoutData(new GridData(SWT.FILL, 10, false, false, 1, 1));
		textInput.addKeyListener(new org.eclipse.swt.events.KeyAdapter()
		{
			public void keyPressed(org.eclipse.swt.events.KeyEvent e)
			{
				if (e.keyCode == ENTER_KEYCODE)
				{
					String input = textInput.getText();
					execute(input);
					textInput.setText("");
				} else if (e.keyCode == UP_KEYCODE)
				{
					recallHistory(true);
					e.doit = false;
				} else if (e.keyCode == DOWN_KEYCODE)
				{
					recallHistory(false);
					e.doit = false;
				}
			}
		});

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();

		FontRegistry fontRegistry = currentTheme.getFontRegistry();
		Font font = fontRegistry.get(JFaceResources.TEXT_FONT);

		textAreaResult.setFont(font);
		textInput.setFont(font);

	}

	private void recallHistory(boolean forward)
	{
		textInput.setText(history.get(index));
		textInput.setSelection(textInput.getText().length());

		if (forward)
			index++;
		else
			index--;

		if (index > history.size() - 1)
		{
			index = history.size() - 1;
		} else if (index < 0)
		{
			index = 0;
		}
	}

	private void storeCommand(String cmd)
	{
		index = 0;
		history.add(0, cmd);
		if (history.size() > HISTORY_COUNT)
		{
			history = history.subList(0, HISTORY_COUNT);
		}
	}

	private void execute(String input)
	{
		if (input.startsWith("p ") || input.startsWith("print "))
		{
			input = input.substring(input.indexOf(' '));
		}

		input = input.trim();

		if (input.isEmpty())
		{
			return;
		}
		
		storeCommand(input);

		if (input.equals("help"))
		{
			appendResult("\n\nOverture Properties: "+"\n\tEVAL_TYPE_BINDS = "
					+ Properties.numeric_type_bind_generation + "\n\tINT_MIN = "
					+ Properties.minint + "\n\tINT_MAX = " + Properties.maxint
					+ "\n\tRelease = " + Settings.release + "\n\tDialect = "
					+ Settings.dialect+"\n");
			return;
		}

		
		appendResult("\n" + input);

		try
		{
			appendResult(" = "
					+ interpreter.execute(input.trim(), null));
		} catch (ParserException e)
		{
			appendResult(" = " + e.toString());
			init();
		} catch (Exception e)
		{
			appendResult(" --- " + e.getMessage());
			init();
		}

	}
	
	private void appendResult(String text)
	{
		final String oldText = textAreaResult.getText();
		String tmp = oldText+text;
		textAreaResult.append(text);
		textAreaResult.setSelection(textAreaResult.getText().length());
		
		scannerResult.setRange(new Document(tmp), 0, tmp.length());

		IToken token = null;
		do
		{
			token = scannerResult.nextToken();
			TextAttribute attribute = null;
			int start = scannerResult.getTokenOffset();
			int length = scannerResult.getTokenLength();

			if (token.getData() instanceof TextAttribute)
			{
				attribute = (TextAttribute) token.getData();
				textAreaResult.setStyleRange(new StyleRange(start, length, attribute.getForeground(), attribute.getBackground()));
			}

		} while (token != Token.EOF);
		
		
	}

	@Override
	public void setFocus()
	{

	}

}
