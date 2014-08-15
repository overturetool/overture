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
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.overture.ide.plugins.rttraceviewer.data.*;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;

public class CpuEventViewer  extends TraceEventViewer {
	private static List<Point> timeLineStart = new ArrayList<Point>();
	
	public void drawStaticItems(GenericTabItem tab, Vector<TraceCPU> cpus, Vector<TraceBus> buses)
	{
		timeLineStart.clear();
		Long currentXPos = BUS_X_START; 
		Long yPos = CPU_Y_POS + CPU_HEIGHT + ELEMENT_SIZE;

		//Draw all buses
		for(TraceBus bus : buses)
		{
			NormalLabel nlb = new NormalLabel(bus.getName(), tab.getCurrentFont());

			RectangleLabelFigure nrr = new RectangleLabelFigure(nlb);
			Point np = new Point(currentXPos.intValue(), CPU_Y_POS.intValue());

			if(bus.isVirtual())
			{
				nrr.setDash();
				nrr.setForegroundColor(ColorConstants.darkGray);
			}

			nrr.setLocation(np);
			nrr.setSize(CPU_WIDTH, CPU_HEIGHT);
			tab.addFigure(nrr);


			//Draw Object timeline
			Long lineXPos = currentXPos + new Long(CPU_WIDTH/2);
			Long lineYStartPos = yPos;
			//Long lineYEndPos = tab.getVerticalSize();
			//drawTimeline(tab, lineXPos, lineYStartPos, lineXPos, lineYEndPos);
			timeLineStart.add(new Point(lineXPos.intValue(), lineYStartPos.intValue()));
			
			bus.setX(currentXPos + new Long((CPU_WIDTH/2))); 
			bus.setY(yPos);

			currentXPos = currentXPos + CPU_WIDTH + CPU_X_OFFSET;         
		}

		//Add spacer between top objects and timeline
		Line spacer = new Line(0L, CPU_Y_POS, 0L, yPos);
		spacer.setForegroundColor(ColorConstants.white);
		tab.addFigure(spacer);
	}
	
	@Override
	public void drawTimelines(GenericTabItem tab) {
		for(Point start : timeLineStart)
		{
			Long x = new Long(start.x);
			Long y = new Long(start.y);
			
			drawTimeline(tab, x, y, x, tab.getVerticalSize());
		}
	}
	

	@Override
	public void drawTimeMarker(GenericTabItem tab, Long markerTime) {

		Long markerStartX = BUS_X_START;
		Long markerStartY = tab.getYMax();
		Long markerEndX = tab.getXMax();
		Long markerEndY = markerStartY;

		//Draw horizontal marker line
		Line markerLine = new Line(markerStartX, markerStartY, markerEndX, markerEndY);
		markerLine.setForegroundColor(ColorConstants.lightGray);
		markerLine.setDot();
		tab.addBackgroundFigure(markerLine);

		//Draw time label
		String labelText = markerTime.toString();
		NormalLabel timeLabel = new NormalLabel(labelText, tab.getCurrentFont());	
		int labelStartX = markerStartX.intValue() - timeLabel.getSize().width;
		int labelStartY = markerStartY.intValue() - (int)(timeLabel.getSize().height/2);
		Point labelLocation = new Point(labelStartX, labelStartY);
		timeLabel.setLocation(labelLocation);
		tab.addBackgroundFigure(timeLabel);
	}

