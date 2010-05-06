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
//	private final RGB inputColor = new RGB(0, 0, 255);
//	private final RGB outputColor = new RGB(0, 128, 0);

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
