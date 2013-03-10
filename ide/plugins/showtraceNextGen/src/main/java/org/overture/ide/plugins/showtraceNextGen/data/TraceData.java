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
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.management.RuntimeErrorException;
import org.overture.interpreter.messages.rtlog.nextgen.*;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent.NextGenBusMessageEventType;


public class TraceData
{
	private NextGenRTLogger rtLogger;   
	private HashMap<Long, TraceCPU> cpus;
	private HashMap<Long, TraceObject> objects;
	private HashMap<Long, TraceBus> buses; 
	private HashMap<Long, TraceThread> threads;
	private HashMap<Long, TraceBusMessage> messages;
	private HashMap<String, TraceOperation> operations; //Key = Class+Operation
	
	private TraceObject mainThreadObject;
	
	private Long currentEventTime;
	private Long lastMarkerTime;
	
    public TraceData(NextGenRTLogger logger)
    {
    	//Pass by reference is needed to avoid problems when switching between two RT models (different NextGenRTLogger instances)
    	rtLogger = logger;//NextGenRTLogger.getInstance();
    	 	
    	cpus = new HashMap<Long, TraceCPU>();
    	objects = new HashMap<Long, TraceObject>();
    	buses = new HashMap<Long, TraceBus>();
    	threads = new HashMap<Long, TraceThread>();
    	messages = new HashMap<Long, TraceBusMessage>();
    	operations = new HashMap<String, TraceOperation>();
    	
    	mainThreadObject = new TraceObject(0L,"MAIN");

    	reset();
    }

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

    public Vector<TraceBus> getBuses()
    {
    	Vector<TraceBus> buses = new Vector<TraceBus>();
    	for(Integer busId : rtLogger.getBusMap().keySet())
    	{
    		TraceBus bus = getBUS(new Long(busId));
    		buses.add(bus);
    	}
    	
    	return buses;
    }

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

    public TraceOperation getOperation(String classNameOperationName)
    {
        if(!rtLogger.getOperationMap().containsKey(classNameOperationName))
            throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getOpreation");
        
        if(!messages.containsKey(classNameOperationName))
        {
        	NextGenOperation message = rtLogger.getOperationMap().get(classNameOperationName);
        	
        	String opName = message.name;
        	Boolean isStatic = message.isStatic;
        	Boolean isAsync = message.isAsync;
        	String clName = message.classDef.name;
        	
        	operations.put(classNameOperationName, new TraceOperation(opName, isAsync, isStatic, clName));
        }
        
        return operations.get(classNameOperationName);
    }

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

    public TraceObject getMainThreadObject()
    {
    	return mainThreadObject;
    }
    
	public void reset()
    {
		cpus.clear();
        objects.clear();
        buses.clear();
        threads.clear();
        messages.clear();
        operations.clear();
        
        currentEventTime = null;
        lastMarkerTime = null;
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

	public TraceCPU getCpuFromThreadId(Long threadID)
	{
		if(!rtLogger.getThreadMap().containsKey(threadID))
			throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getCpuFromThreadId");
		
		int CpuId = rtLogger.getThreadMap().get(threadID).cpu.id;
		
		return getCPU(new Long(CpuId));
	}
	
	//Events
	public Long getMaxEventTime()
	{
		return getEvents().lastKey();
	}

	public Long getCurrentEventTime()
	{
		return currentEventTime;
	}
	
	public Long getLastMarkerTime()
	{
		return lastMarkerTime;
	}
	
	public void setLastMarkerTime(Long time)
	{
		lastMarkerTime = time;
	}
	
	public Vector<Long> getEventTimes()
	{
		return new Vector<Long>(getEvents().keySet());
	}
	
	private TreeMap<Long, ArrayList<INextGenEvent>> getEvents()
	{
		return (TreeMap<Long, ArrayList<INextGenEvent>>)rtLogger.getEvents();
	}
	
	public ArrayList<INextGenEvent> getEvents(Long fromTime)
	{
		ArrayList<INextGenEvent> events = null;
		
		Long eventKey = getEvents().ceilingKey(fromTime); //Null if no key equal to or greater than
	    if(eventKey != null)
	    {
	    	events = getEvents().get(eventKey);
	    	currentEventTime = eventKey;
	    }
	    else
	    {
	    	return events = new ArrayList<INextGenEvent>();
	    }
				
		return events;
	}
	
	public static boolean isEventForCpu(Object e, Long cpuId)
	{
		INextGenEvent event = (INextGenEvent)e;
		if(event == null) return false; //Guard
		
		boolean isForThisCpu = false;
		
		if(event instanceof NextGenThreadEvent)
        {
			int eventCpu = ((NextGenThreadEvent)event).thread.cpu.id;
			if(eventCpu == cpuId)
			{
				isForThisCpu = true;
			}
        }
        else if(event instanceof NextGenOperationEvent)
        {
			isForThisCpu = (((NextGenOperationEvent)event).thread.cpu.id == cpuId.intValue());			
        }
        else if(event instanceof NextGenBusMessageReplyRequestEvent)
        {
        	int fromCpu = ((NextGenBusMessageReplyRequestEvent)event).replyMessage.fromCpu.id;
        	isForThisCpu = (fromCpu == cpuId.intValue());
        }
        else if(event instanceof NextGenBusMessageEvent)
        {
        	NextGenBusMessageEvent busMsg = (NextGenBusMessageEvent)event;
        	if(busMsg.type != NextGenBusMessageEventType.ACTIVATE)
        	{
				int fromCpu = busMsg.message.fromCpu.id;
				int toCpu =  busMsg.message.toCpu.id;
				
				switch(busMsg.type)
				{
					case ACTIVATE: 		isForThisCpu = (fromCpu == cpuId); break;
					case COMPLETED: 	isForThisCpu = (toCpu == cpuId); break;
					//case REPLY_REQUEST: isForThisCpu = (fromCpu == cpuId); break;
					case REQUEST: 		isForThisCpu = (fromCpu == cpuId); break;
					default: 			isForThisCpu = false;
				}

        	}
        }
        else 
        {
        	throw new IllegalArgumentException("Unknown event type!");
        }
		
		return isForThisCpu;
	}

}