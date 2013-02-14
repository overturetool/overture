// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:23
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlTraceEvent.java

package org.overturetool.traceviewer.ast.imp;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.traceviewer.ast.itf.IOmlTraceEvent;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlNode

public class OmlTraceEvent extends OmlNode
    implements IOmlTraceEvent
{

    public OmlTraceEvent()
        throws CGException
    {
    }

    @Override
	public String identity()
        throws CGException
    {
        return new String("TraceEvent");
    }

    @Override
	public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitTraceEvent(this);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();

}