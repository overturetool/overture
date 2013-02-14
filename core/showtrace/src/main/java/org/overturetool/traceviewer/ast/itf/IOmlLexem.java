// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:54
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IOmlLexem.java

package org.overturetool.traceviewer.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.ast.itf:
//            IOmlVisitor

public interface IOmlLexem
{

    public abstract Long getLine()
        throws CGException;

    public abstract Long getColumn()
        throws CGException;

    public abstract Long getLexval()
        throws CGException;

    public abstract String getText()
        throws CGException;

    public abstract Long getIndex()
        throws CGException;

    public abstract Long getType()
        throws CGException;

    public abstract Boolean isReservedWord()
        throws CGException;

    public abstract Boolean isIdentifier()
        throws CGException;

    public abstract Boolean isLineComment()
        throws CGException;

    public abstract Boolean isBlockComment()
        throws CGException;

    public abstract void accept(IOmlVisitor iomlvisitor)
        throws CGException;
}