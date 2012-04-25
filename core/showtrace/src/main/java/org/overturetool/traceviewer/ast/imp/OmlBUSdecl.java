// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlBUSdecl.java

package org.overturetool.traceviewer.ast.imp;

import java.util.HashMap;
import java.util.HashSet;
import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;
import org.overturetool.traceviewer.ast.itf.*;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlTraceEvent

public class OmlBUSdecl extends OmlTraceEvent
    implements IOmlBUSdecl
{

    public OmlBUSdecl()
        throws CGException
    {
        ivId = null;
        ivName = null;
        ivTopo = new HashSet();
        try
        {
            ivId = null;
            ivName = UTIL.ConvertToString(new String());
            ivTopo = new HashSet();
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
        return new String("BUSdecl");
    }

    @Override
	public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitBUSdecl(this);
    }

    public OmlBUSdecl(Long p1, String p2, HashSet p3)
        throws CGException
    {
        this();
        setId(p1);
        setName(p2);
        setTopo(p3);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("id");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setId(UTIL.NumberToLong(data.get(fname)));
        fname = new String("name");
        Boolean cond_13 = null;
        cond_13 = new Boolean(data.containsKey(fname));
        if(cond_13.booleanValue())
            setName(UTIL.ConvertToString(data.get(fname)));
        fname = new String("topo");
        Boolean cond_22 = null;
        cond_22 = new Boolean(data.containsKey(fname));
        if(cond_22.booleanValue())
            setTopo((HashSet)data.get(fname));
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

    public HashSet getTopo()
        throws CGException
    {
        return ivTopo;
    }

    public void setTopo(HashSet parg)
        throws CGException
    {
        ivTopo = (HashSet)UTIL.clone(parg);
    }

    public void addTopo(Long parg)
        throws CGException
    {
        ivTopo.add(parg);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long ivId;
    private String ivName;
    private HashSet ivTopo;

}