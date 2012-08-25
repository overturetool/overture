/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TraceData.java

package org.overture.ide.plugins.showtraceNextGen.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.management.RuntimeErrorException;
import org.overture.ide.plugins.showtraceNextGen.view.UnknownEventTypeException;
import org.overture.interpreter.messages.rtlog.nextgen.*;


public class TraceData
{
	private NextGenRTLogger rtLogger;   
	private HashMap<Long, TraceCPU> cpus;
	private HashMap<Long, TraceObject> objects;
	private HashMap<Long, TraceBus> buses; 
	private HashMap<Long, TraceThread> threads;
	private HashMap<Long, TraceBusMessage> messages;
	
	private TraceObject mainThreadObject;
	private TraceObject initThreadObject;
	
	private List<INextGenEvent> sortedEvents;
	private HashMap<Long, List<INextGenEvent>> cpuEvents;
	private Vector<Long> times; 
	
	private Long eventTime;
	
    public TraceData()
    {
    	rtLogger = NextGenRTLogger.getInstance();
    	cpus = new HashMap<Long, TraceCPU>();
    	objects = new HashMap<Long, TraceObject>();
    	buses = new HashMap<Long, TraceBus>();
    	threads = new HashMap<Long, TraceThread>();
    	messages = new HashMap<Long, TraceBusMessage>();
    	
    	mainThreadObject = new TraceObject(0L,"MAIN");
    	initThreadObject = new TraceObject(0L, "INIT");
    	
    	sortedEvents = null;
    	cpuEvents = new HashMap<Long, List<INextGenEvent>>();
    	times = null;
    	eventTime = null;
    }

    //CPU
    public TraceCPU getCPU(Long pid) throws RuntimeErrorException
    {
    	if(!rtLogger.getCpuMap().containsKey((int)(long)pid))
    		throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getCPU");

    	if(!cpus.containsKey(pid))
    	{
    		NextGenCpu cpu = rtLogger.getCpuMap().get(pid.intValue());
    		
    		Long cpuId = new Long(cpu.id);
    		String cpuName = cpu.name;
    		boolean isVirtual = !cpu.expl;
    		
    		cpus.put(cpuId, new TraceCPU(cpuId, cpuName, isVirtual));
    	}
    	
        return cpus.get(pid);
    }

    //TODO: Remove - is only used in TraceFileVisitor
    public HashSet<Long> getCPUIds()
    {
        HashSet<Long> cpuIds = new HashSet<Long>();
        Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
        for(Integer key : cpus.keySet())
        {
        	cpuIds.add(new Long(key));
        }

        return cpuIds;
    }

    public Vector<TraceCPU> getCPUs()
    {
    	Vector<TraceCPU> cpus = new Vector<TraceCPU>();
    	for(Long cpuId : getOrderedCpus())
    	{
    		TraceCPU cpu = getCPU(cpuId);
    		cpus.add(cpu);
    	}
    	
    	return cpus;
    }
    
    public Vector<Long> getOrderedCpus()
    {
        Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
        Vector<Long> tdCpuIds = new Vector<Long>(); 
        
        for(Integer key : cpus.keySet())
        {
        	tdCpuIds.add(new Long(key));
        }
                
        Collections.sort(tdCpuIds);     
        return tdCpuIds;
    }

    public Long getNoCpus()
    {
    	Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
    	return new Long(cpus.size());
    }

    //Bus
    public TraceBus getBUS(Long pid) throws RuntimeErrorException
    {   
        if(!rtLogger.getBusMap().containsKey(pid.intValue()))
        	 throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getBUS");
        
        if(!buses.containsKey(pid))
        {
        	NextGenBus bus = rtLogger.getBusMap().get(pid.intValue());
        	
        	Long id = new Long(bus.id);
        	String name = bus.name;
        	Boolean isVirtual =  (id.intValue() == rtLogger.getvBus().id);
        	Vector<Long> connectedCPUIds = new Vector<Long>();
        	
        	for(NextGenCpu connectedCPU : bus.cpus)
        	{
        		connectedCPUIds.add(new Long(connectedCPU.id));
        	}
        	
        	buses.put(pid, new TraceBus(id, name, isVirtual, connectedCPUIds));
        }
        
        return buses.get(pid);
    }

    //TODO: Remove - is only used in TraceFileVisitor
    public HashSet<Long> getBusIds()
    {      
        HashSet<Long> tdBusIds = new HashSet<Long>();
        Map<Integer, NextGenBus> buses = rtLogger.getBusMap();
        for(Integer key : buses.keySet())
        {
        	tdBusIds.add(new Long(key));
        }
        
        return tdBusIds;
    }

    public Vector<TraceBus> getBuses()
    {
    	Vector<TraceBus> buses = new Vector<TraceBus>();
    	for(Long busId : getOrderedBuses())
    	{
    		TraceBus bus = getBUS(busId);
    		buses.add(bus);
    	}
    	
    	return buses;
    }
    
    public Vector<Long> getOrderedBuses()
    {    	
    	Map<Integer, NextGenBus> buses = rtLogger.getBusMap();
    	Vector<Long> tdBusIds = new Vector<Long>();
    	
    	for(Integer key : buses.keySet())
    	{
    		tdBusIds.add(new Long(key));
    	}
    	
    	Collections.sort(tdBusIds);
    	
    	return tdBusIds;
    }

    public Long getNoBuses()
    {
        return new Long(rtLogger.getBusMap().size());
    }

    //Thread
    public TraceThread getThread(Long pthrid) throws RuntimeErrorException
    {
        if(!rtLogger.getThreadMap().containsKey(pthrid))
            throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getThread");
        
        if(!threads.containsKey(pthrid))
        {
        	threads.put(pthrid, new TraceThread(pthrid));
        }
        
        return threads.get(pthrid);
    }

