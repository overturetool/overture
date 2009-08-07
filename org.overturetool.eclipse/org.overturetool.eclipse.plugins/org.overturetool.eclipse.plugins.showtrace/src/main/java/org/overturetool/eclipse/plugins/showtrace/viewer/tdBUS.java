// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   tdBUS.java

package org.overturetool.eclipse.plugins.showtrace.viewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdResource, TraceData, tdMessage
@SuppressWarnings("unchecked")
public class tdBUS extends tdResource
{

    public tdBUS()
        throws CGException
    {
        id = null;
        name = null;
        expl = null;
        cpu_uconnect = new HashSet();
        messages = new HashMap();
        try
        {
            cpu_uconnect = new HashSet();
            messages = new HashMap();
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public tdBUS(TraceData pdata, Long pid, String pname, Boolean pexpl)
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
        return cpu_uconnect;
    }

    public void connect(Long pbid)
        throws CGException
    {
        cpu_uconnect.add(pbid);
    }

    public tdMessage createMessage(Long pmsgid, Long pfrom, Long pfromthr, Long pto, Long ptothr, Long ptoobj, String pdescr)
        throws CGException
    {
        if(!pre_createMessage(pmsgid, pfrom, pfromthr, pto, ptothr, ptoobj, pdescr).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in createMessage");
        tdMessage msg = new tdMessage(this, pmsgid, pfrom, pfromthr, pto, ptothr, ptoobj, pdescr);
        HashMap rhs_16 = new HashMap();
        HashMap var2_18 = new HashMap();
        var2_18 = new HashMap();
        var2_18.put(pmsgid, msg);
        HashMap m1_25 = (HashMap)messages.clone();
        HashMap m2_26 = var2_18;
        HashSet com_21 = new HashSet();
        com_21.addAll(m1_25.keySet());
        com_21.retainAll(m2_26.keySet());
        boolean all_applies_22 = true;
        Object d_23;
        for(Iterator bb_24 = com_21.iterator(); bb_24.hasNext() && all_applies_22; all_applies_22 = m1_25.get(d_23).equals(m2_26.get(d_23)))
            d_23 = bb_24.next();

        if(!all_applies_22)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_25.putAll(m2_26);
        rhs_16 = m1_25;
        messages = (HashMap)UTIL.clone(rhs_16);
        data.addMessage(msg);
        return msg;
    }

    public Boolean pre_createMessage(Long pmsgid, Long pfrom, Long pfromthr, Long pto, Long ptothr, Long ptoobj, String pdescr)
        throws CGException
    {
        Boolean varRes_8 = null;
        HashSet var2_10 = new HashSet();
        var2_10.clear();
        var2_10.addAll(messages.keySet());
        varRes_8 = new Boolean(!var2_10.contains(pmsgid));
        return varRes_8;
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

    @Override
	public void reset()
        throws CGException
    {
        super.reset();
        HashSet iset_2 = new HashSet();
        iset_2.clear();
        iset_2.addAll(messages.keySet());
        Long msgid = null;
        tdMessage obj_9;
        for(Iterator enm_12 = iset_2.iterator(); enm_12.hasNext(); obj_9.reset())
        {
            Long elem_3 = UTIL.NumberToLong(enm_12.next());
            msgid = elem_3;
            obj_9 = null;
            obj_9 = (tdMessage)messages.get(msgid);
        }

    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long id;
    private String name;
    private Boolean expl;
    private HashSet cpu_uconnect;
    private HashMap messages;

}