	//Threads
	public void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread swappedThread)
	{
		TraceObject obj = swappedThread.getCurrentObject();
		updateObject(tab, obj);
		
		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

		drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
		drawSwapImage(tab, x1, y1, SWAP_DIRECTION.EAST);

		obj.setY(y2);
	}

	public void drawThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread swappedThread)
	{
		TraceObject obj = swappedThread.getCurrentObject();
		updateObject(tab, obj);
		
		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

		drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
		drawSwapImage(tab, x1, y1, SWAP_DIRECTION.WEST);

		obj.setY(y2);
	}

	public void drawDelayedThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread swappedThread)
	{
		//MAA: Assumes from reverse engineering Tracefilevisitor that ThreadSwapIn = DelayedThreadSwapIn
		drawThreadSwapIn(tab, cpu, currentThread, swappedThread);
	}

	public void drawThreadKill(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread killedThread)
	{
		TraceObject obj = killedThread.getCurrentObject();
		updateObject(tab, obj);

		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

		drawMarker(tab, x1, y1, x2, y2, ColorConstants.red);

		obj.setY(y2);
	}

	public void drawThreadCreate(GenericTabItem tab, TraceCPU cpu, TraceThread currentThread, TraceThread newthread)
	{
		TraceObject obj = newthread.getCurrentObject();

		updateObject(tab, obj);

		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

		drawMarker(tab, x1, y1, x2, y2, ColorConstants.green);

		obj.setY(y2);
	}


	//Bus Messages
	public void drawMessageCompleted(GenericTabItem tab, TraceCPU cpu,  TraceThread thread, TraceBus bus, TraceOperation op, TraceObject obj)
	{
		updateObject(tab, obj);
		Long busX = bus.getX();
		String toolTipLabel = op.getName();
		Long objX = obj.getX();
		
		//Draw Bus Marker
		drawMarker(tab, busX, tab.getYMax(), busX, tab.getYMax() + ELEMENT_SIZE, ColorConstants.darkGray);	
		
		//Draw Message Arrow
		drawHorizontalArrow(tab, busX + BUSMSG_ARROW_OFFSET, objX - BUSMSG_ARROW_OFFSET, tab.getYMax(), " " , toolTipLabel, ColorConstants.darkGreen);
	}
	
	public void drawReplyRequest(GenericTabItem tab, TraceCPU cpu, TraceObject object, TraceBus bus, TraceOperation op)
	{
		updateObject(tab, object);
		Long busX = bus.getX();
		String toolTipLabel = " Return from  " + op.getName();

		Long objX = object.getX();
		Long arrowYPos = tab.getYMax();
		
		//Draw Bus Marker		
		drawMarker(tab, busX, tab.getYMax(), busX, tab.getYMax() + ELEMENT_SIZE, ColorConstants.lightGray);	

		//Draw message arrow
		drawHorizontalArrow(tab, objX - BUSMSG_ARROW_OFFSET, busX+BUSMSG_ARROW_OFFSET, arrowYPos, " " , toolTipLabel, ColorConstants.darkGreen);
		
	}
	
	public void drawMessageRequest(GenericTabItem tab, TraceCPU cpu, TraceObject object, TraceBus bus, TraceOperation op)
	{	
		updateObject(tab, object);
		Long busX = bus.getX();
		String toolTipLabel = " Call " + op.getName();

		Long objX = object.getX();
		Long arrowYPos = tab.getYMax();
		
		//Draw Bus Marker		
		drawMarker(tab, busX, tab.getYMax(), busX, tab.getYMax() + ELEMENT_SIZE, ColorConstants.lightGray);	

		//Draw message arrow
		drawHorizontalArrow(tab, objX - BUSMSG_ARROW_OFFSET, busX+BUSMSG_ARROW_OFFSET, arrowYPos, " " , toolTipLabel, ColorConstants.darkGreen);
	
	}

	public void drawMessageActivated(GenericTabItem tab, TraceCPU cpu, TraceObject object, TraceBus bus, TraceOperation op) {
		//Intentionally left blank : Ignore on CPU view
	}

	//Operations
	public void drawOpCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation )
	{
		TraceObject currentObj = thread.getCurrentObject();
		updateObject(tab, currentObj);
		if(currentObj.getId() == destinationObj.getId())
		{
			//Internal object operation
			updateObject(tab, currentObj);
			Long x1 = currentObj.getX();
			Long x2 = x1;
			Long y1 = tab.getYMax();
			Long y2 = y1 + ELEMENT_SIZE;

			NormalLabel lbl = new NormalLabel("C", tab.getCurrentFont());;

			String operationLabel = " Completed " + operation.getName() + " on object " + currentObj.getId();		
			NormalLabel ttl = new NormalLabel(operationLabel, tab.getCurrentFont());		
			Point pt = new Point(x1.intValue() + 8, y1.intValue() + 2);

			drawMarker(tab, x1, y1, x2, y2, ColorConstants.blue);
			lbl.setToolTip(ttl);
			lbl.setLocation(pt);
			tab.addFigure(lbl);
		} 
		else
		{
			updateObject(tab,destinationObj);
			drawObjectArrow(tab, destinationObj, currentObj, new String(""));
		}

	}

	public void drawOpActivate(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{
		TraceObject currentObj = thread.getCurrentObject();
		updateObject(tab, currentObj);

		if(currentObj.getId() == destinationObj.getId())
		{		
			//Internal Object Operation
			Long x1 = currentObj.getX();
			Long x2 = x1;
			Long y1 = tab.getYMax();
			Long y2 = y1.longValue() + ELEMENT_SIZE;
			Point pt = new Point(x1.intValue() + 8 , y1.intValue() + 2);

			String operationLabel = "A " + operation.getName();
			NormalLabel lbl = new NormalLabel(operationLabel, tab.getCurrentFont());

			drawMarker(tab, x1, y1, x2, y2, ColorConstants.blue);
			lbl.setLocation(pt);
			tab.addFigure(lbl);

			currentObj.setY(y2);
		} 
		else
		{
			//External Object Operation
			updateObject(tab, destinationObj);
			String operationName = operation.getName();

			drawObjectArrow(tab, currentObj, destinationObj, operationName);
		}
	} 

	public void drawOpRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{
		TraceObject obj = thread.getCurrentObject();
		updateObject(tab, obj);

		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1.longValue() + ELEMENT_SIZE;

		NormalLabel lbl = new NormalLabel("R", tab.getCurrentFont());

		String str = "";
		Boolean hasArguments = false; //TODO MAA
		if(hasArguments)
		{
			str = ("With argument: " + " TODO");
		} else
		{
			str = "";
		}

		String operationLabel = " Requested " + operation.getName() + " on object " + obj.getId().toString() + str + " ";
		NormalLabel ttl = new NormalLabel(operationLabel, tab.getCurrentFont());

		Point pt = new Point(x1.intValue() + 8, y1.intValue() + 2); 

		drawMarker(tab, x1, y1, x2, y2, ColorConstants.blue);
		lbl.setToolTip(ttl);
		lbl.setLocation(pt);
		tab.addFigure(lbl);

		obj.setY(y2);
	}

	//Helpers
	private void updateObject(GenericTabItem tab, TraceObject pobj)
	{
		if(pobj != null && !pobj.isVisible())
		{	
			//Draw Object
			String name = pobj.getName() + " (" + pobj.getId().toString() + ")";
			NormalLabel nlb = new NormalLabel(name, tab.getCurrentFont());;
			RectangleLabelFigure nrr = new RectangleLabelFigure(nlb);	
			
			Long objectXPos = tab.getXMax() + CPU_X_OFFSET;
			Long objectYPos = CPU_Y_POS + CPU_HEIGHT + ELEMENT_SIZE;
			Long objWidth = new Long(name.length())*OBJECT_WIDTH_FACTOR;

			Point np = new Point(objectXPos.intValue(), CPU_Y_POS.intValue());
			nrr.setLocation(np);
			nrr.setSize(objWidth, CPU_HEIGHT);
			tab.addFigure(nrr);

			//Save Object timeline
			Long lineXPos = objectXPos + new Long(objWidth/2);
			Long lineYStartPos = objectYPos;
			timeLineStart.add(new Point(lineXPos.intValue(), lineYStartPos.intValue()));
			
			//Update Object
			pobj.setY(objectYPos);			
			pobj.setVisible(true);
			pobj.setX(lineXPos);
		}
	}

	private void drawObjectArrow(GenericTabItem tab, TraceObject psrc, TraceObject pdest, String pstr)
	{
		Line verticalMarkerSource = new Line(psrc.getX(), tab.getYMax(), psrc.getX(), tab.getYMax() + ELEMENT_SIZE);
		verticalMarkerSource.setLineWidth(MARKER_THICKNESS);
		verticalMarkerSource.setForegroundColor(ColorConstants.blue);
		tab.addFigure(verticalMarkerSource);

		Long destionationY = tab.getYMax();
		drawHorizontalArrow(tab, psrc.getX(), pdest.getX(), tab.getYMax(), pstr, " ", ColorConstants.blue);

		Line verticalMarkerDestination = new Line(pdest.getX(), tab.getYMax() + ELEMENT_SIZE, pdest.getX(), destionationY);
		verticalMarkerDestination.setLineWidth(MARKER_THICKNESS);
		verticalMarkerDestination.setForegroundColor(ColorConstants.blue);
		tab.addFigure(verticalMarkerDestination);

	}
}
