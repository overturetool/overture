package org.overture.ide.plugins.rttraceviewer.event;

import java.util.Vector;

import org.overture.ide.plugins.rttraceviewer.data.Conjecture;
import org.overture.ide.plugins.rttraceviewer.data.ConjectureData;
import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.UnexpectedEventTypeException;
import org.overture.ide.plugins.rttraceviewer.draw.CpuEventViewer;
import org.overture.ide.plugins.rttraceviewer.draw.DummyViewer;
import org.overture.ide.plugins.rttraceviewer.draw.OverviewEventViewer;
import org.overture.ide.plugins.rttraceviewer.draw.TraceEventViewer;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;

public abstract class EventHandler {
	
	//TODO: Remove DUMMY type and viewer. Hack to handle timeshifting
	public enum EventViewType {OVERVIEW, CPU, DUMMY};
	
	protected TraceData data;
	protected TraceEventViewer eventViewer;
	protected CpuEventViewer cpuViewer;
	protected OverviewEventViewer overviewViewer;
	protected DummyViewer dummyViewer;

	public EventHandler(TraceData data)
	{
		this.data = data;
		this.cpuViewer = new CpuEventViewer();
		this.overviewViewer = new OverviewEventViewer();
		this.dummyViewer = new DummyViewer();
	}
	
	public void handleEvent(Object e, EventViewType viewType, GenericTabItem tab)
	{
		INextGenEvent event = null;
		
		if(e instanceof INextGenEvent)
			event = (INextGenEvent)e;
		else
			throw new IllegalArgumentException("EventHandler expected event of type: " + INextGenEvent.class.getName());
				
		//Set viewer used in handle
		switch(viewType)
		{
			case OVERVIEW: eventViewer = overviewViewer; break;
			case CPU: eventViewer = cpuViewer; break;
			case DUMMY: eventViewer = dummyViewer; break;
			default: throw new UnexpectedEventTypeException("Got unexpected view type in EventHandler");
		}
		
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
