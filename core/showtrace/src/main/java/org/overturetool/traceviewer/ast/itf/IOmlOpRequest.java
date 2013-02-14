// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:54
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IOmlOpRequest.java

package org.overturetool.traceviewer.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.ast.itf:
//            IOmlTraceEvent

public interface IOmlOpRequest
    extends IOmlTraceEvent
{

    public abstract Long getId()
        throws CGException;

    public abstract String getOpname()
        throws CGException;

    public abstract Long getObjref()
        throws CGException;

    public abstract Boolean hasObjref()
        throws CGException;

    public abstract String getClnm()
        throws CGException;

    public abstract Long getCpunm()
        throws CGException;

    public abstract Boolean getAsynchronous()
        throws CGException;

    public abstract String getArgs()
        throws CGException;

    public abstract Boolean hasArgs()
        throws CGException;

    public abstract Long getObstime()
        throws CGException;
}