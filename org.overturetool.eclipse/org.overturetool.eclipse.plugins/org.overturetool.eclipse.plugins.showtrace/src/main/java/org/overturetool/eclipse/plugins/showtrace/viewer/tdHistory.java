// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   tdHistory.java

package org.overturetool.eclipse.plugins.showtrace.viewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlTraceEvent;
@SuppressWarnings("unchecked")
public abstract class tdHistory
{

    public tdHistory()
        throws CGException
    {
        hist_uevents = new HashMap();
        hist_utimes = null;
        try
        {
            hist_uevents = new HashMap();
            hist_utimes = new Vector();
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    protected Vector insert(Long pval, Vector pseq)
        throws CGException
    {
        Vector res = new Vector();
        Boolean found = new Boolean(false);
        if((new Boolean(UTIL.equals(pseq, new Vector()))).booleanValue())
        {
            Vector rhs_37 = null;
            rhs_37 = new Vector();
            rhs_37.add(pval);
            res = (Vector)UTIL.ConvertToList(UTIL.clone(rhs_37));
        } else
        {
            Long pi = null;
            for(Iterator enm_32 = pseq.iterator(); enm_32.hasNext();)
            {
                Long elem_7 = UTIL.NumberToLong(enm_32.next());
                pi = elem_7;
                if((new Boolean(pi.intValue() < pval.intValue())).booleanValue())
                    res.add(pi);
                else
                if((new Boolean(pi.intValue() == pval.intValue())).booleanValue())
                {
                    res.add(pi);
                    found = (Boolean)UTIL.clone(new Boolean(true));
                } else
                if((new Boolean(!found.booleanValue())).booleanValue())
                {
                    Vector rhs_20 = null;
                    Vector var2_22 = null;
                    var2_22 = new Vector();
                    var2_22.add(pval);
                    var2_22.add(pi);
                    rhs_20 = (Vector)res.clone();
                    rhs_20.addAll(var2_22);
                    res = (Vector)UTIL.ConvertToList(UTIL.clone(rhs_20));
                    found = (Boolean)UTIL.clone(new Boolean(true));
                } else
                {
                    res.add(pi);
                }
            }

            if((new Boolean(!found.booleanValue())).booleanValue())
                res.add(pval);
        }
        return res;
    }

    public void addHistory(IOmlTraceEvent pite, Long ptime)
        throws CGException
    {
        Boolean cond_3 = null;
        cond_3 = new Boolean(hist_uevents.containsKey(ptime));
        if(cond_3.booleanValue())
        {
            Vector mr_20 = null;
            Vector var2_24 = null;
            var2_24 = new Vector();
            var2_24.add(pite);
            mr_20 = (Vector)((Vector)UTIL.ConvertToList(hist_uevents.get(ptime))).clone();
            mr_20.addAll(var2_24);
            hist_uevents.put(ptime, mr_20);
        } else
        {
            HashMap rhs_6 = new HashMap();
            HashMap var2_8 = new HashMap();
            Vector tmpVar2_10 = null;
            tmpVar2_10 = new Vector();
            tmpVar2_10.add(pite);
            var2_8 = new HashMap();
            var2_8.put(ptime, tmpVar2_10);
            HashMap m1_16 = (HashMap)hist_uevents.clone();
            HashMap m2_17 = var2_8;
            HashSet com_12 = new HashSet();
            com_12.addAll(m1_16.keySet());
            com_12.retainAll(m2_17.keySet());
            boolean all_applies_13 = true;
            Object d_14;
            for(Iterator bb_15 = com_12.iterator(); bb_15.hasNext() && all_applies_13; all_applies_13 = m1_16.get(d_14).equals(m2_17.get(d_14)))
                d_14 = bb_15.next();

            if(!all_applies_13)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_16.putAll(m2_17);
            rhs_6 = m1_16;
            hist_uevents = (HashMap)UTIL.clone(rhs_6);
        }
        hist_utimes = (Vector)UTIL.ConvertToList(UTIL.clone(insert(ptime, hist_utimes)));
    }

    public Vector getHistory(Long ptime)
        throws CGException
    {
        if(!pre_getHistory(ptime).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getHistory");
        return (Vector)UTIL.ConvertToList(hist_uevents.get(ptime));
    }

    public Boolean pre_getHistory(Long ptime)
        throws CGException
    {
        Boolean varRes_2 = null;
        varRes_2 = new Boolean(hist_uevents.containsKey(ptime));
        return varRes_2;
    }

    public Vector getTimes()
        throws CGException
    {
        return hist_utimes;
    }

    public abstract void reset()
        throws CGException;

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private HashMap hist_uevents;
    private Vector hist_utimes;

}