package org.overture.ide.plugins.showtraceNextGen.event;

import java.util.Vector;

import org.overture.ide.plugins.showtraceNextGen.data.Conjecture;
import org.overture.ide.plugins.showtraceNextGen.data.ConjectureData;
import org.overture.ide.plugins.showtraceNextGen.data.TraceCPU;
import org.overture.ide.plugins.showtraceNextGen.data.TraceData;
import org.overture.ide.plugins.showtraceNextGen.data.UnexpectedEventTypeException;
import org.overture.ide.plugins.showtraceNextGen.draw.CpuEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.DummyViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.OverviewEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.TraceEventViewer;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;

public abstract class EventHandler {
	
	//TODO: Remove DUMMY type and viewer. Hack to handle timeshifting
	public enum EventViewType {OVERVIEW, CPU, DUMMY};
	
	protected ConjectureData conjectures;
	protected TraceData data;
	protected TraceEventViewer eventViewer;
	protected CpuEventViewer cpuViewer;
	protected OverviewEventViewer overviewViewer;
	protected DummyViewer dummyViewer;

	public EventHandler(TraceData data, ConjectureData conjectures)
	{
		this.conjectures = conjectures;
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
			case OVERVIEW: 
				eventViewer = overviewViewer;
				
				//Draw conjectures on the overview
				Vector<Conjecture> cons = conjectures.getConjecture(event.getTime().getAbsoluteTime());
				
				for(Conjecture c : cons)
				{
					TraceCPU cpu = data.getCpuFromThreadId(c.getThreadID());
					switch(c.getType())
					{
						case SOURCE: overviewViewer.drawSourceConjecture(tab, cpu, c.getName());
						case DESTINATION: overviewViewer.drawDestinationConjecture(tab, cpu, c.getName());
					}
				}

				break;
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