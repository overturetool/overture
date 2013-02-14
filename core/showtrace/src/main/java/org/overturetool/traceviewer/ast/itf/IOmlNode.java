// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:54
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IOmlNode.java

package org.overturetool.traceviewer.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.ast.itf:
//            IOmlVisitor

public interface IOmlNode
{

    public abstract String identity()
        throws CGException;

    public abstract void accept(IOmlVisitor iomlvisitor)
        throws CGException;

    public abstract Boolean hasPositionInfo()
        throws CGException;

    public abstract Long getLeftMostLexemIndex()
        throws CGException;

    public abstract Long getRightMostLexemIndex()
        throws CGException;
}