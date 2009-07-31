// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:23
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlThreadKill.java

package org.overturetool.traceviewer.ast.imp;

import java.util.HashMap;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlThreadKill;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlThreadKill extends OmlTraceEvent
    implements IOmlThreadKill
{

    public OmlThreadKill()
        throws CGException
    {
        ivId = null;
        ivCpunm = null;
        ivObstime = null;
        try
        {
            ivId = null;
            ivCpunm = null;
            ivObstime = null;
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    @Override
	public String identity()
        throws CGException
    {
        return new String("ThreadKill");
    }

    @Override
	public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitThreadKill(this);
    }

    public OmlThreadKill(Long p1, Long p2, Long p3)
        throws CGException
    {
        this();
        setId(p1);
        setCpunm(p2);
        setObstime(p3);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("id");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setId(UTIL.NumberToLong(data.get(fname)));
        fname = new String("cpunm");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setCpunm(UTIL.NumberToLong(data.get(fname)));
        fname = new String("time");
        Boolean cond_22 = null;
        cond_22 = new Boolean(data.containsKey(fname));
        if(cond_22.booleanValue())
            setObstime(UTIL.NumberToLong(data.get(fname)));
    }

    public Long getId()
        throws CGException
    {
        return ivId;
    }

    public void setId(Long parg)
        throws CGException
    {
        ivId = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public Long getCpunm()
        throws CGException
    {
        return ivCpunm;
    }

    public void setCpunm(Long parg)
        throws CGException
    {
        ivCpunm = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public Long getObstime()
        throws CGException
    {
        return ivObstime;
    }

    public void setObstime(Long parg)
        throws CGException
    {
        ivObstime = UTIL.NumberToLong(UTIL.clone(parg));
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long ivId;
    private Long ivCpunm;
    private Long ivObstime;

}