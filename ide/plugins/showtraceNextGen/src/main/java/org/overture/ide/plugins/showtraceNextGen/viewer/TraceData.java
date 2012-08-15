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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenBus;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenCpu;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, tdCPU, tdBUS, tdThread, 
//            tdMessage, tdObject
@SuppressWarnings({"unchecked","rawtypes"})
public class TraceData
{
	private NextGenRTLogger rtLogger;
	private Vector hist_utimes = new Vector();
    private HashMap hist_uevents = new HashMap();
    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private HashMap cpus;
    private Vector cpu_uorder;
    private HashMap buses;
    private Vector bus_uorder;
    private HashMap threads;
    private HashMap messages;
    private HashMap objects;
    
    public TraceData()
    {
    	rtLogger = NextGenRTLogger.getInstance();
        cpus = new HashMap();
        cpu_uorder = null;
        buses = new HashMap();
        bus_uorder = null;
        threads = new HashMap();
        messages = new HashMap();
        objects = new HashMap();
        try
        {
            cpus = new HashMap();
            cpu_uorder = new Vector();
            buses = new HashMap();
            bus_uorder = new Vector();
            threads = new HashMap();
            messages = new HashMap();
            objects = new HashMap();
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public tdCPU getCPU(Long pid)
        throws CGException
    {
        if(!rtLogger.getCpuMap().containsKey((int)(long)pid))
            UTIL.RunTime("Run-Time Error:Precondition failure in getCPU");
        
        return new tdCPU((int)(long)pid);
    }

    public HashSet getCPUs()
    {
        HashSet tdCpus = new HashSet();
        Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
        for(Integer key : cpus.keySet())
        {
        	tdCpus.add(new Long(key));
        }

        return tdCpus;
    }

    public Vector<Long> getOrderedCpus()
        throws CGException
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

    public tdBUS getBUS(Long pid)
        throws CGException
    {   
        if(!rtLogger.getBusMap().containsKey(pid.intValue()))
        	 UTIL.RunTime("Run-Time Error:Precondition failure in getBUS");
        
        return new tdBUS(pid.intValue());
    }

    public HashSet getBUSes()
        throws CGException
    {      
        HashSet tdBuses = new HashSet();
        Map<Integer, NextGenBus> buses = rtLogger.getBusMap();
        for(Integer key : buses.keySet())
        {
        	tdBuses.add(new Long(key));
        }
        
        return tdBuses;
    }

    public Vector getOrderedBuses()
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

    public void addThread(tdThread pthr)
        throws CGException
    {
        if(!pre_addThread(pthr).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in addThread");
        HashMap rhs_2 = new HashMap();
        HashMap var2_4 = new HashMap();
        Long tmpVar1_5 = null;
        tmpVar1_5 = pthr.getId();
        var2_4 = new HashMap();
        var2_4.put(tmpVar1_5, pthr);
        HashMap m1_11 = (HashMap)threads.clone();
        HashMap m2_12 = var2_4;
        HashSet com_7 = new HashSet();
        com_7.addAll((Collection)m1_11.keySet());
        com_7.retainAll((Collection)m2_12.keySet());
        boolean all_applies_8 = true;
        Object d_9;
        for(Iterator bb_10 = com_7.iterator(); bb_10.hasNext() && all_applies_8; all_applies_8 = m1_11.get(d_9).equals(m2_12.get(d_9)))
            d_9 = bb_10.next();

        if(!all_applies_8)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_11.putAll(m2_12);
        rhs_2 = m1_11;
        threads = (HashMap)UTIL.clone(rhs_2);
    }

    public Boolean pre_addThread(tdThread pthr)
        throws CGException
    {
        Boolean varRes_2 = null;
        Long var1_3 = null;
        var1_3 = pthr.getId();
        HashSet var2_4 = new HashSet();
        var2_4.clear();
        var2_4.addAll((Collection)threads.keySet());
        varRes_2 = new Boolean(!var2_4.contains(var1_3));
        return varRes_2;
    }

    public tdThread getThread(Long pthrid)
        throws CGException
    {
        if(!pre_getThread(pthrid).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getThread");
        return (tdThread)threads.get(pthrid);
    }

    public Boolean pre_getThread(Long pthrid)
        throws CGException
    {
        Boolean varRes_2 = null;
        varRes_2 = new Boolean(threads.containsKey(pthrid));
        return varRes_2;
    }

    public void addMessage(tdMessage pmsg)
        throws CGException
    {
        if(!pre_addMessage(pmsg).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in addMessage");
        HashMap rhs_2 = new HashMap();
        HashMap var2_4 = new HashMap();
        Long tmpVar1_5 = null;
        tmpVar1_5 = pmsg.getMsgId();
        var2_4 = new HashMap();
        var2_4.put(tmpVar1_5, pmsg);
        HashMap m1_11 = (HashMap)messages.clone();
        HashMap m2_12 = var2_4;
        HashSet com_7 = new HashSet();
        com_7.addAll((Collection)m1_11.keySet());
        com_7.retainAll((Collection)m2_12.keySet());
        boolean all_applies_8 = true;
        Object d_9;
        for(Iterator bb_10 = com_7.iterator(); bb_10.hasNext() && all_applies_8; all_applies_8 = m1_11.get(d_9).equals(m2_12.get(d_9)))
            d_9 = bb_10.next();

        if(!all_applies_8)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_11.putAll(m2_12);
        rhs_2 = m1_11;
        messages = (HashMap)UTIL.clone(rhs_2);
    }

    public Boolean pre_addMessage(tdMessage pmsg)
        throws CGException
    {
        Boolean varRes_2 = null;
        Long var1_3 = null;
        var1_3 = pmsg.getMsgId();
        HashSet var2_4 = new HashSet();
        var2_4.clear();
        var2_4.addAll((Collection)messages.keySet());
        varRes_2 = new Boolean(!var2_4.contains(var1_3));
        return varRes_2;
    }

    public tdMessage getMessage(Long pmsgid)
        throws CGException
    {
        if(!pre_getMessage(pmsgid).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getMessage");
        return (tdMessage)messages.get(pmsgid);
    }

    public Boolean pre_getMessage(Long pmsgid)
        throws CGException
    {
        Boolean varRes_2 = null;
        varRes_2 = new Boolean(messages.containsKey(pmsgid));
        return varRes_2;
    }

    public tdObject createObject(Long pobjid, String pclnm)
        throws CGException
    {
        if(!pre_createObject(pobjid, pclnm).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in createObject");
        tdObject obj = new tdObject(this, pobjid, pclnm);
        HashMap rhs_6 = new HashMap();
        HashMap var2_8 = new HashMap();
        var2_8 = new HashMap();
        var2_8.put(pobjid, obj);
        HashMap m1_15 = (HashMap)objects.clone();
        HashMap m2_16 = var2_8;
        HashSet com_11 = new HashSet();
        com_11.addAll((Collection)m1_15.keySet());
        com_11.retainAll((Collection)m2_16.keySet());
        boolean all_applies_12 = true;
        Object d_13;
        for(Iterator bb_14 = com_11.iterator(); bb_14.hasNext() && all_applies_12; all_applies_12 = m1_15.get(d_13).equals(m2_16.get(d_13)))
            d_13 = bb_14.next();

        if(!all_applies_12)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_15.putAll(m2_16);
        rhs_6 = m1_15;
        objects = (HashMap)UTIL.clone(rhs_6);
        return obj;
    }

    public Boolean pre_createObject(Long pobjid, String pclnm)
        throws CGException
    {
        Boolean varRes_3 = null;
        HashSet var2_5 = new HashSet();
        var2_5.clear();
        var2_5.addAll((Collection)objects.keySet());
        varRes_3 = new Boolean(!var2_5.contains(pobjid));
        return varRes_3;
    }

    public Boolean hasObject(Long pobjid)
        throws CGException
    {
        Boolean rexpr_2 = null;
        rexpr_2 = new Boolean(objects.containsKey(pobjid));
        return rexpr_2;
    }

    public tdObject getObject(Long pobjid)
        throws CGException
    {
        if(!pre_getObject(pobjid).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getObject");
        return (tdObject)objects.get(pobjid);
    }

    public Boolean pre_getObject(Long pobjid)
        throws CGException
    {
        return hasObject(pobjid);
    }

    public Vector getTimes()
    {
        return hist_utimes;
    }
    
	public Vector getHistory(Long ptime)
    {

        return (Vector)UTIL.ConvertToList(hist_uevents.get(ptime));
    }

	public void reset()
    {
        //TODO MAA: Reset data
    }


}