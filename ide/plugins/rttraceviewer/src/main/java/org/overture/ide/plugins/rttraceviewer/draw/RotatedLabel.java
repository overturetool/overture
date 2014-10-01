/*
 * #%~
 * RT Trace Viewer Plugin
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
package org.overture.ide.plugins.rttraceviewer.draw;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class RotatedLabel extends Label implements TraceFigure
{
    final Image theImage;
    public RotatedLabel(String theText, Font theFont)
    {
        Dimension dimStr = FigureUtilities.getStringExtents(theText, theFont);
        theImage = ImageUtilities.createRotatedImageOfString(theText, theFont, ColorConstants.black, ColorConstants.white);
        setIcon(theImage);
        setSize(dimStr.height, dimStr.width);
    }
    
    public void dispose() 
    {
    	if(!theImage.isDisposed())
    	{
    		theImage.dispose();
    	}
    }

}
