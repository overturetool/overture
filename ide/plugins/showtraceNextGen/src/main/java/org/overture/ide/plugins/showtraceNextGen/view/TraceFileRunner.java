package org.overture.ide.plugins.showtraceNextGen.view;

import java.util.ArrayList;
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
	
	public TraceFileRunner(TraceData data, ConjectureData conjectures)
	{
		this.data = data;
		this.eventHandlers = new HashMap<Class<?>, EventHandler>();
		
		//Register Events
		eventHandlers.put(NextGenThreadEvent.class, new ThreadEventHandler(data, conjectures));
		eventHandlers.put(NextGenThreadSwapEvent.class, new ThreadSwapEventHandler(data, conjectures));
		eventHandlers.put(NextGenOperationEvent.class, new OperationEventHandler(data, conjectures));
		eventHandlers.put(NextGenBusMessageEvent.class, new BusMessageEventHandler(data, conjectures));
		eventHandlers.put(NextGenBusMessageReplyRequestEvent.class, new BusMessageReplyEventHandler(data, conjectures));
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
		Long eventTime = 0L;
		EventViewType currentView = viewType;
		Long lastEventTime = data.getMaxEventTime();
		while(!tab.isCanvasOverrun() && eventTime <= lastEventTime) 
		{
			//Get all events at the current time
			ArrayList<INextGenEvent> currentEvents = data.getEvents(eventTime);
			
			//TODO: Remove DUMMY. Introduced to hack time travels
			currentView = (data.getCurrentEventTime() < eventStartTime) ? EventViewType.DUMMY : viewType;
			
			for(Object event : currentEvents)
			{		
				if(viewType == EventViewType.CPU && !TraceData.isEventForCpu(event, cpuId)) 
					continue; //Ignore event for other CPU's
				
				EventHandler handler = eventHandlers.get(event.getClass());
				handler.handleEvent(event, currentView, tab);					
			}

			eventTime = data.getCurrentEventTime() + 1; //Get next event time - MAA: Consider a more elegant way? Counter in data?
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
