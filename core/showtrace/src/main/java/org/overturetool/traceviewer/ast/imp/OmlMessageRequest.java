// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlMessageRequest.java

package org.overturetool.traceviewer.ast.imp;

import java.io.PrintStream;
import java.util.HashMap;

import org.overturetool.traceviewer.ast.itf.*;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlMessageRequest extends OmlTraceEvent
    implements IOmlMessageRequest
{

    public OmlMessageRequest()
        throws CGException
    {
        ivBusid = null;
        ivFromcpu = null;
        ivTocpu = null;
        ivMsgid = null;
        ivCallthr = null;
        ivOpname = null;
        ivObjref = null;
        ivSize = null;
        ivObstime = null;
        try
        {
            ivBusid = null;
            ivFromcpu = null;
            ivTocpu = null;
            ivMsgid = null;
            ivCallthr = null;
            ivOpname = UTIL.ConvertToString(new String());
            ivObjref = null;
            ivSize = null;
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
        return new String("MessageRequest");
    }

    public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitMessageRequest(this);
    }

    public OmlMessageRequest(Long p1, Long p2, Long p3, Long p4, Long p5, String p6, Long p7, 
            Long p8, Long p9)
        throws CGException
    {
        this();
        setBusid(p1);
        setFromcpu(p2);
        setTocpu(p3);
        setMsgid(p4);
        setCallthr(p5);
        setOpname(p6);
        setObjref(p7);
        setSize(p8);
        setObstime(p9);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("busid");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setBusid(UTIL.NumberToLong(data.get(fname)));
        fname = new String("fromcpu");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setFromcpu(UTIL.NumberToLong(data.get(fname)));
        fname = new String("tocpu");
        Boolean cond_22 = null;
        cond_22 = new Boolean(data.containsKey(fname));
        if(cond_22.booleanValue())
            setTocpu(UTIL.NumberToLong(data.get(fname)));
        fname = new String("msgid");
        Boolean cond_31 = null;
        cond_31 = new Boolean(data.containsKey(fname));
        if(cond_31.booleanValue())
            setMsgid(UTIL.NumberToLong(data.get(fname)));
        fname = new String("callthr");
        Boolean cond_40 = null;
        cond_40 = new Boolean(data.containsKey(fname));
        if(cond_40.booleanValue())
            setCallthr(UTIL.NumberToLong(data.get(fname)));
        fname = new String("opname");
        Boolean cond_49 = null;
        cond_49 = new Boolean(data.containsKey(fname));
        if(cond_49.booleanValue())
            setOpname(UTIL.ConvertToString(data.get(fname)));
        fname = new String("objref");
        Boolean cond_58 = null;
        cond_58 = new Boolean(data.containsKey(fname));
        if(cond_58.booleanValue())
            setObjref(UTIL.NumberToLong(data.get(fname)));
        fname = new String("size");
        Boolean cond_67 = null;
        cond_67 = new Boolean(data.containsKey(fname));
        if(cond_67.booleanValue())
            setSize(UTIL.NumberToLong(data.get(fname)));
        fname = new String("time");
        Boolean cond_76 = null;
        cond_76 = new Boolean(data.containsKey(fname));
        if(cond_76.booleanValue())
            setObstime(UTIL.NumberToLong(data.get(fname)));
    }

    public Long getBusid()
        throws CGException
    {
        return ivBusid;
    }

    public void setBusid(Long parg)
        throws CGException
    {
        ivBusid = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public Long getFromcpu()
        throws CGException
    {
        return ivFromcpu;
    }

    public void setFromcpu(Long parg)
        throws CGException
    {
        ivFromcpu = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public Long getTocpu()
        throws CGException
    {
        return ivTocpu;
    }

    public void setTocpu(Long parg)
        throws CGException
    {
        ivTocpu = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public Long getMsgid()
        throws CGException
    {
        return ivMsgid;
    }

    public void setMsgid(Long parg)
        throws CGException
    {
        ivMsgid = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public Long getCallthr()
        throws CGException
    {
        return ivCallthr;
    }

    public void setCallthr(Long parg)
        throws CGException
    {
        ivCallthr = UTIL.NumberToLong(UTIL.clone(parg));
    }

    public String getOpname()
        throws CGException
    {
        return ivOpname;
    }

    public void setOpname(String parg)
        throws CGException
    {
        ivOpname = UTIL.ConvertToString(UTIL.clone(parg));
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

    public Long getSize()
        throws CGException
    {
        return ivSize;
    }

    public void setSize(Long parg)
        throws CGException
    {
        ivSize = UTIL.NumberToLong(UTIL.clone(parg));
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
    private Long ivBusid;
    private Long ivFromcpu;
    private Long ivTocpu;
    private Long ivMsgid;
    private Long ivCallthr;
    private String ivOpname;
    private Long ivObjref;
    private Long ivSize;
    private Long ivObstime;

}