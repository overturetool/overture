/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.editor.syntax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class VdmColorProvider {
	
	public static final RGB MULTI_LINE_COMMENT = new RGB(128, 0, 0);
	public static final RGB SINGLE_LINE_COMMENT = new RGB(63, 127, 95);
	public static final RGB KEYWORD = new RGB(127, 0, 85);
	public static final RGB TYPE = new RGB(0, 0, 128);
	public static final RGB STRING = new RGB(42, 0, 255);
	public static final RGB DEFAULT = new RGB(0, 0, 0);
	public static final RGB VDMDOC_KEYWORD = new RGB(0, 128, 0);
	public static final RGB VDMDOC_TAG = new RGB(128, 128, 128);
	public static final RGB VDMDOC_LINK = new RGB(128, 128, 128);
	public static final RGB VDMDOC_DEFAULT = new RGB(0, 128, 128);
	public static final RGB LATEX = new RGB(153,180,209);
	
	protected static Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}

	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
