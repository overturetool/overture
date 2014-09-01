/*
 * #%~
 * org.overture.ide.plugins.poviewer
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
package org.overture.ide.plugins.poviewer.view;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmsl.ui.editor.syntax.VdmSlCodeScanner;
import org.overture.pog.pub.IProofObligation;

public class PoTableView extends ViewPart implements ISelectionListener
{
	protected StyledText viewer;
	protected final Display display = Display.getCurrent();
	protected Font font = null;

	VdmSlCodeScanner scanner = new VdmSlCodeScanner(new VdmColorProvider());

	/**
	 * The constructor.
	 */
	public PoTableView()
	{
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();

		FontRegistry fontRegistry = currentTheme.getFontRegistry();
		font = fontRegistry.get(JFaceResources.TEXT_FONT);

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new StyledText(parent, SWT.WRAP | SWT.V_SCROLL|SWT.READ_ONLY);
		viewer.setFont(font);

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.setFocus();
	}

	public void setDataList(final IVdmProject project,
			final IProofObligation data)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				viewer.setText(data.getFullPredString());

				scanner.setRange(new Document(data.getFullPredString()), 0, data.getFullPredString().length());

				IToken token = null;
				do
				{
					token = scanner.nextToken();
					TextAttribute attribute = null;
					int start = scanner.getTokenOffset();
					int length = scanner.getTokenLength();

					if (token.getData() instanceof TextAttribute)
					{
						attribute = (TextAttribute) token.getData();
						viewer.setStyleRange(new StyleRange(start, length, attribute.getForeground(), attribute.getBackground()));
					}

				} while (token != Token.EOF);

			}

		});
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{

	}
}
