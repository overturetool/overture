// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlCPUdecl.java

package org.overturetool.traceviewer.ast.imp;

import java.util.HashMap;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlCPUdecl;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlCPUdecl extends OmlTraceEvent
    implements IOmlCPUdecl
{

    public OmlCPUdecl()
        throws CGException
    {
        ivId = null;
        ivExpl = null;
        ivSys = null;
        ivName = null;
        try
        {
            ivId = null;
            ivExpl = null;
            ivSys = UTIL.ConvertToString(new String());
            ivName = UTIL.ConvertToString(new String());
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
        return new String("CPUdecl");
    }

    @Override
	public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitCPUdecl(this);
    }

    public OmlCPUdecl(Long p1, Boolean p2, String p3, String p4)
        throws CGException
    {
        this();
        setId(p1);
        setExpl(p2);
        setSys(p3);
        setName(p4);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("id");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setId(UTIL.NumberToLong(data.get(fname)));
        fname = new String("expl");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setExpl((Boolean)data.get(fname));
        fname = new String("sys");
        Boolean cond_22 = null;
        cond_22 = new Boolean(data.containsKey(fname));
        if(cond_22.booleanValue())
            setSys(UTIL.ConvertToString(data.get(fname)));
        fname = new String("name");
        Boolean cond_31 = null;
        cond_31 = new Boolean(data.containsKey(fname));
        if(cond_31.booleanValue())
            setName(UTIL.ConvertToString(data.get(fname)));
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

    public Boolean getExpl()
        throws CGException
    {
        return ivExpl;
    }

    public void setExpl(Boolean parg)
        throws CGException
    {
        ivExpl = (Boolean)UTIL.clone(parg);
    }

    public String getSys()
        throws CGException
    {
        return ivSys;
    }

    public void setSys(String parg)
        throws CGException
    {
        ivSys = UTIL.ConvertToString(UTIL.clone(parg));
    }

    public String getName()
        throws CGException
    {
        return ivName;
    }

    public void setName(String parg)
        throws CGException
    {
        ivName = UTIL.ConvertToString(UTIL.clone(parg));
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long ivId;
    private Boolean ivExpl;
    private String ivSys;
    private String ivName;

}