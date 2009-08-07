// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   tdObject.java

package org.overturetool.eclipse.plugins.showtrace.viewer;

import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.Tuple;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdResource, TraceData
@SuppressWarnings("unchecked")
public class tdObject extends tdResource
{

    public tdObject()
        throws CGException
    {
        id = null;
        name = null;
        where = null;
        try
        {
            where = new Vector();
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public tdObject(TraceData pdata, Long pobjid, String pname)
        throws CGException
    {
        this();
        data = (TraceData)UTIL.clone(pdata);
        id = UTIL.NumberToLong(UTIL.clone(pobjid));
        name = UTIL.ConvertToString(UTIL.clone(pname));
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

    public void deploy(Long pcpu, Long ptime)
        throws CGException
    {
        Vector rhs_3 = null;
        Vector var1_4 = null;
        Tuple e_seq_5 = new Tuple(2);
        e_seq_5 = new Tuple(2);
        e_seq_5.SetField(1, pcpu);
        e_seq_5.SetField(2, ptime);
        var1_4 = new Vector();
        var1_4.add(e_seq_5);
        rhs_3 = (Vector)var1_4.clone();
        rhs_3.addAll(where);
        where = (Vector)UTIL.ConvertToList(UTIL.clone(rhs_3));
    }

    public Long deployedAt(Long ptime)
        throws CGException
    {
        Tuple dinfo = new Tuple(2);
        for(Iterator enm_15 = where.iterator(); enm_15.hasNext();)
        {
            Tuple elem_3 = (Tuple)enm_15.next();
            dinfo = elem_3;
            Tuple tmpVal_7 = new Tuple(2);
            tmpVal_7 = dinfo;
            Long cpunm = null;
            Long etime = null;
            boolean succ_6 = true;
            Vector e_l_8 = new Vector();
            for(int i_9 = 1; i_9 <= tmpVal_7.Length(); i_9++)
                e_l_8.add(tmpVal_7.GetField(i_9));

            if(succ_6 = 2 == e_l_8.size())
            {
                cpunm = UTIL.NumberToLong(e_l_8.get(0));
                etime = UTIL.NumberToLong(e_l_8.get(1));
            }
            if(!succ_6)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            if((new Boolean(ptime.intValue() >= etime.intValue())).booleanValue())
                return cpunm;
        }

        UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
        return new Long(0L);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long id;
    private String name;
    private Vector where;

}