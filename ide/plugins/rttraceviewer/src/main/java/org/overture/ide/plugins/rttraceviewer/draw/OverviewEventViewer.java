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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.geometry.Point;
import org.overture.ide.plugins.rttraceviewer.data.*;
import org.overture.ide.plugins.rttraceviewer.view.*;

public class OverviewEventViewer extends TraceEventViewer {

	private final Long ELEMENT_SIZE = 18L;
	private static List<Point> timeLineStart = new ArrayList<Point>();

	public OverviewEventViewer()
	{
	}

	public void drawStaticItems(GenericTabItem tab, Vector<TraceCPU> cpus, Vector<TraceBus> buses)
	{
        Long yPos = RESOURCE_VINTERVAL / 2L;
        timeLineStart.clear();
        
        //Draw CPU labels in reverse order
        Collections.reverse(cpus);

        for(TraceCPU cpu : cpus)
        {
            NormalLabel nlb = new NormalLabel(cpu.getName(), tab.getCurrentFont());
            Long xPos = BUS_LABEL_X_POS - new Long(nlb.getSize().width);
            Point np = new Point(xPos.intValue(), yPos.intValue());
            nlb.setLocation(np);
            tab.addFigure(nlb);
            cpu.setX(CPU_X_START);
            cpu.setY(yPos + 10L);
            yPos += RESOURCE_VINTERVAL;
            
            timeLineStart.add(new Point(cpu.getX().intValue(), cpu.getY().intValue()));
            //drawTimeline(tab, cpu.getX(), cpu.getY(), tab.getHorizontalSize(), cpu.getY());
        }

        //Draw Bus labels
        for(TraceBus bus : buses)
        {
            NormalLabel nlb = new NormalLabel(bus.getName(), tab.getCurrentFont());
            Long xPos = BUS_LABEL_X_POS - new Long(nlb.getSize().width);
            Point np = new Point(xPos.intValue(), yPos.intValue());
            nlb.setLocation(np);
            tab.addFigure(nlb);
            bus.setX(CPU_X_START);
            bus.setY(yPos + 10L);
            yPos = new Long(yPos.longValue() + RESOURCE_VINTERVAL.longValue());
            
            timeLineStart.add(new Point(bus.getX().intValue(), bus.getY().intValue()));
            //drawTimeline(tab, bus.getX(), bus.getY(), tab.getHorizontalSize(), bus.getY());
        }
        
		//Add spacer between bus/cpu objects and timeline
		Line spacer = new Line(0L, 0L, CPU_X_START, 0L);
		spacer.setForegroundColor(ColorConstants.white);
		tab.addFigure(spacer);
	}
	

	@Override
	public void drawTimelines(GenericTabItem tab) {
		for(Point start : timeLineStart)
		{
			Long x = new Long(start.x);
			Long y = new Long(start.y);
			
			drawTimeline(tab, x, y, tab.getHorizontalSize(), y);
		}
	}
	
