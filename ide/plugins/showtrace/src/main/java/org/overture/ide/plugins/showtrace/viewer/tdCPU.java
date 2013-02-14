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
// Source File Name:   tdCPU.java

package org.overture.ide.plugins.showtrace.viewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdResource, TraceData, tdThread, tdObject
@SuppressWarnings({"unchecked","rawtypes"})
public class tdCPU extends tdResource
{

    public tdCPU()
        throws CGException
    {
        id = null;
        name = null;
        expl = null;
        bus_uconnect = new HashSet();
        threads = new HashMap();
        curthr = null;
        objects = new HashMap();
        try
        {
            bus_uconnect = new HashSet();
            threads = new HashMap();
            curthr = null;
            objects = new HashMap();
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public tdCPU(TraceData pdata, Long pid, String pname, Boolean pexpl)
        throws CGException
    {
        this();
        data = (TraceData)UTIL.clone(pdata);
        id = UTIL.NumberToLong(UTIL.clone(pid));
        name = UTIL.ConvertToString(UTIL.clone(pname));
        expl = (Boolean)UTIL.clone(pexpl);
    }

    public Long getId()
        throws CGException
    {
        return id;
    }

    public String getName()
        throws CGException
    {
        return name;
    }

    public Boolean isExplicit()
        throws CGException
    {
        return expl;
    }

    public HashSet connects()
        throws CGException
    {
        return bus_uconnect;
    }

    public void connect(Long pbid)
        throws CGException
    {
        bus_uconnect.add(pbid);
    }

    public tdThread createThread(Long pthrid)
        throws CGException
    {
        if(!pre_createThread(pthrid).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in createThread");
        tdThread thr = new tdThread(this, pthrid);
        HashMap rhs_4 = new HashMap();
        HashMap var2_6 = new HashMap();
        var2_6 = new HashMap();
        var2_6.put(pthrid, thr);
        HashMap m1_13 = (HashMap)threads.clone();
        HashMap m2_14 = var2_6;
        HashSet com_9 = new HashSet();
        com_9.addAll(m1_13.keySet());
        com_9.retainAll(m2_14.keySet());
        boolean all_applies_10 = true;
        Object d_11;
        for(Iterator bb_12 = com_9.iterator(); bb_12.hasNext() && all_applies_10; all_applies_10 = m1_13.get(d_11).equals(m2_14.get(d_11)))
            d_11 = bb_12.next();

        if(!all_applies_10)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_13.putAll(m2_14);
        rhs_4 = m1_13;
        threads = (HashMap)UTIL.clone(rhs_4);
        data.addThread(thr);
        return thr;
    }

    public Boolean pre_createThread(Long pthrid)
        throws CGException
    {
        Boolean varRes_2 = null;
        HashSet var2_4 = new HashSet();
        var2_4.clear();
        var2_4.addAll(threads.keySet());
        varRes_2 = new Boolean(!var2_4.contains(pthrid));
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

    public void setCurrentThread(Long pthr)
        throws CGException
    {
        if((new Boolean(UTIL.equals(pthr, null))).booleanValue())
        {
            curthr = null;
            setIdle(new Boolean(true));
        } else
        {
            Boolean cond_5 = null;
            HashSet var2_7 = new HashSet();
            var2_7.clear();
            var2_7.addAll(threads.keySet());
            cond_5 = new Boolean(!var2_7.contains(pthr));
            if(cond_5.booleanValue())
            {
                UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
                return;
            }
            curthr = UTIL.NumberToLong(UTIL.clone(pthr));
            setIdle(new Boolean(false));
        }
    }

    public Boolean hasCurrentThread()
        throws CGException
    {
        return new Boolean(!UTIL.equals(curthr, null));
    }

    public tdThread getCurrentThread()
        throws CGException
    {
        if(!pre_getCurrentThread().booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getCurrentThread");
        return getThread(curthr);
    }

    public Boolean pre_getCurrentThread()
        throws CGException
    {
        return hasCurrentThread();
    }

    public void addObject(tdObject pobj)
        throws CGException
    {
        if(!pre_addObject(pobj).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in addObject");
        HashMap rhs_2 = new HashMap();
        HashMap var2_4 = new HashMap();
        Long tmpVar1_5 = null;
        tmpVar1_5 = pobj.getId();
        var2_4 = new HashMap();
        var2_4.put(tmpVar1_5, pobj);
        HashMap m1_11 = (HashMap)objects.clone();
        HashMap m2_12 = var2_4;
        HashSet com_7 = new HashSet();
        com_7.addAll(m1_11.keySet());
        com_7.retainAll(m2_12.keySet());
        boolean all_applies_8 = true;
        Object d_9;
        for(Iterator bb_10 = com_7.iterator(); bb_10.hasNext() && all_applies_8; all_applies_8 = m1_11.get(d_9).equals(m2_12.get(d_9)))
            d_9 = bb_10.next();

        if(!all_applies_8)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_11.putAll(m2_12);
        rhs_2 = m1_11;
        objects = (HashMap)UTIL.clone(rhs_2);
    }

    public Boolean pre_addObject(tdObject pobj)
        throws CGException
    {
        Boolean varRes_2 = null;
        Boolean unArg_3 = null;
        Long par_4 = null;
        par_4 = pobj.getId();
        unArg_3 = hasObject(par_4);
        varRes_2 = new Boolean(!unArg_3.booleanValue());
        return varRes_2;
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

    public HashSet getObjects()
        throws CGException
    {
        HashSet rexpr_1 = new HashSet();
        rexpr_1.clear();
        rexpr_1.addAll(objects.keySet());
        return rexpr_1;
    }

    public Boolean hasObjectAt(Long pobjid, Long ptime)
        throws CGException
    {
        Boolean rexpr_3 = null;
        Long var2_5 = null;
        tdObject obj_6 = null;
        TraceData obj_7 = null;
        obj_7 = getTraceData();
        obj_6 = obj_7.getObject(pobjid);
        var2_5 = obj_6.deployedAt(ptime);
        rexpr_3 = new Boolean(id.longValue() == var2_5.longValue());
        return rexpr_3;
    }

    @Override
	public void reset()
        throws CGException
    {
        super.reset();
        objects = (HashMap)UTIL.clone(new HashMap());
        curthr = null;
        HashSet iset_4 = new HashSet();
        iset_4.clear();
        iset_4.addAll(threads.keySet());
        Long thrid = null;
        tdThread obj_11;
        for(Iterator enm_14 = iset_4.iterator(); enm_14.hasNext(); obj_11.reset())
        {
            Long elem_5 = UTIL.NumberToLong(enm_14.next());
            thrid = elem_5;
            obj_11 = null;
            obj_11 = (tdThread)threads.get(thrid);
        }

    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long id;
    private String name;
    private Boolean expl;
    private HashSet bus_uconnect;
    private HashMap threads;
    private Long curthr;
    private HashMap objects;

}