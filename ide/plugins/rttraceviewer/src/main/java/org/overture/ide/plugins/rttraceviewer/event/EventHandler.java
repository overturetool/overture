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
