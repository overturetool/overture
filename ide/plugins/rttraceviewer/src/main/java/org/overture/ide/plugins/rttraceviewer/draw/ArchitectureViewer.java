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

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Font;
import org.overture.ide.plugins.rttraceviewer.data.TraceBus;
import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;

public class ArchitectureViewer extends TraceViewer {
   
    private final Long BUS_LINE_WIDTH = new Long(2L);
    
    private HashMap<Long, Vector<Line>> vLines; //CPU Id, Vertical Lines
    private HashMap<Long, Line> hLines;	//Y-coordinate (bus), horizontal line
    private HashMap<Long, Point> cpuCoordinates;

    public ArchitectureViewer()
    {
    	vLines = new HashMap<Long, Vector<Line>>(); 
    	hLines = new HashMap<Long, Line>();
    	cpuCoordinates = new HashMap<Long, Point>();
    }
    
	public void drawArchitecture(GenericTabItem tab, Vector<TraceCPU> cpus, Vector<TraceBus> buses)
	{
		drawCpus(tab, cpus);
		drawBuses(tab, buses);
		drawVerticalLines(tab);
		drawHorizontalLines(tab);
	}
	
	private void drawCpus(GenericTabItem tab, Vector<TraceCPU> cpus)
	{
		Font textFont = tab.getCurrentFont();
		Long currentXPos = CPU_X_START;
		vLines.clear();
		
		for(TraceCPU cpu : cpus)
		{
			NormalLabel cpuLabel = new NormalLabel(cpu.getName(), textFont);
			RectangleLabelFigure rectFigure = new RectangleLabelFigure(cpuLabel);	
			if(cpu.isVirtual())
			{
				rectFigure.setDash();
				rectFigure.setForegroundColor(ColorConstants.darkGray);
			}
			
			//Calculate X coordinate of CPU box
			Point rectPoint = new Point(currentXPos.intValue(), CPU_Y_POS.intValue());			
			rectFigure.setLocation(rectPoint);
			rectFigure.setSize(CPU_WIDTH, CPU_HEIGHT);
			cpu.setX(currentXPos);		
			currentXPos += CPU_WIDTH + CPU_X_OFFSET;
			
			tab.addFigure(rectFigure);

			vLines.put(cpu.getId(), new ArrayList<Line>());
			cpuCoordinates.put(cpu.getId(), rectPoint);
		}
	}
	
	private void drawBuses(GenericTabItem tab, Vector<TraceBus> buses)
	{
		Font textFont = tab.getCurrentFont();
		Long currentYPos = BUS_Y_START;
		hLines.clear();
		
		for(TraceBus bus : buses)
		{	
			//Calculate bus label coordinates and draw 
			NormalLabel busLabel = new NormalLabel(bus.getName(), textFont);	
			Long xLabelStart = new Long(BUS_LABEL_X_POS - new Long(busLabel.getSize().width));		
			Point labelPoint = new Point(xLabelStart.intValue(), currentYPos.intValue());			
			busLabel.setLocation(labelPoint);
			bus.setY(currentYPos);
			
			Line hLine = new Line(0L,currentYPos,0L,currentYPos); //Horizontal
								
			//Create vertical line for each connected cpu and horizontal for each bus
			for(Long cpuId : bus.getConnectedCpuIds())
			{
				Long yTop = CPU_Y_POS + CPU_HEIGHT;
				Long yBottom = currentYPos;
				Line vLine = new Line(0L, yTop, 0L, yBottom); //Vertical 
				
				if(bus.isVirtual())
				{
					//Make vertical bus lines gray and dotted
					vLine.setDot();
					vLine.setForegroundColor(ColorConstants.gray);
					hLine.setDot();
					hLine.setForegroundColor(ColorConstants.gray);
				}
				
				Vector<Line> cpuLines = vLines.get(cpuId);
				cpuLines.add(vLine);
			}

			hLines.put(currentYPos, hLine); //Save horizontal line for later
			
			currentYPos = currentYPos + BUS_Y_OFFSET;
			tab.addFigure(busLabel);		
		}
	}
	
	private void drawVerticalLines(GenericTabItem tab)
	{
		//Connect busses to CPU's
		for(Long cpuId : vLines.keySet())
		{
			Vector<Line> cpuLines = vLines.get(cpuId);

			//Calculate line coordinates
			Point cpuCoordinate = cpuCoordinates.get(cpuId);
			int lineXStartPos = cpuCoordinate.x;
			int lineXPos = lineXStartPos;
			int lineXOffset = (int)(CPU_WIDTH.intValue() / (cpuLines.size()+1));;		

			for(Line line : cpuLines)
			{
				lineXPos += lineXOffset;
				
				//Update x coordinates to match cpu coordinates
				Point cpuStart = line.getStart();
				cpuStart.setX(lineXPos);			
				Point cpuEnd = line.getEnd();
				cpuEnd.setX(lineXPos);
				
				line.setStart(cpuStart);
				line.setEnd(cpuEnd);		
				tab.addFigure(line);
				
				//Save x coordinate if this is the first or last connection on the horizontal lines
				Line busLine = hLines.get(new Long(cpuEnd.y));
				Point busStart = busLine.getStart();
				Point busEnd = busLine.getEnd();
				
				if(busStart.x > lineXPos || busStart.x == 0)
					busStart.setX(lineXPos);
				
				if(busEnd.x < lineXPos)
					busEnd.setX(lineXPos);	
				
				busLine.setStart(busStart);
				busLine.setEnd(busEnd);
			}		
		}
	}
	
	private void drawHorizontalLines(GenericTabItem tab)
	{
		for(Line line : hLines.values())
		{
			line.setLineWidth(BUS_LINE_WIDTH);
			tab.addFigure(line);
		}
	}
}
