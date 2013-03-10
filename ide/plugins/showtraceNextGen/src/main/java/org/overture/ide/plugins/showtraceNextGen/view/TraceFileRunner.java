package org.overture.ide.plugins.showtraceNextGen.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.overture.ide.plugins.showtraceNextGen.data.Conjecture;
import org.overture.ide.plugins.showtraceNextGen.data.ConjectureData;
import org.overture.ide.plugins.showtraceNextGen.data.TraceCPU;
import org.overture.ide.plugins.showtraceNextGen.data.TraceData;
import org.overture.ide.plugins.showtraceNextGen.data.TraceThread;
import org.overture.ide.plugins.showtraceNextGen.draw.ArchitectureViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.CpuEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.OverviewEventViewer;
import org.overture.ide.plugins.showtraceNextGen.draw.TraceEventViewer;
import org.overture.ide.plugins.showtraceNextGen.event.BusMessageEventHandler;
import org.overture.ide.plugins.showtraceNextGen.event.BusMessageReplyEventHandler;
import org.overture.ide.plugins.showtraceNextGen.event.EventHandler;
import org.overture.ide.plugins.showtraceNextGen.event.EventHandler.EventViewType;
import org.overture.ide.plugins.showtraceNextGen.event.OperationEventHandler;
import org.overture.ide.plugins.showtraceNextGen.event.ThreadEventHandler;
import org.overture.ide.plugins.showtraceNextGen.event.ThreadSwapEventHandler;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadSwapEvent;

public class TraceFileRunner
{
	private TraceData data;
	private Map<Class<?>, EventHandler> eventHandlers;
	private ConjectureData conjectures;
	
	public TraceFileRunner(TraceData data, ConjectureData conjectures)
	{
		this.data = data;
		this.eventHandlers = new HashMap<Class<?>, EventHandler>();
		this.conjectures = conjectures;
		
		//Register Events
		eventHandlers.put(NextGenThreadEvent.class, new ThreadEventHandler(data));
		eventHandlers.put(NextGenThreadSwapEvent.class, new ThreadSwapEventHandler(data));
		eventHandlers.put(NextGenOperationEvent.class, new OperationEventHandler(data));
		eventHandlers.put(NextGenBusMessageEvent.class, new BusMessageEventHandler(data));
		eventHandlers.put(NextGenBusMessageReplyRequestEvent.class, new BusMessageReplyEventHandler(data));
	}

	public void drawArchitecture(GenericTabItem tab) throws Exception 
	{
		data.reset();
		ArchitectureViewer viewer = new ArchitectureViewer();
		viewer.drawArchitecture(tab, data.getCPUs(), data.getBuses());	
	}

	public void drawOverview(GenericTabItem tab, Long eventStartTime)
			throws Exception 
	{
		drawView(tab, eventStartTime, EventViewType.OVERVIEW, 0L);
	}

	public void drawCpu(GenericTabItem tab, Long cpuId, Long eventStartTime)
			throws Exception 
	{
		drawView(tab, eventStartTime, EventViewType.CPU, cpuId);		
	}
	
	private void drawView(GenericTabItem tab, Long eventStartTime, EventViewType viewType, Long cpuId) throws Exception
	{
		data.reset();
		TraceEventViewer viewer = null;
	
		//Draw pre-defined content
		//TODO: Refactor to make unaware of types including CPU sortings
		if(viewType == EventViewType.OVERVIEW)
		{
			viewer = new OverviewEventViewer();
			viewer.drawStaticItems(tab, data.getCPUs(), data.getBuses());
		}
		else if(viewType == EventViewType.CPU)
		{
			viewer = new CpuEventViewer();
			viewer.drawStaticItems(tab, null, data.getConnectedBuses(cpuId));
		}
			
		//Draw events as long as there is room and time
		Long conjectureTime = -1L;
		Long eventTime = 0L;
		EventViewType currentView = viewType;
		Long lastEventTime = data.getMaxEventTime();
		while(!tab.isCanvasOverrun() && eventTime <= lastEventTime) 
		{
			//Get all events at the current time
			ArrayList<INextGenEvent> currentEvents = data.getEvents(eventTime);
			
			//TODO: Remove DUMMY. Introduced to hack time travels
			currentView = (data.getCurrentEventTime() < eventStartTime) ? EventViewType.DUMMY : viewType;
			
			for(INextGenEvent event : currentEvents)
			{		
				if(viewType == EventViewType.CPU && !TraceData.isEventForCpu(event, cpuId)) 
					continue; //Ignore event for other CPU's
				
				//Draw conjectures on the overview
				if(viewType == EventViewType.OVERVIEW && eventTime != conjectureTime)
				{
					conjectureTime = eventTime;
					Vector<Conjecture> cons = conjectures.getConjecture(event.getTime().getAbsoluteTime());
					
					for(Conjecture c : cons)
					{
						TraceCPU cpu = data.getCpuFromThreadId(c.getThreadID());
						switch(c.getType())
						{
							case SOURCE: ((OverviewEventViewer)viewer).drawSourceConjecture(tab, cpu, c.getName()); break;
							case DESTINATION: ((OverviewEventViewer)viewer).drawDestinationConjecture(tab, cpu, c.getName()); break;
						}
					}
				}
				
				EventHandler handler = eventHandlers.get(event.getClass());
				handler.handleEvent(event, currentView, tab);					
			}

			eventTime = data.getCurrentEventTime() + 1; //Get next event time - MAA: Consider a more elegant way? Counter in data?
		}
		
		ArrayList<INextGenEvent> nextEvents = data.getEvents(eventTime);
		
		if(!nextEvents.isEmpty())
		{
			viewer.drawTimeMarker(tab, nextEvents.get(0).getTime().getAbsoluteTime());
			data.setLastMarkerTime(nextEvents.get(0).getTime().getAbsoluteTime());
		}
				
		
		if(viewer != null)
		{
			//FIXME - MVQ: Dirty hack in order to extend the blue line (Active/Blocked) to the end of canvas.
			if(viewType == EventViewType.OVERVIEW)
			{
				for(TraceCPU cpu : data.getCPUs())
				{
					TraceThread tThread = null;
					Long threadId = cpu.getCurrentThread();
					if(threadId != null)
					{
						tThread = data.getThread(threadId);
					}
					((OverviewEventViewer)viewer).updateCpu(tab, cpu, tThread);
				}
			}
			viewer.drawTimelines(tab);
		}
	}
	
	public Vector<Long> getCpuIds() 
	{
		return data.getOrderedCpus();
	}

	public String getCpuName(Long cpuId) 
	{
		return data.getCPU(cpuId).getName();
	}
}
