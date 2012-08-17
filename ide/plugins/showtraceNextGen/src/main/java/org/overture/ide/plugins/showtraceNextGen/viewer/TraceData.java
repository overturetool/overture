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

package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.management.RuntimeErrorException;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overture.interpreter.messages.rtlog.nextgen.*;


// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, tdCPU, tdBUS, tdThread, 
//            tdMessage, tdObject
@SuppressWarnings({"unchecked","rawtypes"})
public class TraceData
{
	private NextGenRTLogger rtLogger;   
	private HashMap<Long, tdCPU> cpus;
	private HashMap<Long, tdObject> objects;
	private HashMap<Long, tdBUS> buses; 
	private HashMap<Long, tdThread> threads;
	private HashMap<Long, tdMessage> messages;
	private tdObject mainThreadObject;
	private tdObject initThreadObject;
	
	
    public TraceData()
    {
    	rtLogger = NextGenRTLogger.getInstance();
    	cpus = new HashMap<Long, tdCPU>();
    	objects = new HashMap<Long, tdObject>();
    	buses = new HashMap<Long, tdBUS>();
    	threads = new HashMap<Long, tdThread>();
    	messages = new HashMap<Long, tdMessage>();
    	
    	mainThreadObject = new tdObject(0L,"MAIN");
    	initThreadObject = new tdObject(0L, "INIT");
    }

    public tdCPU getCPU(Long pid) throws RuntimeErrorException
    {
    	if(!rtLogger.getCpuMap().containsKey((int)(long)pid))
    		throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getCPU");

    	if(!cpus.containsKey(pid))
    	{
    		cpus.put(pid, new tdCPU(pid.intValue()));
    	}
    	
        return cpus.get(pid);
    }

    public HashSet<Long> getCPUs()
    {
        HashSet cpuIds = new HashSet();
        Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
        for(Integer key : cpus.keySet())
        {
        	cpuIds.add(new Long(key));
        }

        return cpuIds;
    }

    public Vector<Long> getOrderedCpus()
    {
        Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
        Vector<Long> tdCpuIds = new Vector(); 
        
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

    public tdBUS getBUS(Long pid) throws RuntimeErrorException
    {   
        if(!rtLogger.getBusMap().containsKey(pid.intValue()))
        	 throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getBUS");
        
        if(!buses.containsKey(pid))
        {
        	buses.put(pid, new tdBUS(pid.intValue()));
        }
        
        return buses.get(pid);
    }

    public HashSet<Long> getBUSes()
    {      
        HashSet tdBusIds = new HashSet();
        Map<Integer, NextGenBus> buses = rtLogger.getBusMap();
        for(Integer key : buses.keySet())
        {
        	tdBusIds.add(new Long(key));
        }
        
        return tdBusIds;
    }

    public Vector<Long> getOrderedBuses()
    {    	
    	Map<Integer, NextGenBus> buses = rtLogger.getBusMap();
    	Vector<Long> tdBusIds = new Vector();
    	
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

    public tdThread getThread(Long pthrid) throws RuntimeErrorException
    {
        if(!rtLogger.getThreadMap().containsKey(pthrid))
            throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getThread");
        
        if(!threads.containsKey(pthrid))
        {
        	threads.put(pthrid, new tdThread(pthrid));
        }
        
        return threads.get(pthrid);
    }

    public tdMessage getMessage(Long pmsgid) throws RuntimeErrorException
    {
        if(!rtLogger.getBusMessage().containsKey(pmsgid))
            throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getMessage");
        
        if(!messages.containsKey(pmsgid))
        {
        	messages.put(pmsgid, new tdMessage(pmsgid));
        }
        
        return messages.get(pmsgid);
    }

    public tdObject getObject(Long pobjid) throws RuntimeErrorException
    {
        if(!rtLogger.getObjectMap().containsKey(pobjid.intValue()))
            throw new RuntimeErrorException(null, "Run-Time Error:Precondition failure in getThread");
        
        if(!objects.containsKey(pobjid))
        {
        	objects.put(pobjid, new tdObject(pobjid));
        }
        
        return objects.get(pobjid);
    }

    public tdObject getInitThreadObject()
    {
    	return initThreadObject;
    }
    
    public tdObject getMainThreadObject()
    {
    	return mainThreadObject;
    }
    
    public Vector getTimes()
    {
    	//FIXME MAA: MUST BE CHANGED TO SOMETHING MORE EFFECTIVE
        List<INextGenEvent> events = rtLogger.getEvents();
        Vector<Long> times = new Vector<Long>();
        for(INextGenEvent event : events)
        {
        	times.add(new Long(event.getTime()));
        }
        return times;
    }

	public List<INextGenEvent> getSortedEvents()
	{
		rtLogger.sortEvents();
		return rtLogger.getEvents();
	}

	public void reset()
    {
        cpus.clear();
        objects.clear();
        buses.clear();
        threads.clear();
        messages.clear();
    }

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
    

	public Vector<INextGenEvent> getSortedCpuEvents(Integer cpuId)
	{

		List<INextGenEvent> sortedEvents = getSortedEvents();
		Vector<INextGenEvent> result = new Vector<INextGenEvent>();
		
		//FIXME MAA: Consider moving the iteration to constructor to only do it once?	
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
		
		return result;
	}
}