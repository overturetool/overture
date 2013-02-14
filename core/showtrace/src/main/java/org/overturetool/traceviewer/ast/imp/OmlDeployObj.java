// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlDeployObj.java

package org.overturetool.traceviewer.ast.imp;

import java.util.HashMap;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlDeployObj;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlDeployObj extends OmlTraceEvent
    implements IOmlDeployObj
{

    public OmlDeployObj()
        throws CGException
    {
        ivObjref = null;
        ivCpunm = null;
        ivClnm = null;
        ivObstime = null;
        try
        {
            ivObjref = null;
            ivCpunm = null;
            ivClnm = UTIL.ConvertToString(new String());
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
        return new String("DeployObj");
    }

    @Override
	public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitDeployObj(this);
    }

    public OmlDeployObj(Long p1, Long p2, String p3, Long p4)
        throws CGException
    {
        this();
        setObjref(p1);
        setCpunm(p2);
        setClnm(p3);
        setObstime(p4);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("objref");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setObjref(UTIL.NumberToLong(data.get(fname)));
        fname = new String("cpunm");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setCpunm(UTIL.NumberToLong(data.get(fname)));
        fname = new String("clnm");
        Boolean cond_22 = null;
        cond_22 = new Boolean(data.containsKey(fname));
        if(cond_22.booleanValue())
            setClnm(UTIL.ConvertToString(data.get(fname)));
        fname = new String("time");
        Boolean cond_31 = null;
        cond_31 = new Boolean(data.containsKey(fname));
        if(cond_31.booleanValue())
            setObstime(UTIL.NumberToLong(data.get(fname)));
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

    public String getClnm()
        throws CGException
    {
        return ivClnm;
    }

    public void setClnm(String parg)
        throws CGException
    {
        ivClnm = UTIL.ConvertToString(UTIL.clone(parg));
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
    private Long ivObjref;
    private Long ivCpunm;
    private String ivClnm;
    private Long ivObstime;

}