// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:54
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IOmlDocument.java

package org.overturetool.traceviewer.ast.itf;

import java.util.Vector;
import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.ast.itf:
//            IOmlTraceFile, IOmlVisitor

public interface IOmlDocument
{

    public abstract String getFilename()
        throws CGException;

    public abstract Boolean hasTraceFile()
        throws CGException;

    public abstract IOmlTraceFile getTraceFile()
        throws CGException;

    public abstract Vector getLexems()
        throws CGException;

    public abstract String toVdmSlValue()
        throws CGException;

    public abstract String toVdmPpValue()
        throws CGException;

    public abstract void accept(IOmlVisitor iomlvisitor)
        throws CGException;
}