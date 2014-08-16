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
package org.overture.ide.debug.ui.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.overture.ide.debug.core.VdmDebugPlugin;

public class VdmDebugLogLabelProvider extends LabelProvider implements
		ITableLabelProvider, IColorProvider
{

	// private final IColorManager colorManager = new DLTKColorManager(false);

	//	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$

	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS"); //$NON-NLS-1$

	@Override
	public void dispose()
	{
		super.dispose();
		// colorManager.dispose();
	}

	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	public String getColumnText(Object element, int columnIndex)
	{
		if (element instanceof VdmDebugLogItem)
		{
			final VdmDebugLogItem item = (VdmDebugLogItem) element;
			switch (columnIndex)
			{
			// case 0:
			// return dateFormat.format(new Date(item.getTimestamp()));
				case 1 - 1:
					return timeFormat.format(new Date(item.getTimestamp()));
				case 2 - 1:
					return item.getType();
				case 3 - 1:
					if (item.getSessionId() > 0)
					{
						return String.valueOf(item.getSessionId());
					} else
					{
						break;
					}
				case 4 - 1:
					return formatMessage(item);
			}
		}
		return null;
	}

	private static final String XML_DECL_BEGIN = "<?xml"; //$NON-NLS-1$
	private static final String XML_DECL_END = "?>"; //$NON-NLS-1$

	private String formatMessage(VdmDebugLogItem item)
	{
		String result = item.getMessage();
		if (result.startsWith(XML_DECL_BEGIN))
		{
			int end = result.indexOf(XML_DECL_END);
			if (end >= 0)
			{
				end += XML_DECL_END.length();
				while (end < result.length()
						&& Character.isWhitespace(result.charAt(end)))
				{
					++end;
				}
				result = result.substring(end);
			}
		}
		return result.replaceAll("[\\p{Cntrl}]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Color getBackground(Object element)
	{
		return null;
	}

	private final RGB textColor = new RGB(85, 85, 85);
	private final RGB inputColor = new RGB(0, 0, 255);
	private final RGB outputColor = new RGB(0, 128, 0);

	public Color getForeground(Object element)
	{
		final VdmDebugPlugin colors = VdmDebugPlugin.getDefault();
		if (element instanceof VdmDebugLogItem)
		{
			final VdmDebugLogItem item = (VdmDebugLogItem) element;
			if (item == null || item.getType() == null)
			{
				return colors.getColor(textColor);
			} else if (item.getType().equals(Messages.ItemType_Input))
			{
				return colors.getColor(inputColor);
			} else if (item.getType().equals(Messages.ItemType_Output))
			{
				return colors.getColor(outputColor);
			} else
			{
				return colors.getColor(textColor);
			}
		}
		return null;
	}
}
