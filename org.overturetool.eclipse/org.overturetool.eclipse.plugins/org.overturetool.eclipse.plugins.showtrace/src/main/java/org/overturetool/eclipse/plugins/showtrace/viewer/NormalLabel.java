// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   NormalLabel.java

package org.overturetool.eclipse.plugins.showtrace.viewer;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;

public class NormalLabel extends Label
{

    public NormalLabel(String theText, Font theFont)
    {
        Dimension dimStr = FigureUtilities.getStringExtents(theText, theFont);
        setText(theText);
        setSize(dimStr.width, dimStr.height);
        setFont(theFont);
    }
}