// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlMessageCompleted.java

package org.overturetool.traceviewer.ast.imp;

import java.io.PrintStream;
import java.util.HashMap;

import org.overturetool.traceviewer.ast.itf.IOmlMessageCompleted;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlMessageCompleted extends OmlTraceEvent
    implements IOmlMessageCompleted
{

    public OmlMessageCompleted()
        throws CGException
    {
        ivMsgid = null;
        ivObstime = null;
        try
        {
            ivMsgid = null;
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
        return new String("MessageCompleted");
    }

    public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitMessageCompleted(this);
    }

    public OmlMessageCompleted(Long p1, Long p2)
        throws CGException
    {
        this();
        setMsgid(p1);
        setObstime(p2);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("msgid");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setMsgid(UTIL.NumberToLong(data.get(fname)));
        fname = new String("time");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setObstime(UTIL.NumberToLong(data.get(fname)));
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
    private Long ivMsgid;
    private Long ivObstime;

}