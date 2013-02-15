package org.overture.ide.plugins.showtraceNextGen.data;

import java.util.Vector;

import org.overture.ide.plugins.showtraceNextGen.draw.CpuEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.OverviewEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.TraceEventViewer;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class EventHandler {
	
	public enum EventViewType {OVERVIEW, CPU};
	
	protected ConjectureData conjectures;
	protected TraceData data;
	protected TraceEventViewer eventViewer;
	protected CpuEventViewer cpuViewer;
	protected OverviewEventViewer overviewViewer;

	public EventHandler(TraceData data, ConjectureData conjectures)
	{
		this.conjectures = conjectures;
		this.data = data;
		this.cpuViewer = new CpuEventViewer();
		this.overviewViewer = new OverviewEventViewer();
	}
	
	public boolean handleEvent(Object e, EventViewType viewType, GenericTabItem tab)
	{
		INextGenEvent event = (INextGenEvent)e;
		if(event == null) return false; //Guard
		
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
			default: return false;
		}
		
		//Check event time and draw marker if needed
		if(data.getLastMarkerTime() == null || data.getLastMarkerTime() != event.getTime().getAbsoluteTime())
		{
			eventViewer.drawTimeMarker(tab, event.getTime().getAbsoluteTime());
			data.setLastMarkerTime(event.getTime().getAbsoluteTime());
		}
		
		//Handle the event
		return handle(event, tab);
	}
	
	protected abstract boolean handle(INextGenEvent event, GenericTabItem tab);

}