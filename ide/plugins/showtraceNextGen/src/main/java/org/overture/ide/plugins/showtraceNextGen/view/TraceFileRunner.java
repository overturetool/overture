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
	
	public void addFailedUpper(Long ptime, Long pthr, String pname) 
	{
		// TODO Auto-generated method stub
	}

	public void addFailedLower(Long ptime, Long pthr, String pname) 
	{
		// TODO Auto-generated method stub
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
		data.reset();
		
		OverviewEventViewer viewer = new OverviewEventViewer();
		viewer.drawOverview(tab, data.getCPUs(), data.getBuses());
		
		for(INextGenEvent event : data.getSortedEvents())
		{
			
			EventHandler handler = eventHandlers.get(event.getClass());
			
			if(handler == null)
				throw new Exception("No eventhandler registered for event: " + event.getClass());

			if(!handler.handleEvent(event, EventViewType.OVERVIEW, tab))
				System.out.println("Failed to handle Overview event: " + event.getClass());		
			
		}
	}

	public void drawCpu(GenericTabItem tab, Long cpuId, Long eventStartTime)
			throws Exception 
	{
		data.reset();
		
		CpuEventViewer viewer = new CpuEventViewer();
		viewer.drawView(tab, data.getConnectedBuses(cpuId));
		
		for(INextGenEvent event : data.getSortedCpuEvents(cpuId))
		{

			
			EventHandler handler = eventHandlers.get(event.getClass());
			
			if(handler == null)
				throw new Exception("No eventhandler registered for event: " + event.getClass());

			if(!handler.handleEvent(event, EventViewType.CPU, tab))
				System.out.println("Failed to handle CPU event: " + event.getClass());			
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
