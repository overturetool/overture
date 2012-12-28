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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

// Referenced classes of package org.overturetool.tracefile.viewer:
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
    static final boolean $assertionsDisabled = false;//!org.overturetool.tracefile.viewer.GenericTabItem.desiredAssertionStatus();
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
        	theCanvas.setSize(theFolder.getSize());
        	//theCanvas.setSize(3000,3000);

        	theTabItem = new TabItem(theFolder, 0);
            theTabItem.setText(theName);
            theTabItem.setControl(theCanvas);
            
            //theFigure.
            //theFigure.setSize(3000, 3000);
            
            this.allowedDirection = direction;
            
            theFolder.addControlListener(new ResizeListener(theFolder));
            
            theFont = new Font(theTabItem.getDisplay(), "MS Gothic", 12, 0);
            return;
            
        }
    }
    
    private IResizeCallback callback = null;
    public void registerResizeCallback(IResizeCallback callback){
    	
    	this.callback = callback;
    }
    
    private void handleResize(){
    	
    	if(this.callback != null)
    		this.callback.handleResize(this);
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
            int xfig = rect.x + rect.width - 2;
            xmax = xmax < xfig ? xfig : xmax;
            int yfig = rect.y + rect.height - 2;
            ymax = ymax < yfig ? yfig : ymax;
                    
			Point currentFolderSize = theTabItem.getParent().getSize();
			int currentX = currentFolderSize.x;
			int currentY = currentFolderSize.y;
            sizeCanvas(currentX, currentY);
            
            theFigure.add(aFigure);
        }
    }
    
    public void addBackgroundFigure(IFigure bFigure)
    {
    	//Add figure and dont update xmax and ymax
       // theFigure.add(bFigure);
        theFigure.add(bFigure, 0);
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
    
    private void sizeCanvas(int currentX, int currentY){
    	theFigure.setSize(xmax, ymax);
    	/*
    	if(allowedDirection == AllowedOverrunDirection.Both)
		{
    		if(xmax > currentX || ymax > currentY)
    		{
    			theFigure.setSize(xmax, ymax);
    			//theCanvas.setSize(xmax, ymax);
    		}
			return; //handleResize()
		}
		else if(allowedDirection == AllowedOverrunDirection.Horizontal)
		{
				theFigure.setSize(xmax, currentY);
    			//theCanvas.setSize(xmax, currentY);
		}
		else //Allows vertical overrun
		{
			theFigure.setSize(currentX, ymax);
			//theCanvas.setSize(currentX, ymax);
		}*/
    	
    }
    
    final class ResizeListener implements org.eclipse.swt.events.ControlListener
    {
    	private int lastX;
    	private int lastY;
    	
    	public ResizeListener(TabFolder tabFolder)
    	{
    		Point size = tabFolder.getSize();
    		lastX = size.x;
    		lastY = size.y;
    	}
    	
		public void controlMoved(ControlEvent e) {
			//Do nothing
		}

		public void controlResized(ControlEvent e) {
			
			
			TabFolder folder = (TabFolder) e.getSource();
			Point currentFolderSize = folder.getSize();
			
			int currentX = currentFolderSize.x;
			int currentY = currentFolderSize.y;
			
			sizeCanvas(currentX, currentY);
			//theCanvas.setSize(3000, 3000);
							
			if(currentX > lastX || currentY > lastY)
				handleResize();
		}
    	
    }

}