/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   GenericTabItem.java

package org.overture.ide.plugins.showtraceNextGen.view;

import java.io.File;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            TracefileViewerPlugin, tdCPU

public class GenericTabItem
{
    private TabItem theTabItem;
    private FigureCanvas theCanvas;
    private Figure theFigure;
    private int xmax;
    private int ymax;
    private Font theFont;
    static final boolean $assertionsDisabled = false;//!org.overturetool.tracefile.viewer.GenericTabItem.desiredAssertionStatus();

    public GenericTabItem(String theName, TabFolder theFolder)
    {
        theTabItem = null;
        theCanvas = null;
        theFigure = null;
        xmax = 0;
        ymax = 0;
        theFont = null;
        if(!$assertionsDisabled && theName == null)
            throw new AssertionError();
        if(!$assertionsDisabled && theFolder == null)
        {
            throw new AssertionError();
        } else
        {
            theTabItem = new TabItem(theFolder, 0);
            theTabItem.setText(theName);
            theCanvas = new FigureCanvas(theFolder);
            theTabItem.setControl(theCanvas);
            theCanvas.setLayout(new FillLayout());
            theFigure = new Figure();
            theCanvas.setContents(theFigure);
            theFigure.setSize(3000, 3000);
            theCanvas.setBackground(ColorConstants.white);
            theCanvas.setScrollBarVisibility(FigureCanvas.ALWAYS);
            theFont = new Font(theTabItem.getDisplay(), "MS Gothic", 12, 0);
            return;
        }
    }

    
    public String getName()
    {
        return theTabItem.getText();
    }


    public Long getHorizontalSize()
    {
        return new Long(3000L);
    }

    public Long getVerticalSize()
    {
        return new Long(3000L);
    }

    public void addFigure(IFigure aFigure)
    {
        if(!$assertionsDisabled && aFigure == null)
            throw new AssertionError();
        if(!$assertionsDisabled && theFigure == null)
        {
            throw new AssertionError();
        } else
        {
            Rectangle rect = aFigure.getBounds();
            int xfig = rect.x + rect.width;
            xmax = xmax < xfig ? xfig : xmax;
            int yfig = rect.y + rect.height;
            ymax = ymax < yfig ? yfig : ymax;
            theFigure.add(aFigure);
            return;
        }
    }

    public Font getCurrentFont()
    {
        return theFont;
    }

    public void exportJPG(String fileName)
    {
        Image theImage = new Image(null, xmax + 10, ymax + 10);
        GC theGC = new GC(theImage);
        Graphics theGraphics = new SWTGraphics(theGC);
        theFigure.paint(theGraphics);
        theGraphics.fillRectangle(xmax, 0, 10, ymax + 10);
        ImageData imgData[] = new ImageData[1];
        imgData[0] = theImage.getImageData();
        ImageLoader theLoader = new ImageLoader();
        theLoader.data = imgData;
        theLoader.save((new StringBuilder(String.valueOf(fileName))).append(".jpg").toString(), 4);
        theGraphics.dispose();
        theGC.dispose();
        theImage.dispose();
    }

    public final Image getImage(String path)
    {
        ImageDescriptor theDescriptor = TracefileViewerPlugin.getImageDescriptor(path);
        Image theImage = null;
        if(theDescriptor != null)
            theImage = theDescriptor.createImage();
        return theImage;
    }

    public String composePath(String pp1, String pp2)
    {
        String res = pp1;
        res = (new StringBuilder(String.valueOf(res))).append(File.separator).toString();
        res = (new StringBuilder(String.valueOf(res))).append(pp2).toString();
        return res;
    }

    public void disposeFigures()
    {
        if(!$assertionsDisabled && theFigure == null)
        {
            throw new AssertionError();
        } else
        {
            theCanvas.getViewport().setViewLocation(0, 0);
            theFigure.removeAll();
            theFigure.erase();
            xmax = 0;
            ymax = 0;
            return;
        }
    }

    public void dispose()
    {
        disposeFigures();
        theCanvas.dispose();
        theTabItem.dispose();
        if(theFont != null)
            theFont.dispose();
    }

}