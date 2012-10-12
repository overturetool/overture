package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.draw.CpuEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.OverviewEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.TraceEventViewer;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class EventHandler {
	
	public enum EventViewType {OVERVIEW, CPU};
	
	protected TraceData data;
	protected TraceEventViewer eventViewer;
	protected CpuEventViewer cpuViewer;
	protected OverviewEventViewer overviewViewer;

	public EventHandler(TraceData data)
	{
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
				//if(conjectureData.IsConjecturePresent(event.time))
				//{
				//	Vector<TraceCPU> cpus = data.GetCpuFromThreadId(conjectureData.GetConjectureThreadId(event.time));
				//  for(TraceCPU cpu : cpus)
				//	  overviewViewer.DrawConjecture(cpu, conjectureName);
				//}
				break;
			case CPU: eventViewer = cpuViewer; break;
			default: return false;
		}
		
		//Check event time and draw marker if needed
		if(data.getLastMarkerTime() == null || data.getLastMarkerTime() != event.getTime())
		{
			eventViewer.drawTimeMarker(tab, event.getTime());
			data.setLastMarkerTime(event.getTime());
		}
		
		//Handle the event
		return handle(event, tab);
	}
	
	protected abstract boolean handle(INextGenEvent event, GenericTabItem tab);

}