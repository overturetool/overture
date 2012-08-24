package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.draw.*;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class EventHandler {
	
	public enum EventViewType {OVERVIEW, CPU};
	
	protected TraceData data;
	protected TraceEventViewer eventViewer;
	protected CpuEventViewer cpuViewer;
	protected OverviewEventViewer overviewViwer;

	public EventHandler(TraceData data)
	{
		this.data = data;
		this.cpuViewer = new CpuEventViewer();
		this.overviewViwer = new OverviewEventViewer();
	}
	
	public boolean handleEvent(INextGenEvent event, EventViewType viewType, GenericTabItem tab)
	{
		//Set viewer used in handle
		switch(viewType)
		{
			case OVERVIEW: eventViewer = overviewViwer;	break;
			case CPU: eventViewer = cpuViewer; break;
			default: return false;
		}
		
		//Check event time and draw marker if needed
		if(data.getEventTime() == null || data.getEventTime() != event.getTime())
		{
			eventViewer.drawTimeMarker(tab, event.getTime());
			data.setEventTime(event.getTime());
		}
		
		//Handle the event
		return handle(event, tab);
	}
	
	protected abstract boolean handle(INextGenEvent event, GenericTabItem tab);

}