    //Message
    public TraceBusMessage getMessage(Long pmsgid) throws RuntimeErrorException
    {
        if(!rtLogger.getBusMessage().containsKey(pmsgid))
            throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getMessage");
        
        if(!messages.containsKey(pmsgid))
        {
        	NextGenBusMessage message = rtLogger.getBusMessage().get(pmsgid);
        	
        	Long id = message.id;
        	Long busId = new Long(message.bus.id);
        	Long fromCpu = new Long(message.fromCpu.id);
        	Long toCpu = new Long(message.toCpu.id);
        	Long callerThread = new Long(message.callerThread.id);
        	
        	messages.put(pmsgid, new TraceBusMessage(id, busId, fromCpu, toCpu, callerThread));
        }
        
        return messages.get(pmsgid);
    }

    //Object
    public TraceObject getObject(Long pobjid) throws RuntimeErrorException
    {
        if(!rtLogger.getObjectMap().containsKey(pobjid.intValue()))
            throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getThread");
        
        if(!objects.containsKey(pobjid))
        {
        	NextGenObject object = rtLogger.getObjectMap().get(pobjid.intValue());
        	
        	Long id = new Long(object.id);
        	String name = object.classDef.name;
        	
        	objects.put(pobjid, new TraceObject(id, name));
        }
        
        return objects.get(pobjid);
    }

    public TraceObject getInitThreadObject()
    {
    	return initThreadObject;
    }
    
    public TraceObject getMainThreadObject()
    {
    	return mainThreadObject;
    }
    
    //Helpers
    public Vector<Long> getTimes()
    {
        return times;
    }

	public void reset()
    {
        cpus.clear();
        objects.clear();
        buses.clear();
        threads.clear();
        messages.clear();
        
        eventTime = null;
        
        
        
    }
	
	public Vector<TraceBus> getConnectedBuses(Long cpuId)
	{
    	Vector<TraceBus> res = new Vector<TraceBus>();
    	
    	Map<Integer, NextGenBus> buses = rtLogger.getBusMap(); 	
    	for(NextGenBus bus : buses.values())
    	{	
    		for (NextGenCpu cpu : bus.cpus) 
    		{
    			if(cpuId.intValue() == cpu.id)
    			{
    				res.add(getBUS(new Long(bus.id)));
    			}
			}  		
    	}
        return res; 
	}
	
	
	//TODO: Remove - is only used in TraceFileVisitor
    public Vector<Long> getBusIdsFromCpu(Long cpuId)
    {
    	Vector<Long> res = new Vector<Long>();
    	
    	Map<Integer, NextGenBus> buses = rtLogger.getBusMap(); 	
    	for(NextGenBus bus : buses.values())
    	{	
    		for (NextGenCpu cpu : bus.cpus) 
    		{
    			if(cpuId.intValue() == cpu.id)
    				res.add(new Long(bus.id));
			}  		
    	}
        return res; 
    }
    
    public Vector<Long> getObjectIdsFromCpu(Long cpuId)
    {
    	Vector<Long> res = new Vector<Long>();   	
    	Map<Integer, NextGenObject> objects = rtLogger.getObjectMap(); 
   		    	
    	for(NextGenObject obj : objects.values())
    	{	
			if(cpuId.intValue() == obj.cpu.id)
				res.add(new Long(obj.id));	  		
    	}
    	  	
        return res; 
    }
    
	public void sortEvents()
	{
		sortedEvents = new ArrayList<INextGenEvent>(rtLogger.getEvents());	
		//Collections.copy(sortedEvents, events);
		
		//Sort events
		Collections.sort(sortedEvents, new EventComparator());
			
		//Collect events belonging to each CPU
		//XXX: Can we do this smarter - maybe specify filter or sorting rules? 
		cpuEvents.clear();
		for(Integer iCpuId : rtLogger.getCpuMap().keySet())
		{
			Vector<INextGenEvent> result = new Vector<INextGenEvent>();	
			Long cpuId = new Long((int)iCpuId);
			
			for(INextGenEvent event : sortedEvents)
			{
				if(event instanceof NextGenThreadEvent)
	            {
	    			int eventCpu = ((NextGenThreadEvent)event).thread.cpu.id;
	    			if(eventCpu == cpuId)
	    			{
	    				result.add(event);
	    			}
	            }
	            else if(event instanceof NextGenOperationEvent)
	            {
	    			int eventCpu = ((NextGenOperationEvent)event).thread.cpu.id;
	    			if(eventCpu == cpuId)
	    			{
	    				result.add(event);
	    			}
	    			
	            }
	            else if(event instanceof NextGenBusMessageEvent)
	            {
	    			int fromCpu = ((NextGenBusMessageEvent)event).message.fromCpu.id;
	    			int toCpu =  ((NextGenBusMessageEvent)event).message.toCpu.id;
	    			if(fromCpu == cpuId || toCpu == cpuId)
	    			{
	    				result.add(event);
	    			}
	            }
	            else 
	            {
	            	throw new UnknownEventTypeException("Unknown event type!");
	            }
			}
			
			cpuEvents.put(cpuId, result);
		}
	}
	
	public List<INextGenEvent> getSortedEvents()
	{
		return sortedEvents;
	}
	
	public List<INextGenEvent> getSortedCpuEvents(Long cpuId)
	{
		return cpuEvents.get(cpuId);
	}

	public Long getEventTime()
	{
		return eventTime;
	}
	
	public void setEventTime(Long eventTime)
	{
		this.eventTime = eventTime;
	}
}