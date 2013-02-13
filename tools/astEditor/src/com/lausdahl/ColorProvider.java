package com.lausdahl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorProvider
{
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
	public static final RGB GRAPH = new RGB(101, 74, 13);

	protected static Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

	public void dispose()
	{
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}

	public Color getColor(RGB rgb)
	{
		Color color = (Color) fColorTable.get(rgb);
		if (color == null)
		{
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
