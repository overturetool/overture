// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlInstVarChange.java

package org.overturetool.traceviewer.ast.imp;

import java.io.PrintStream;
import java.util.HashMap;

import org.overturetool.traceviewer.ast.itf.*;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlInstVarChange extends OmlTraceEvent
    implements IOmlInstVarChange
{

    public OmlInstVarChange()
        throws CGException
    {
        ivInstnm = null;
        ivVal = null;
        ivObjref = null;
        ivObstime = null;
        try
        {
            ivInstnm = UTIL.ConvertToString(new String());
            ivVal = UTIL.ConvertToString(new String());
            ivObjref = null;
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
        return new String("InstVarChange");
    }

    public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitInstVarChange(this);
    }

    public OmlInstVarChange(String p1, String p2, Long p3, Long p4)
        throws CGException
    {
        this();
        setInstnm(p1);
        setVal(p2);
        setObjref(p3);
        setObstime(p4);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("instnm");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setInstnm(UTIL.ConvertToString(data.get(fname)));
        fname = new String("val");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setVal(UTIL.ConvertToString(data.get(fname)));
        fname = new String("objref");
        Boolean cond_22 = null;
        cond_22 = new Boolean(data.containsKey(fname));
        if(cond_22.booleanValue())
            setObjref(UTIL.NumberToLong(data.get(fname)));
        fname = new String("time");
        Boolean cond_31 = null;
        cond_31 = new Boolean(data.containsKey(fname));
        if(cond_31.booleanValue())
            setObstime(UTIL.NumberToLong(data.get(fname)));
    }

    public String getInstnm()
        throws CGException
    {
        return ivInstnm;
    }

    public void setInstnm(String parg)
        throws CGException
    {
        ivInstnm = UTIL.ConvertToString(UTIL.clone(parg));
    }

    public String getVal()
        throws CGException
    {
        return ivVal;
    }

    public void setVal(String parg)
        throws CGException
    {
        ivVal = UTIL.ConvertToString(UTIL.clone(parg));
    }

    public Long getObjref()
        throws CGException
    {
        return ivObjref;
    }

    public void setObjref(Long parg)
        throws CGException
    {
        ivObjref = UTIL.NumberToLong(UTIL.clone(parg));
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
    private String ivInstnm;
    private String ivVal;
    private Long ivObjref;
    private Long ivObstime;

}