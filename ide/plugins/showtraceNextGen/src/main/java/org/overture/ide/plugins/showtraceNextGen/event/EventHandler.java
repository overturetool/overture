package org.overture.ide.plugins.showtraceNextGen.event;

import java.util.Vector;

import org.overture.ide.plugins.showtraceNextGen.data.Conjecture;
import org.overture.ide.plugins.showtraceNextGen.data.ConjectureData;
import org.overture.ide.plugins.showtraceNextGen.data.TraceCPU;
import org.overture.ide.plugins.showtraceNextGen.data.TraceData;
import org.overture.ide.plugins.showtraceNextGen.draw.OverviewEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.TraceEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.TraceViewer;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class EventHandler {
	
	//TODO: Remove DUMMY type and viewer. Hack to handle timeshifting
	public enum EventViewType {OVERVIEW, CPU, DUMMY};
	
	protected ConjectureData conjectures;
	protected TraceData data;
	protected TraceEventViewer eventViewer;
	public EventHandler(TraceData data, ConjectureData conjectures)
	{
		this.conjectures = conjectures;
		this.data = data;
	}
	
	public void handleEvent(INextGenEvent event, TraceViewer view, GenericTabItem tab)
	{
		eventViewer = (TraceEventViewer)view;
		if(eventViewer instanceof OverviewEventViewer) {
			//Draw conjectures on the overview
			OverviewEventViewer overviewView = (OverviewEventViewer)view;
			Vector<Conjecture> cons = conjectures.getConjecture(event.getTime().getAbsoluteTime());
			
			for(Conjecture c : cons)
			{
				TraceCPU cpu = data.getCpuFromThreadId(c.getThreadID());
				switch(c.getType())
				{
					case SOURCE: 
						overviewView.drawSourceConjecture(tab, cpu, c.getName());
						break;
					case DESTINATION: 
						overviewView.drawDestinationConjecture(tab, cpu, c.getName());
						break;
				}
			}
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