package org.overture.ide.plugins.rttraceviewer.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ide.plugins.rttraceviewer.data.Conjecture;
import org.overture.ide.plugins.rttraceviewer.data.ConjectureData;
import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.TraceEventManager;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.draw.ArchitectureViewer;
import org.overture.ide.plugins.rttraceviewer.draw.CpuEventViewer;
import org.overture.ide.plugins.rttraceviewer.draw.DummyViewer;
import org.overture.ide.plugins.rttraceviewer.draw.OverviewEventViewer;
import org.overture.ide.plugins.rttraceviewer.draw.TraceEventViewer;
import org.overture.ide.plugins.rttraceviewer.event.BusMessageEventHandler;
import org.overture.ide.plugins.rttraceviewer.event.BusMessageReplyEventHandler;
import org.overture.ide.plugins.rttraceviewer.event.CPUEventFilter;
import org.overture.ide.plugins.rttraceviewer.event.EventFilter;
import org.overture.ide.plugins.rttraceviewer.event.EventHandler;
import org.overture.ide.plugins.rttraceviewer.event.OperationEventHandler;
import org.overture.ide.plugins.rttraceviewer.event.OverviewEventFilter;
import org.overture.ide.plugins.rttraceviewer.event.ThreadEventHandler;
import org.overture.ide.plugins.rttraceviewer.event.ThreadSwapEventHandler;
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
		data.reset();
		TraceEventViewer viewer = new OverviewEventViewer();
		viewer.drawStaticItems(tab, data.getCPUs(), data.getBuses());
		
		drawView(tab, eventStartTime, viewer, new OverviewEventFilter());
		
		//FIXME - MVQ: Dirty hack in order to extend the blue line (Active/Blocked) to the end of canvas.
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

	public void drawCpu(GenericTabItem tab, Long cpuId, Long eventStartTime)
			throws Exception 
	{
		data.reset();
		TraceEventViewer viewer = new CpuEventViewer();
		viewer.drawStaticItems(tab, null, data.getConnectedBuses(cpuId));
		
		drawView(tab, eventStartTime, viewer, new CPUEventFilter(cpuId));		
	}
	
	private void drawView(GenericTabItem tab, Long eventStartTime, TraceEventViewer viewer, EventFilter filter) throws Exception
	{		
		//Draw events as long as there is room and time
		TraceEventManager eventManager = data.getEventManager();
		TraceEventViewer dummyViewer = new DummyViewer();
		TraceEventViewer currentView = null;
		List<INextGenEvent> events = eventManager.getEvents(0L); //First series of events
		
		while(!tab.isCanvasOverrun() && events != null) 
		{
			//TODO: Remove DUMMY. Introduced to hack time travels
			currentView = (eventManager.getCurrentEventTime() < eventStartTime) ? dummyViewer : viewer;
			
			//Draw conjectures on the overview
			if(viewer instanceof OverviewEventViewer)
			{
				Vector<Conjecture> cons = conjectures.getConjecture(eventManager.getCurrentEventTime());
				
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
			
			for(INextGenEvent event : events)
			{	
				if(filter.apply(event)) { 
					EventHandler handler = eventHandlers.get(event.getClass());
					handler.handleEvent(event, currentView, tab);
				}				
			}

			events = eventManager.getEvents();
		}
		
		//Draw a final time marker indicating the time for the next (not drawn) series of events
		if(events != null) {
			viewer.drawTimeMarker(tab, events.get(0).getTime().getAbsoluteTime());
		}
				
		//Finally add timelines 
		viewer.drawTimelines(tab);
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
