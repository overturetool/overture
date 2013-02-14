// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:23
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlTraceFile.java

package org.overturetool.traceviewer.ast.imp;

import java.util.HashMap;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlNode;
import org.overturetool.traceviewer.ast.itf.IOmlTraceFile;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlNode

public class OmlTraceFile extends OmlNode
    implements IOmlTraceFile
{

    public OmlTraceFile()
        throws CGException
    {
        ivTrace = null;
        try
        {
            ivTrace = new Vector();
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
        return new String("TraceFile");
    }

    @Override
	public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitTraceFile(this);
    }

    public OmlTraceFile(Vector p1)
        throws CGException
    {
        this();
        setTrace(p1);
    }

    public void init(HashMap data)
        throws CGException
    {
        String fname = new String("Trace");
        Boolean cond_4 = null;
        cond_4 = new Boolean(data.containsKey(fname));
        if(cond_4.booleanValue())
            setTrace((Vector)data.get(fname));
    }

    public Vector getTrace()
        throws CGException
    {
        return ivTrace;
    }

    public void setTrace(Vector parg)
        throws CGException
    {
        ivTrace = (Vector)UTIL.ConvertToList(UTIL.clone(parg));
    }

    public void addTrace(IOmlNode parg)
        throws CGException
    {
        ivTrace.add(parg);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Vector ivTrace;

}