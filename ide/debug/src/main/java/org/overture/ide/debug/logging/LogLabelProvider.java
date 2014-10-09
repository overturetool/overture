/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.logging;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class LogLabelProvider extends LabelProvider implements
		ITableLabelProvider, IColorProvider
{
	private final RGB textColor = new RGB(85, 85, 85);

	// private final RGB inputColor = new RGB(0, 0, 255);
	// private final RGB outputColor = new RGB(0, 128, 0);

	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	public String getColumnText(Object element, int columnIndex)
	{
		LogItem data = (LogItem) element;
		String columnText = "";
		switch (columnIndex)
		{
			case 0:
				columnText = data.type;// data.output?"->":"<-";
				break;
			case 1:
				columnText = data.threadId.toString();
				break;
			case 2:
				columnText = data.getData();
				break;
			default:
				columnText = "not set";
		}
		return columnText;
	}

	public Color getBackground(Object element)
	{
		return null;
	}

	public Color getForeground(Object element)
	{
		if (element instanceof LogItem)
		{
			final LogItem item = (LogItem) element;
			final Display display = Display.getCurrent();

			if (item.isError)
			{
				if (item.output)
				{
					return display.getSystemColor(SWT.COLOR_DARK_RED);
				} else
				{
					return display.getSystemColor(SWT.COLOR_RED);
				}
			} else if (!item.output)
			{
				return display.getSystemColor(SWT.COLOR_DARK_YELLOW);
			} else if (item.output)
			{
				return display.getSystemColor(SWT.COLOR_DARK_BLUE);
			} else
			{
				return new Color(display, textColor);
			}
		}
		return null;
	}

}
