// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TraceData.java

package org.overture.ide.plugins.showtrace.viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, tdCPU, tdBUS, tdThread, 
//            tdMessage, tdObject
@SuppressWarnings("unchecked")
public class TraceData extends tdHistory
{

    public TraceData()
        throws CGException
    {
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

    public tdCPU createCPU(Long pid, String pname, Boolean pvirt)
        throws CGException
    {
        if(!pre_createCPU(pid, pname, pvirt).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in createCPU");
        tdCPU cpu = new tdCPU(this, pid, pname, pvirt);
        HashMap rhs_8 = new HashMap();
        HashMap var2_10 = new HashMap();
        var2_10 = new HashMap();
        var2_10.put(pid, cpu);
        HashMap m1_17 = (HashMap)cpus.clone();
        HashMap m2_18 = var2_10;
        HashSet com_13 = new HashSet();
        com_13.addAll(m1_17.keySet());
        com_13.retainAll((Collection)m2_18.keySet());
        boolean all_applies_14 = true;
        Object d_15;
        for(Iterator bb_16 = com_13.iterator(); bb_16.hasNext() && all_applies_14; all_applies_14 = m1_17.get(d_15).equals(m2_18.get(d_15)))
            d_15 = bb_16.next();

        if(!all_applies_14)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_17.putAll(m2_18);
        rhs_8 = m1_17;
        cpus = (HashMap)UTIL.clone(rhs_8);
        cpu_uorder = (Vector)UTIL.ConvertToList(UTIL.clone(insert(pid, cpu_uorder)));
        return cpu;
    }

    public Boolean pre_createCPU(Long pid, String pname, Boolean pvirt)
        throws CGException
    {
        Boolean varRes_4 = null;
        HashSet var2_6 = new HashSet();
        var2_6.clear();
        var2_6.addAll((Collection)cpus.keySet());
        varRes_4 = new Boolean(!var2_6.contains(pid));
        return varRes_4;
    }

    public tdCPU getCPU(Long pid)
        throws CGException
    {
        if(!pre_getCPU(pid).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getCPU");
        return (tdCPU)cpus.get(pid);
    }

    public Boolean pre_getCPU(Long pid)
        throws CGException
    {
        Boolean varRes_2 = null;
        varRes_2 = new Boolean(cpus.containsKey(pid));
        return varRes_2;
    }

    public HashSet getCPUs()
        throws CGException
    {
        HashSet rexpr_1 = new HashSet();
        rexpr_1.clear();
        rexpr_1.addAll((Collection)cpus.keySet());
        return rexpr_1;
    }

    public Vector getOrderedCpus()
        throws CGException
    {
        return cpu_uorder;
    }

    public Long getNoCpus()
        throws CGException
    {
        Long rexpr_1 = null;
        HashSet unArg_2 = new HashSet();
        unArg_2.clear();
        unArg_2.addAll((Collection)cpus.keySet());
        rexpr_1 = new Long(unArg_2.size());
        return rexpr_1;
    }

    public tdBUS createBUS(Long pid, String pname, Boolean pvirt)
        throws CGException
    {
        if(!pre_createBUS(pid, pname, pvirt).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in createBUS");
        tdBUS bus = new tdBUS(this, pid, pname, pvirt);
        HashMap rhs_8 = new HashMap();
        HashMap var2_10 = new HashMap();
        var2_10 = new HashMap();
        var2_10.put(pid, bus);
        HashMap m1_17 = (HashMap)buses.clone();
        HashMap m2_18 = var2_10;
        HashSet com_13 = new HashSet();
        com_13.addAll((Collection)m1_17.keySet());
        com_13.retainAll((Collection)m2_18.keySet());
        boolean all_applies_14 = true;
        Object d_15;
        for(Iterator bb_16 = com_13.iterator(); bb_16.hasNext() && all_applies_14; all_applies_14 = m1_17.get(d_15).equals(m2_18.get(d_15)))
            d_15 = bb_16.next();

        if(!all_applies_14)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_17.putAll(m2_18);
        rhs_8 = m1_17;
        buses = (HashMap)UTIL.clone(rhs_8);
        bus_uorder = (Vector)UTIL.ConvertToList(UTIL.clone(insert(pid, bus_uorder)));
        return bus;
    }

    public Boolean pre_createBUS(Long pid, String pname, Boolean pvirt)
        throws CGException
    {
        Boolean varRes_4 = null;
        HashSet var2_6 = new HashSet();
        var2_6.clear();
        var2_6.addAll((Collection)buses.keySet());
        varRes_4 = new Boolean(!var2_6.contains(pid));
        return varRes_4;
    }

    public tdBUS getBUS(Long pid)
        throws CGException
    {
        if(!pre_getBUS(pid).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getBUS");
        return (tdBUS)buses.get(pid);
    }

    public Boolean pre_getBUS(Long pid)
        throws CGException
    {
        Boolean varRes_2 = null;
        varRes_2 = new Boolean(buses.containsKey(pid));
        return varRes_2;
    }

    public HashSet getBUSes()
        throws CGException
    {
        HashSet rexpr_1 = new HashSet();
        rexpr_1.clear();
        rexpr_1.addAll((Collection)buses.keySet());
        return rexpr_1;
    }

    public Vector getOrderedBuses()
        throws CGException
    {
        return bus_uorder;
    }

    public Long getNoBuses()
        throws CGException
    {
        Long rexpr_1 = null;
        HashSet unArg_2 = new HashSet();
        unArg_2.clear();
        unArg_2.addAll((Collection)buses.keySet());
        rexpr_1 = new Long(unArg_2.size());
        return rexpr_1;
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

    @Override
	public void reset()
        throws CGException
    {
        HashSet iset_1 = new HashSet();
        iset_1.clear();
        iset_1.addAll((Collection)cpus.keySet());
        Long cpuid = null;
        tdCPU obj_8;
        for(Iterator enm_11 = iset_1.iterator(); enm_11.hasNext(); obj_8.reset())
        {
            Long elem_2 = UTIL.NumberToLong(enm_11.next());
            cpuid = elem_2;
            obj_8 = null;
            obj_8 = (tdCPU)cpus.get(cpuid);
        }

        HashSet iset_12 = new HashSet();
        iset_12.clear();
        iset_12.addAll((Collection)buses.keySet());
        Long busid = null;
        tdBUS obj_19;
        for(Iterator enm_22 = iset_12.iterator(); enm_22.hasNext(); obj_19.reset())
        {
            Long elem_13 = UTIL.NumberToLong(enm_22.next());
            busid = elem_13;
            obj_19 = null;
            obj_19 = (tdBUS)buses.get(busid);
        }

        HashSet iset_23 = new HashSet();
        iset_23.clear();
        iset_23.addAll((Collection)objects.keySet());
        Long objid = null;
        tdObject obj_30;
        for(Iterator enm_33 = iset_23.iterator(); enm_33.hasNext(); obj_30.reset())
        {
            Long elem_24 = UTIL.NumberToLong(enm_33.next());
            objid = elem_24;
            obj_30 = null;
            obj_30 = (tdObject)objects.get(objid);
        }

    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private HashMap cpus;
    private Vector cpu_uorder;
    private HashMap buses;
    private Vector bus_uorder;
    private HashMap threads;
    private HashMap messages;
    private HashMap objects;

}