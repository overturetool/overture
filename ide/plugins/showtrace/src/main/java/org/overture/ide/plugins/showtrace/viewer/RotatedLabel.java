// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RotatedLabel.java

package org.overture.ide.plugins.showtrace.viewer;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class RotatedLabel extends Label
{

    public RotatedLabel(String theText, Font theFont)
    {
        Dimension dimStr = FigureUtilities.getStringExtents(theText, theFont);
        theImage = ImageUtilities.createRotatedImageOfString(theText, theFont, ColorConstants.black, ColorConstants.white);
        setIcon(theImage);
        setSize(dimStr.height, dimStr.width);
    }

    final Image theImage;
}