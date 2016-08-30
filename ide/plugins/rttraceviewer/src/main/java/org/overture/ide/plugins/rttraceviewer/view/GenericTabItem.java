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
package org.overture.ide.plugins.rttraceviewer.view;

import java.io.File;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.overture.ide.plugins.rttraceviewer.TracefileViewerPlugin;
import org.overture.ide.plugins.rttraceviewer.draw.TraceFigure;

// Referenced classes of package org.overture.tracefile.viewer:
//            TracefileViewerPlugin, tdCPU

public class GenericTabItem
{	
	public enum AllowedOverrunDirection {Horizontal, Vertical, Both}
	
    private TabItem theTabItem;
    private FigureCanvas theCanvas;
    private Figure theFigure;
    private int xmax;
    private int ymax;
    private Font theFont;
    static final boolean $assertionsDisabled = false;//!org.overture.tracefile.viewer.GenericTabItem.desiredAssertionStatus();
    private AllowedOverrunDirection allowedDirection;
    
    
    public GenericTabItem(String theName, TabFolder theFolder, AllowedOverrunDirection direction)
    {
        theTabItem = null;
        theCanvas = null;
        theFigure = null;
        xmax = 0;
        ymax = 0;
        theFont = null;
        
        if(!$assertionsDisabled && theName == null)
        {
            throw new AssertionError();
        }
        if(!$assertionsDisabled && theFolder == null)
        {
            throw new AssertionError();
        } 
        else
        {
        	theFigure = new Figure();
        	
        	theCanvas = new FigureCanvas(theFolder);
        	theCanvas.setLayout(new FillLayout());
        
        	theCanvas.setContents(theFigure);
        	theCanvas.setBackground(ColorConstants.white);

        	theCanvas.setScrollBarVisibility(FigureCanvas.ALWAYS);
        	//theCanvas.setSize(theFolder.getSize());
        	//theCanvas.setSize(3000,3000);

        	theTabItem = new TabItem(theFolder, 0);
            theTabItem.setText(theName);
            theTabItem.setControl(theCanvas);
            
            this.allowedDirection = direction;
                       
            theFont = new Font(theTabItem.getDisplay(), "MS Gothic", 12, 0);
            return;
            
        }
    }
    
    public boolean isCanvasOverrun()
    { 	 
    	
    	if(this.allowedDirection == AllowedOverrunDirection.Horizontal)
    		return isCanvasVerticallyOverrun();
    	else if(this.allowedDirection == AllowedOverrunDirection.Vertical)
    		return isCanvasHorizontallyOverrun();
    	else //Both, so we never overrun
    		return false;
    }
    
    private boolean isCanvasHorizontallyOverrun()
    {
    	Point tabFolderSize = theTabItem.getParent().getSize();
    	int tabFolderWidth = tabFolderSize.x;
    	
    	return xmax > tabFolderWidth;
    }
    
    private boolean isCanvasVerticallyOverrun(){
    	
    	Point tabFolderSize = theTabItem.getParent().getSize();
    	int tabFolderHeight = tabFolderSize.y;
    	
    	return ymax > tabFolderHeight;
    }

    public Long getXMax()
    {
    	return new Long(xmax);
    }
    
    public Long getYMax()
    {
    	return new Long(ymax);
    }
    
    public String getName()
    {
        return theTabItem.getText();
    }

    public Long getHorizontalSize()
    {
    	return new Long(theFigure.getSize().width);
    	//return new Long(theCanvas.getSize().x);
    }

    public Long getVerticalSize()
    {  	
        return new Long(theFigure.getSize().height);
    	//return new Long(theCanvas.getSize().y);
    }

    public void addFigure(TraceFigure aFigure)
    { 	
        if(!$assertionsDisabled && aFigure == null)
            throw new AssertionError();
        if(!$assertionsDisabled && theFigure == null)
        {
            throw new AssertionError();
        } else
        {
        	//Update xmax and ymax
            Rectangle rect = aFigure.getBounds();
            int xfig = rect.x + rect.width - 2;
            xmax = xmax < xfig ? xfig : xmax;
            int yfig = rect.y + rect.height - 2;
            ymax = ymax < yfig ? yfig : ymax;
            
            //Ensure that the canvas is only expanded not shrinked
            int newX = theFigure.getSize().width > xmax ? theFigure.getSize().width : xmax;
            int newY = theFigure.getSize().height > ymax ? theFigure.getSize().height : ymax;
                    
            theFigure.setSize(newX, newY);
            theFigure.add(aFigure);
        }
    }
    
    public void addBackgroundFigure(TraceFigure bFigure)
    {
    	//Add figure and dont update xmax and ymax
    	//Ensure that the canvas is expanded if the background figure goes beyond
        Rectangle rect = bFigure.getBounds();
        int xfig = rect.x + rect.width;
        int yfig = rect.y + rect.height;
        int newX = theFigure.getSize().width > xfig ? theFigure.getSize().width : xfig;
        int newY = theFigure.getSize().height > yfig ? theFigure.getSize().height : yfig;

        theFigure.setSize(newX, newY);
        theFigure.add(bFigure, 0);
    }

    public Font getCurrentFont()
    {
        return theFont;
    }

    public void exportJPG(String fileName)
    {
        Image theImage = new Image(null, xmax + 50, ymax + 50);
        GC theGC = new GC(theImage);
        Graphics theGraphics = new SWTGraphics(theGC);
        theFigure.paint(theGraphics);
        theGraphics.fillRectangle(xmax + 50, 0, 10, ymax + 50);
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
    		//Force cleanup of special trace figures
        	for(Object child : theFigure.getChildren())
        	{
        		if(child instanceof TraceFigure)
        		{
        			((TraceFigure)child).dispose();
        		}
        	}
        	
            theCanvas.getViewport().setViewLocation(0, 0);
            theFigure.removeAll();
            theFigure.erase();
            theFigure.setSize(0, 0);
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
    
    public TabItem getTabItem()
    {
    	return theTabItem;
    }
}