	public void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread swappedThread)
	{
		updateCpu(tab, cpu, currentThread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
		drawSwapImage(tab, x1, y1, SWAP_DIRECTION.NORTH);
		cpu.setX(x2);
	}

	public void drawDelayedThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread swappedThread)
	{
		updateCpu(tab, cpu, currentThread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.orange);
        drawSwapImage(tab, x1, y1, SWAP_DIRECTION.SOUTH);
        cpu.setX(x2);
	}
	
	public void drawThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread swappedThread)
	{
		updateCpu(tab, cpu, currentThread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
        
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
        drawSwapImage(tab, x1, y1, SWAP_DIRECTION.SOUTH);
        cpu.setX(x2);
	}
	
	public void drawThreadKill(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread killedThread)
	{
		updateCpu(tab, cpu, currentThread);
		
		Long x1 = tab.getXMax();// < cpu.getX() ? cpu.getX() : tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
        
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.red);
        cpu.setX(x2);
	}

	public void drawThreadCreate(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread newthread)
	{
		updateCpu(tab, cpu, currentThread);
		
		Long x1 = tab.getXMax();// < cpu.getX() ? cpu.getX() : tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
        
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.green);
        cpu.setX(x2);
	}
	
	public void drawOpRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
	    Line line = new Line(x1, y1, x2, y2);
        line.setForegroundColor(ColorConstants.blue);
        line.setLineWidth(3L);
        tab.addFigure(line);
        cpu.setX(tab.getXMax());
	}
	
	public void drawOpActivate(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
	    Line line = new Line(x1, y1, x2, y2);
        line.setForegroundColor(ColorConstants.blue);
        line.setLineWidth(3L);
        tab.addFigure(line);
        cpu.setX(tab.getXMax());
	}
	
	public void drawOpCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destionationObject,TraceOperation operation)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
	    Line line = new Line(x1, y1, x2, y2);
        line.setForegroundColor(ColorConstants.blue);
        line.setLineWidth(3L);
        tab.addFigure(line);
        cpu.setX(tab.getXMax());
	}
	
	public void drawMessageRequest(GenericTabItem tab, TraceCPU cpu, TraceObject object, TraceBus bus, TraceOperation op)
	{
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.lightGray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
		
		//Draw arrow from CPU to bus
		y1 = cpu.getY() + BUSMSG_ARROW_OFFSET;
		y2 = bus.getY() - BUSMSG_ARROW_OFFSET;
		String label = " call "+op.getName()+" ";
		
		drawVerticalArrow(tab, x1, y1, y2, label, ColorConstants.darkBlue);
	}

	public void drawReplyRequest(GenericTabItem tab, TraceCPU cpu, TraceObject object, TraceBus bus, TraceOperation op)
	{
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.lightGray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
		
		//Draw arrow from CPU to bus
		y1 = cpu.getY() + BUSMSG_ARROW_OFFSET;
		y2 = bus.getY() - BUSMSG_ARROW_OFFSET;
		String label = " return from "+op.getName()+" ";
		
		drawVerticalArrow(tab, x1, y1, y2, label, ColorConstants.darkBlue);
	}
	
	public void drawMessageActivated(GenericTabItem tab, TraceCPU cpu, TraceObject object, TraceBus bus, TraceOperation op)
	{
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
	}

	public void drawMessageCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op, TraceObject obj)
	{
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.darkGray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
		
		//Draw arrow from bus to CPU
		y1 = bus.getY() - BUSMSG_ARROW_OFFSET;
		y2 = cpu.getY() + BUSMSG_ARROW_OFFSET;
		String label = " "+op.getName()+" ";
		
		drawVerticalArrow(tab, x2, y1, y2, label, ColorConstants.darkBlue);
		
		updateCpu(tab, cpu, thread);
	}

	@Override
	public void drawTimeMarker(GenericTabItem tab, Long markerTime)
	{	//FIXME: Magic numbers
		Long markerStartX = tab.getXMax() < CPU_X_START ? CPU_X_START : tab.getXMax();
		Long markerStartY = 10L;
		Long markerEndX = markerStartX;
		Long markerEndY = tab.getYMax() + 5L;
		
		//Draw vertical marker line
		Line markerLine = new Line(markerStartX, markerStartY, markerEndX, markerEndY);
		markerLine.setForegroundColor(ColorConstants.lightGray);
		markerLine.setDot();
		tab.addBackgroundFigure(markerLine);
		
		//Draw time label
		Line labelLine = new Line(markerEndX, markerEndY + 5L, markerEndX, markerEndY + 10L);
		tab.addBackgroundFigure(labelLine);
		String labelText = markerTime.toString();
		RotatedLabel timeLabel = new RotatedLabel(labelText, tab.getCurrentFont());	
		int labelStartX = markerEndX.intValue() - timeLabel.getSize().width/2;
		int labelStartY = markerEndY.intValue() + 15;
		Point labelLocation = new Point(labelStartX, labelStartY);
		timeLabel.setLocation(labelLocation);
		tab.addBackgroundFigure(timeLabel);
	}
	
    public void updateCpu(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
    {
    	if(cpu.getX() < tab.getXMax())
    	{
		    if(!cpu.isIdle() && thread != null)
		    {
			    Line line = new Line(cpu.getX(), cpu.getY(), tab.getXMax(), cpu.getY());
		        line.setForegroundColor(ColorConstants.blue);
		        line.setLineWidth(3L);
		        if(thread.getStatus())
		            line.setDot();
		        
		        tab.addFigure(line);
		        cpu.setX(tab.getXMax());
		        
		    }
    	}
    }
    
	public void drawSourceConjecture(GenericTabItem tab, TraceCPU cpu, String name)
	{
		ConjectureMarker marker = new ConjectureMarker();
        NormalLabel label = new NormalLabel(name, tab.getCurrentFont());

        Point p1 = new Point(tab.getXMax() + 1L, cpu.getY() - 8L);
        Point p2 = new Point(tab.getXMax() + 2L, cpu.getY() + 12L);
  
        marker.setLocation(p1);
        marker.setSize(16, 16);
        marker.setFill(false);
        marker.setForegroundColor(ColorConstants.red);
        
        tab.addBackgroundFigure(marker);
        
        label.setLocation(p2);
        label.setForegroundColor(ColorConstants.red);
        
        tab.addBackgroundFigure(label);
	}
    
	public void drawDestinationConjecture(GenericTabItem tab, TraceCPU cpu, String name)
	{
        ConjectureMarker marker = new ConjectureMarker();
        NormalLabel label = new NormalLabel(name, tab.getCurrentFont());

        Point p1 = new Point(tab.getXMax() + 1L, cpu.getY() - 8L);
        Point p2 = new Point(tab.getXMax() + 2L, cpu.getY() - 40L);
        
        marker.setLocation(p1);
        marker.setSize(16, 16);
        marker.setFill(false);
        marker.setForegroundColor(ColorConstants.red);
        
        tab.addBackgroundFigure(marker);
        
        label.setLocation(p2);
        label.setForegroundColor(ColorConstants.red);
        
        tab.addBackgroundFigure(label);
	}

}
