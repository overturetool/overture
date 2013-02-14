// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:54
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IOmlInstVarChange.java

package org.overturetool.traceviewer.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.ast.itf:
//            IOmlTraceEvent

public interface IOmlInstVarChange
    extends IOmlTraceEvent
{

    public abstract String getInstnm()
        throws CGException;

    public abstract String getVal()
        throws CGException;

    public abstract Long getObjref()
        throws CGException;

    public abstract Long getObstime()
        throws CGException;
}