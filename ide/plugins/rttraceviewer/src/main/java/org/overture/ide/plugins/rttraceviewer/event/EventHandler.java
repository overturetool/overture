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
package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.draw.TraceEventViewer;
import org.overture.ide.plugins.rttraceviewer.draw.TraceViewer;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class EventHandler {
	
	protected TraceData data;
	protected TraceEventViewer eventViewer;

	public EventHandler(TraceData data)
	{
		this.data = data;
	}
	
	public void handleEvent(INextGenEvent event, TraceViewer view, GenericTabItem tab)
	{
		eventViewer = (TraceEventViewer)view;

		//Check event time and draw marker if needed
		if(data.getLastMarkerTime() == null || data.getLastMarkerTime() != event.getTime().getAbsoluteTime())
		{
			eventViewer.drawTimeMarker(tab, event.getTime().getAbsoluteTime());
			data.setLastMarkerTime(event.getTime().getAbsoluteTime());
		}

		//Handle the event
		handle(event, tab);
	}
	
	protected abstract void handle(INextGenEvent event, GenericTabItem tab);

}
