package org.overture.ide.plugins.showtraceNextGen.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.overture.ide.plugins.showtraceNextGen.data.*;
import org.overture.ide.plugins.showtraceNextGen.data.EventHandler.EventViewType;
import org.overture.ide.plugins.showtraceNextGen.draw.*;
import org.overture.interpreter.messages.rtlog.nextgen.*;

public class TraceFileRunner implements ITraceRunner 
{
	private TraceData data;
	private Map<Class<?>, EventHandler> eventHandlers;
	
	public TraceFileRunner(TraceData data)
	{
		this.data = data;
		this.eventHandlers = new HashMap<Class<?>, EventHandler>();
		
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
		
		//Draw pre-defined content
		if(viewType == EventViewType.OVERVIEW)
		{
			OverviewEventViewer viewer = new OverviewEventViewer();
			viewer.drawOverview(tab, data.getCPUs(), data.getBuses());
		}
		else if(viewType == EventViewType.CPU)
		{
			CpuEventViewer viewer = new CpuEventViewer();
			viewer.drawView(tab, data.getConnectedBuses(cpuId));
		}
			
		//Draw events as long as there is room and time
		Long eventTime = eventStartTime;
		Long lastEventTime = data.getMaxEventTime();
		while(!tab.isCanvasOverrun() && eventTime <= lastEventTime) 
		{
			//Get all events at the current time
			for(Object event : data.getEvents(eventTime))
			{		
				if(viewType == EventViewType.CPU && !data.isEventForCpu(event, cpuId)) 
					continue; //Ignore event for other CPU's
				
				EventHandler handler = eventHandlers.get(event.getClass());
				
				if(handler == null)
					throw new Exception("No eventhandler registered for event: " + event.getClass());

				if(!handler.handleEvent(event, viewType, tab))
					throw new Exception("Failed to handle Overview event: " + event.getClass());						
			}
			
			eventTime = data.getCurrentEventTime() + 1; //Get next event time - MAA: Consider a more elegant way? Counter in data?
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
