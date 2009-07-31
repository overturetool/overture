// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:23
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlThreadSwapOut.java

package org.overturetool.traceviewer.ast.imp;

import java.util.HashMap;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlThreadSwapOut;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlThreadSwapOut extends OmlTraceEvent
    implements IOmlThreadSwapOut
{

    public OmlThreadSwapOut()
        throws CGException
    {
        ivId = null;
        ivObjref = null;
        ivClnm = null;
        ivCpunm = null;
        ivOverhead = null;
        ivObstime = null;
        try
        {
            ivId = null;
            ivObjref = null;
            ivClnm = null;
            ivCpunm = null;
            ivOverhead = null;
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
        return new String("ThreadSwapOut");
    }

    @Override
	public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitThreadSwapOut(this);
    }

    public OmlThreadSwapOut(Long p1, Long p2, String p3, Long p4, Long p5, Long p6)
        throws CGException
    {
        this();
        setId(p1);
        setObjref(p2);
        setClnm(p3);
        setCpunm(p4);
        setOverhead(p5);
        setObstime(p6);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("id");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setId(UTIL.NumberToLong(data.get(fname)));
        fname = new String("objref");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setObjref(UTIL.NumberToLong(data.get(fname)));
        fname = new String("clnm");
        Boolean cond_22 = null;
        cond_22 = new Boolean(data.containsKey(fname));
        if(cond_22.booleanValue())
            setClnm(UTIL.ConvertToString(data.get(fname)));
        fname = new String("cpunm");
        Boolean cond_31 = null;
        cond_31 = new Boolean(data.containsKey(fname));
        if(cond_31.booleanValue())
            setCpunm(UTIL.NumberToLong(data.get(fname)));
        fname = new String("overhead");
        Boolean cond_40 = null;
        cond_40 = new Boolean(data.containsKey(fname));
        if(cond_40.booleanValue())
            setOverhead(UTIL.NumberToLong(data.get(fname)));
        fname = new String("time");
        Boolean cond_49 = null;
        cond_49 = new Boolean(data.containsKey(fname));
        if(cond_49.booleanValue())
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

    public Long getObjref()
        throws CGException
    {
        if(!pre_getObjref().booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getObjref");
        return ivObjref;
    }

    public Boolean pre_getObjref()
        throws CGException
    {
        return hasObjref();
    }

    public Boolean hasObjref()
        throws CGException
    {
        return new Boolean(!UTIL.equals(ivObjref, null));
    }

    public void setObjref(Long parg)
        throws CGException
    {
        ivObjref = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public String getClnm()
        throws CGException
    {
        if(!pre_getClnm().booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getClnm");
        return ivClnm;
    }

    public Boolean pre_getClnm()
        throws CGException
    {
        return hasClnm();
    }

    public Boolean hasClnm()
        throws CGException
    {
        return new Boolean(!UTIL.equals(ivClnm, null));
    }

    public void setClnm(String parg)
        throws CGException
    {
        ivClnm = UTIL.ConvertToString(UTIL.clone(parg));
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

    public Long getOverhead()
        throws CGException
    {
        return ivOverhead;
    }

    public void setOverhead(Long parg)
        throws CGException
    {
        ivOverhead = UTIL.NumberToLong(UTIL.clone(parg));
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
    private Long ivObjref;
    private String ivClnm;
    private Long ivCpunm;
    private Long ivOverhead;
    private Long ivObstime;

}