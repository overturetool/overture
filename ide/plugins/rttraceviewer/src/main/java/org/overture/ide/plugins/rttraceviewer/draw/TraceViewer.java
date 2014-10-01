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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;

public abstract class TraceViewer {

	protected enum SWAP_DIRECTION	{ NORTH, SOUTH, EAST, WEST }
	
	protected final Long CPU_X_START = new Long(150L);
	protected final Long CPU_X_OFFSET= new Long(40L);
	protected final Long CPU_Y_POS= new Long(25L);;
	protected final Long CPU_WIDTH = new Long(150L);
	protected final Long CPU_HEIGHT = new Long(40L);
    
	protected final Long BUS_LABEL_X_POS = new Long(125L);
	protected final Long BUS_X_START = new Long(100L);
	protected final Long BUS_Y_START = new Long(90L);
	protected final Long BUS_Y_OFFSET = new Long(30L);
	
	protected final Long MARKER_START_END_HALFLENGTH = new Long(5L);
	protected final Long MARKER_THICKNESS = new Long(3L);

	protected final Long ELEMENT_SIZE = new Long(18L);
	
	protected final Long OBJECT_WIDTH_FACTOR = new Long(10L);
	
	protected final Long OBJECT_ARROW_SIZE = new Long(5L);
	protected final Long BUSMSG_ARROW_OFFSET = 8L;
	
	
    protected void drawMarker(GenericTabItem tab, Long x1, Long y1, Long x2, Long y2, Color clr)
	{
		//Draw main line (Bold and colored)
	    Line line = new Line(x1, y1, x2, y2);
	    line.setLineWidth(MARKER_THICKNESS);
	    line.setForegroundColor(clr);
	    tab.addFigure(line);
	    
	    //Highlight start and end of the main line with small black lines
	    //First determine if line is vertical or horizontal
	    if(y1 == y2)
	    {
	        line = new Line(x1, y1 - MARKER_START_END_HALFLENGTH, x1, y1 + MARKER_START_END_HALFLENGTH);
	        tab.addFigure(line);
	        line = new Line(x2, y1 - MARKER_START_END_HALFLENGTH, x2, y1 + MARKER_START_END_HALFLENGTH);
	        tab.addFigure(line);
	    }
	    else if(x1 == x2)
	    {
	        line = new Line(x1 - MARKER_START_END_HALFLENGTH, y1, x1 + MARKER_START_END_HALFLENGTH, y1);
	        tab.addFigure(line);
	        line = new Line(x1 - MARKER_START_END_HALFLENGTH, y2, x1 + MARKER_START_END_HALFLENGTH, y2);
	        tab.addFigure(line);
	    }
	
	}
    
    protected void drawSwapImage(GenericTabItem tab, Long x, Long y, SWAP_DIRECTION dir)
    {
    	org.eclipse.swt.graphics.Image image = null;
    	String imagePath = "";
    	Dimension dim = null;
    	Point point = null;
    	
    	//FIXME: All these magic numbers?!
    	switch(dir)
    	{
	    	case NORTH: imagePath = tab.composePath("icons", "vswapout.gif");
	    				dim = new Dimension(16, 20);
	    				point = new Point(x.intValue() + 2, y.intValue() - 24);
	    				break;
	    	case SOUTH: imagePath = tab.composePath("icons", "vswapin.gif");
	    				dim = new Dimension(16, 20);
	    				point = new Point(x.intValue() + 2, y.intValue() - 24);
	    				break;
	    	case EAST:  imagePath = tab.composePath("icons", "hswapout.gif");
	    				dim = new Dimension(20, 16);
	    				point = new Point(x.intValue() + 8, y.intValue() + 2);
	    				break;
	    	case WEST:  imagePath = tab.composePath("icons", "hswapin.gif");
	    				dim = new Dimension(20, 16);
	    				point = new Point(x.intValue() + 8, y.intValue() + 2);
	    				break;
    	}
    	
        image = tab.getImage(imagePath);
        
        
        if(image != null && point != null && dim != null)
        {
            TraceImage imagefig = new TraceImage(image);
            imagefig.setLocation(point);
            imagefig.setSize(dim);
            tab.addFigure(imagefig);
        }
    }
    
    protected void drawTimeline(GenericTabItem tab, Long x1, Long y1, Long x2, Long y2)
    {
		Line timeLine = new Line(x1, y1, x2, y2);
        timeLine.setForegroundColor(ColorConstants.lightGray);
        timeLine.setDot();
        tab.addBackgroundFigure(timeLine);
    }
    
    protected void drawHorizontalArrow(GenericTabItem tab, Long fromX, Long toX, Long y, String label, String toolTip, Color color)
    {
		NormalLabel arrowLabel = new NormalLabel(label, tab.getCurrentFont());
		NormalLabel toolLabel = new NormalLabel(toolTip, tab.getCurrentFont());
		
		Line horizontalLine = new Line(fromX, y, toX, y);
		horizontalLine.setForegroundColor(color);
		horizontalLine.setToolTip(toolLabel);
		tab.addFigure(horizontalLine);
		
		Line upperArrow;
		Line lowerArrow;
		Long labelX;
		
		if(toX > fromX) //Left to right
		{
			labelX = fromX + new Long(((toX-fromX) - new Long(arrowLabel.getSize().width))/2); //Place label on center of arrow
			upperArrow = new Line(toX - OBJECT_ARROW_SIZE, y - OBJECT_ARROW_SIZE, toX, y);
			lowerArrow = new Line(toX - OBJECT_ARROW_SIZE, y + OBJECT_ARROW_SIZE, toX, y);
		} 
		else //Right to left
		{
			labelX = new Long((fromX-toX)/2) - new Long(arrowLabel.getSize().width) + toX;
			upperArrow = new Line(toX + OBJECT_ARROW_SIZE, y - OBJECT_ARROW_SIZE, toX, y);
			lowerArrow = new Line(toX + OBJECT_ARROW_SIZE, y + OBJECT_ARROW_SIZE, toX, y);		
		}
		
		Point labelPos = new Point(labelX.intValue(), y.intValue() - arrowLabel.getSize().height); //Place label above arrow
		arrowLabel.setLocation(labelPos);
		
		upperArrow.setForegroundColor(color);
		lowerArrow.setForegroundColor(color);
		
		tab.addFigure(upperArrow);
		tab.addFigure(lowerArrow);
		tab.addFigure(arrowLabel);
    }
    
	protected void drawVerticalArrow(GenericTabItem tab, Long x, Long y1, Long y2, String label, Color clr)
	{
		//Draw line
		Line line = new Line(x, y1, x, y2);
		NormalLabel lbl = new NormalLabel(" "+label+" ", tab.getCurrentFont());
		line.setForegroundColor(clr);
		line.setToolTip(lbl);
		tab.addFigure(line);

		//Draw arrow
		//TODO: Remove magic numbers
		if(y1 < y2)
		{
			line = new Line(x - 4L, y2 - 8L, x, y2);
			line.setForegroundColor(clr);
			tab.addFigure(line);
			line = new Line(x + 4L, y2 - 8L, x, y2);
			line.setForegroundColor(clr);
			tab.addFigure(line);
		} else
		{
			line = new Line(x - 4L, y2 + 8L, x, y2);
			line.setForegroundColor(clr);
			tab.addFigure(line);
			line = new Line(x + 4L, y2 + 8L, x, y2);
			line.setForegroundColor(clr);
			tab.addFigure(line);
		}
	}
}
